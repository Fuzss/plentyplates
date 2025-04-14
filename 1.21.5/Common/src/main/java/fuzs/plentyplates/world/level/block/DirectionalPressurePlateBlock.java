package fuzs.plentyplates.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.entity.PressurePlateBlockEntity;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import fuzs.puzzleslib.api.util.v1.ShapesHelper;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class DirectionalPressurePlateBlock extends PressurePlateBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final MapCodec<PressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(SensitivityMaterial.CODEC.fieldOf("material").forGetter((pressurePlateBlock) -> {
            return ((DirectionalPressurePlateBlock) pressurePlateBlock).sensitivityMaterial;
        }), propertiesCodec()).apply(instance, DirectionalPressurePlateBlock::new);
    });
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty SHROUDED = BooleanProperty.create("shrouded");
    public static final BooleanProperty SILENT = BooleanProperty.create("silent");
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final VoxelShape SHAPE = Block.column(14.0, 0.0, 1.0);
    private static final VoxelShape SHAPE_PRESSED = Block.column(14.0, 0.0, 0.5);
    private static final Map<Direction, VoxelShape> SHAPES = ShapesHelper.rotate(SHAPE);
    private static final Map<Direction, VoxelShape> PRESSED_SHAPES = ShapesHelper.rotate(SHAPE_PRESSED);
    private static final Map<Direction, AABB> TOUCH_AABBS = ShapesHelper.rotate(Shapes.create(TOUCH_AABB))
            .entrySet()
            .stream()
            .collect(Maps.toImmutableEnumMap(Map.Entry::getKey,
                    (Map.Entry<Direction, VoxelShape> entry) -> entry.getValue().toAabbs().iterator().next()));

    private final SensitivityMaterial sensitivityMaterial;

    public DirectionalPressurePlateBlock(SensitivityMaterial sensitivityMaterial, Properties properties) {
        super(BlockSetType.STONE, properties);
        this.sensitivityMaterial = sensitivityMaterial;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.UP)
                .setValue(SHROUDED, false)
                .setValue(SILENT, false)
                .setValue(LIT, false));
    }

    @Override
    public MapCodec<PressurePlateBlock> codec() {
        return CODEC;
    }

    public SensitivityMaterial getSensitivityMaterial() {
        return this.sensitivityMaterial;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return this.getSignalForState(state) > 0 ? PRESSED_SHAPES.get(facing) : SHAPES.get(facing);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        return level.getBlockState(blockPos).isFaceSturdy(level, blockPos, direction, SupportType.RIGID) ||
                canSupportCenter(level, blockPos, direction);
    }

    @Override
    protected BlockState updateShape(BlockState blockState, LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos blockPos, Direction direction, BlockPos neighborBlockPos, BlockState neighborBlockState, RandomSource randomSource) {
        if (blockState.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }

        return direction == blockState.getValue(FACING).getOpposite() && !blockState.canSurvive(levelReader, blockPos) ?
                Blocks.AIR.defaultBlockState() : blockState;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState()
                .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
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
            level.playSound(null, pos, BlockSetType.STONE.pressurePlateClickOn(), SoundSource.BLOCKS);
        }
    }

    protected void playOffSound(LevelAccessor level, BlockPos pos) {
        if (!level.getBlockState(pos).getValue(SILENT)) {
            level.playSound(null, pos, BlockSetType.STONE.pressurePlateClickOff(), SoundSource.BLOCKS);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int i = this.getSignalForState(state);
        if (i > 0) {
            this.checkPressed(null, level, pos, state, i);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier) {
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
        AABB aABB = TOUCH_AABBS.get(state.getValue(FACING)).move(pos);
        List<? extends Entity> entities = level.getEntitiesOfClass(this.sensitivityMaterial.getEntityClass(),
                aABB,
                EntitySelector.NO_SPECTATORS.and(Predicate.not(Entity::isIgnoringBlockTriggers)).and(entity -> {
                    if (!level.isClientSide &&
                            level.getBlockEntity(pos) instanceof PressurePlateBlockEntity blockEntity) {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PressurePlateBlockEntity(this.sensitivityMaterial, pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof PressurePlateBlockEntity blockEntity) {
                if (blockEntity.allowedToAccess(player)) {
                    ProxyImpl.get().openMenu(player, blockEntity, () -> {
                        return new PressurePlateMenu.Data(this.sensitivityMaterial,
                                blockEntity.getAllowedValues(),
                                blockEntity.getCurrentValues());
                    });
                }
            }

            return InteractionResultHelper.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }
}
