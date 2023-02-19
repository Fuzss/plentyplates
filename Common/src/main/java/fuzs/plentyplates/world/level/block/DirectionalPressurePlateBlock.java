package fuzs.plentyplates.world.level.block;

import com.google.common.collect.Maps;
import fuzs.plentyplates.world.phys.shapes.VoxelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class DirectionalPressurePlateBlock extends PressurePlateBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final Vec3[] SHAPE_VECTORS = VoxelUtils.makeVectors(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
    private static final Vec3[] PRESSED_SHAPE_VECTORS = VoxelUtils.makeVectors(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
    protected static final Vec3[] TOUCH_VECTORS = VoxelUtils.makeVectors(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    private final Map<Direction, VoxelShape> shapes = Stream.of(Direction.values()).collect(Maps.<Direction, Direction, VoxelShape>toImmutableEnumMap(Function.identity(), direction -> {
        return VoxelUtils.makeCombinedShape(VoxelUtils.rotate(direction, SHAPE_VECTORS));
    }));
    private final Map<Direction, VoxelShape> pressedShapes = Stream.of(Direction.values()).collect(Maps.<Direction, Direction, VoxelShape>toImmutableEnumMap(Function.identity(), direction -> {
        return VoxelUtils.makeCombinedShape(VoxelUtils.rotate(direction, PRESSED_SHAPE_VECTORS));
    }));
    private final Map<Direction, AABB> touchAABBs = Stream.of(Direction.values()).collect(Maps.<Direction, Direction, AABB>toImmutableEnumMap(Function.identity(), direction -> {
        return VoxelUtils.makeCombinedShape(VoxelUtils.rotate(direction, TOUCH_VECTORS)).toAabbs().stream().findAny().orElse(TOUCH_AABB);
    }));

    private final Sensitivity sensitivity;

    public DirectionalPressurePlateBlock(Sensitivity sensitivity, Properties properties) {
        super(sensitivity, properties);
        this.sensitivity = sensitivity;
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(FACING, Direction.UP));
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
        return level.getBlockState(blockPos).isFaceSturdy(level, blockPos, direction, SupportType.RIGID) ||  canSupportCenter(level, blockPos, direction);
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

    @Override
    protected void playOnSound(LevelAccessor level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
    }

    @Override
    protected void playOffSound(LevelAccessor level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.5F);
    }

    @Override
    protected int getSignalStrength(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        AABB aABB = this.touchAABBs.get(state.getValue(FACING)).move(pos);
        List<? extends Entity> list;
        switch (this.sensitivity) {
            case EVERYTHING -> list = level.getEntities(null, aABB);
            case MOBS -> list = level.getEntitiesOfClass(LivingEntity.class, aABB);
            default -> {
                return 0;
            }
        }

        if (!list.isEmpty()) {

            for (Entity entity : list) {
                if (!entity.isIgnoringBlockTriggers()) {
                    return 15;
                }
            }
        }

        return 0;
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
        builder.add(WATERLOGGED, FACING);
    }
}
