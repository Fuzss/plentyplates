package fuzs.plentyplates.world.phys.shapes;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class VoxelUtils {
    private static final double SCALE = 16.0;
    private static final double SCALED_DOWN = 0.0625;

    public static Vec3[] scale(Vec3[] edges) {
        return Stream.of(edges).map(edge -> edge.scale(SCALED_DOWN)).toArray(Vec3[]::new);
    }

    public static Vec3[] mirrorX(Vec3[] edges) {
        return Stream.of(edges).map(edge -> new Vec3(SCALE - edge.x, edge.y, edge.z)).toArray(Vec3[]::new);
    }

    public static Vec3[] mirrorY(Vec3[] edges) {
        return Stream.of(edges).map(edge -> new Vec3(edge.x, SCALE - edge.y, edge.z)).toArray(Vec3[]::new);
    }

    public static Vec3[] mirrorZ(Vec3[] edges) {
        return Stream.of(edges).map(edge -> new Vec3(edge.x, edge.y, SCALE - edge.z)).toArray(Vec3[]::new);
    }

    public static Vec3[] flipXZ(Vec3[] edges) {
        return Stream.of(edges).map(edge -> new Vec3(edge.z, edge.y, edge.x)).toArray(Vec3[]::new);
    }

    public static Vec3[] flipXY(Vec3[] edges) {
        return Stream.of(edges).map(edge -> new Vec3(edge.y, edge.x, edge.z)).toArray(Vec3[]::new);
    }

    public static Vec3[] flipYZ(Vec3[] edges) {
        return Stream.of(edges).map(edge -> new Vec3(edge.x, edge.z, edge.y)).toArray(Vec3[]::new);
    }

    /**
     * similar to {@link Direction#getRotation()}
     */
    public static Vec3[] rotate(Direction direction, Vec3[] edges) {
        return switch (direction) {
            case DOWN -> mirrorY(edges);
            case UP -> edges;
            case NORTH -> mirrorZ(flipYZ(edges));
            case SOUTH -> flipYZ(edges);
            case EAST -> flipXY(edges);
            case WEST -> mirrorX(flipXY(edges));
        };
    }

    public static VoxelShape makeCombinedShape(Vec3[] corners) {
        if (corners.length == 0 || corners.length % 2 != 0) {
            throw new IllegalArgumentException("Incorrect number of provided corners");
        }
        VoxelShape shape = null;
        for (int i = 0; i < corners.length / 2; i++) {
            int index = 2 * i;
            VoxelShape boxShape = makeBoxShape(corners[index], corners[index + 1]);
            if (i == 0) {
                shape = boxShape;
            } else {
                shape = Shapes.or(shape, boxShape);
            }
        }
        return shape;
    }

    public static VoxelShape makeBoxShape(Vec3 start, Vec3 end) {
        double startX = Math.min(start.x, end.x);
        double startY = Math.min(start.y, end.y);
        double startZ = Math.min(start.z, end.z);
        double endX = Math.max(start.x, end.x);
        double endY = Math.max(start.y, end.y);
        double endZ = Math.max(start.z, end.z);
        return Block.box(startX, startY, startZ, endX, endY, endZ);
    }

    public static Vec3[] makeVectors(double... values) {
        if (values.length % 3 != 0) {
            throw new IllegalArgumentException("Unable to create proper number of vectors");
        }
        Vec3[] array = new Vec3[values.length / 3];
        for (int i = 0; i < array.length; i++) {
            int index = 3 * i;
            array[i] = new Vec3(values[index], values[index + 1], values[index + 2]);
        }
        return array;
    }

    /**
     * @param corners 0-3 for bottom plate, 4 apex
     * @return all edges of a pyramid constructed from corners
     */
    public static Vec3[] makePyramidEdges(Vec3[] corners) {
        if (corners.length != 5) {
            throw new IllegalArgumentException("Constructing a pyramid requires 5 corners");
        }
        return new Vec3[]{
                // bottom plate
                corners[0], corners[1],
                corners[1], corners[2],
                corners[2], corners[3],
                corners[3], corners[0],
                // connections between bottom and apex
                corners[0], corners[4],
                corners[1], corners[4],
                corners[2], corners[4],
                corners[3], corners[4]
        };
    }

    /**
     * @param corners 0-3 for bottom face, 4-7 for top face
     * @return vectors as pairs representing the edges
     */
    public static Vec3[] makeCuboidEdges(Vec3[] corners) {
        if (corners.length != 8) {
            throw new IllegalArgumentException("Constructing a cuboid requires 8 corners");
        }
        return new Vec3[]{
                // bottom face
                corners[0], corners[1],
                corners[1], corners[2],
                corners[2], corners[3],
                corners[3], corners[0],
                // connections between bottom and top
                corners[0], corners[4],
                corners[1], corners[5],
                corners[2], corners[6],
                corners[3], corners[7],
                // top face
                corners[4], corners[5],
                corners[5], corners[6],
                corners[6], corners[7],
                corners[7], corners[4]
        };
    }
}
