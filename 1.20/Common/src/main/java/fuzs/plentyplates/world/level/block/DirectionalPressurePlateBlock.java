package fuzs.plentyplates.world.level.block;

import com.google.common.collect.Maps;
import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.networking.ClientboundInitialValuesMessage;
import fuzs.plentyplates.world.level.block.entity.PressurePlateBlockEntity;
import fuzs.plentyplates.world.phys.shapes.VoxelUtils;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.Proxy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class DirectionalPressurePlateBlock extends PressurePlateBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty SHROUDED = BooleanProperty.create("shrouded");
    public static final BooleanProperty SILENT = BooleanProperty.create("silent");
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    protected static final Vec3[] TOUCH_VECTORS = VoxelUtils.makeVectors(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
    private static final Vec3[] SHAPE_VECTORS = VoxelUtils.makeVectors(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
    private static final Vec3[] PRESSED_SHAPE_VECTORS = VoxelUtils.makeVectors(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
    private final Map<Direction, VoxelShape> shapes = Stream.of(Direction.values()).collect(Maps.<Direction, Direction, VoxelShape>toImmutableEnumMap(Function.identity(), direction -> {
        return VoxelUtils.makeCombinedShape(VoxelUtils.rotate(direction, SHAPE_VECTORS));
    }));
    private final Map<Direction, VoxelShape> pressedShapes = Stream.of(Direction.values()).collect(Maps.<Direction, Direction, VoxelShape>toImmutableEnumMap(Function.identity(), direction -> {
        return VoxelUtils.makeCombinedShape(VoxelUtils.rotate(direction, PRESSED_SHAPE_VECTORS));
    }));
    private final Map<Direction, AABB> touchAABBs = Stream.of(Direction.values()).collect(Maps.<Direction, Direction, AABB>toImmutableEnumMap(Function.identity(), direction -> {
        return VoxelUtils.makeCombinedShape(VoxelUtils.rotate(direction, TOUCH_VECTORS)).toAabbs().stream().findAny().orElse(TOUCH_AABB);
    }));

    private final SensitivityMaterial sensitivityMaterial;

    public DirectionalPressurePlateBlock(SensitivityMaterial sensitivityMaterial, Properties properties) {
        super(Sensitivity.EVERYTHING, properties, BlockSetType.STONE);
        this.sensitivityMaterial = sensitivityMaterial;
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(FACING, Direction.UP).setValue(SHROUDED, false).setValue(SILENT, false).setValue(LIT, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return this.getSignalForState(state) > 0 ? this.pressedShapes.get(facing) : this.shapes.get(facing);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        return level.getBlockState(blockPos).isFaceSturdy(level, blockPos, direction, SupportType.RIGID) || canSupportCenter(level, blockPos, direction);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState().setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
        for (Direction direction : context.getNearestLookingDirections()) {
            Direction opposite = direction.getOpposite();
            state = state.setValue(FACING, opposite);
            if (state.canSurvive(level, pos)) {
                return state;
            }
        }
        return null;
    }

    protected void playOnSound(LevelAccessor level, BlockPos pos) {
        if (!level.getBlockState(pos).getValue(SILENT)) {
            level.playSound((Player) null, pos, BlockSetType.STONE.pressurePlateClickOn(), SoundSource.BLOCKS);
        }
    }

    protected void playOffSound(LevelAccessor level, BlockPos pos) {
        if (!level.getBlockState(pos).getValue(SILENT)) {
            level.playSound((Player) null, pos, BlockSetType.STONE.pressurePlateClickOff(), SoundSource.BLOCKS);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int i = this.getSignalForState(state);
        if (i > 0) {
            this.checkPressed((Entity) null, level, pos, state, i);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide) {
            int i = this.getSignalForState(state);
            if (i == 0) {
                this.checkPressed(entity, level, pos, state, i);
            }
        }
    }

    protected void checkPressed(@Nullable Entity entity, Level level, BlockPos pos, BlockState state, int currentSignal) {
        int i = this.getSignalStrength(level, pos);
        boolean bl = currentSignal > 0;
        boolean bl2 = i > 0;
        if (currentSignal != i) {
            BlockState blockState = this.setSignalForState(state, i);
            level.setBlock(pos, blockState, 2);
            this.updateNeighbours(level, pos);
            level.setBlocksDirty(pos, state, blockState);
        }

        if (!bl2 && bl) {
            this.playOffSound(level, pos);
            level.gameEvent(entity, GameEvent.BLOCK_DEACTIVATE, pos);
        } else if (bl2 && !bl) {
            this.playOnSound(level, pos);
            level.gameEvent(entity, GameEvent.BLOCK_ACTIVATE, pos);
        }

        if (bl2) {
            level.scheduleTick(new BlockPos(pos), this, this.getPressedTime());
        }
    }

    @Override
    protected int getSignalStrength(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        AABB aABB = this.touchAABBs.get(state.getValue(FACING)).move(pos);
        List<? extends Entity> entities = level.getEntitiesOfClass(this.sensitivityMaterial.getClazz(), aABB, EntitySelector.NO_SPECTATORS.and(Predicate.not(Entity::isIgnoringBlockTriggers)).and(entity -> {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof PressurePlateBlockEntity blockEntity) {
                return blockEntity.permits(entity);
            }
            return false;
        }));
        return !entities.isEmpty() ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return direction == state.getValue(FACING) ? this.getSignalForState(state) : 0;
    }

    @Override
    protected void updateNeighbours(Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        BlockState state = level.getBlockState(pos);
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, FACING, SHROUDED, SILENT, LIT);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("block.plentyplates.pressure_plate.activated_by", Component.translatable(this.sensitivityMaterial.descriptionKey()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GREEN));
        if (!ModLoaderEnvironment.INSTANCE.isClient()) return;
        Component shiftComponent = Component.empty().append(Proxy.INSTANCE.getKeyMappingComponent("key.sneak")).withStyle(ChatFormatting.LIGHT_PURPLE);
        Component useComponent = Component.empty().append(Proxy.INSTANCE.getKeyMappingComponent("key.use")).withStyle(ChatFormatting.LIGHT_PURPLE);
        tooltip.add(Component.translatable("block.plentyplates.pressure_plate.description", shiftComponent, useComponent).withStyle(ChatFormatting.GRAY));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PressurePlateBlockEntity(this.sensitivityMaterial, pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof PressurePlateBlockEntity blockEntity) {
                if (blockEntity.allowedToAccess(player)) {
                    player.openMenu(blockEntity).ifPresent(containerId -> {
                        PlentyPlates.NETWORKING.sendTo((ServerPlayer) player, new ClientboundInitialValuesMessage(containerId, blockEntity.getAllowedValues(), blockEntity.getCurrentValues()));
                    });
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
