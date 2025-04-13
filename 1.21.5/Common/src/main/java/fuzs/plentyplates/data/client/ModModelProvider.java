package fuzs.plentyplates.data.client;

import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ModModelProvider extends AbstractModelProvider {
    /**
     * Rotations, so that horizontal direction textures line up with simple cube model.
     *
     * @see BlockModelGenerators#ROTATIONS_COLUMN_WITH_FACING
     */
    public static final PropertyDispatch<VariantMutator> ROTATIONS_COLUMN_WITH_FACING = PropertyDispatch.modify(
                    BlockStateProperties.FACING)
            .select(Direction.DOWN, BlockModelGenerators.X_ROT_180)
            .select(Direction.UP, BlockModelGenerators.NOP)
            .select(Direction.NORTH, BlockModelGenerators.X_ROT_270.then(BlockModelGenerators.Y_ROT_180))
            .select(Direction.SOUTH, BlockModelGenerators.X_ROT_270)
            .select(Direction.WEST, BlockModelGenerators.X_ROT_270.then(BlockModelGenerators.Y_ROT_90))
            .select(Direction.EAST, BlockModelGenerators.X_ROT_270.then(BlockModelGenerators.Y_ROT_270));

    public ModModelProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addBlockModels(BlockModelGenerators blockModelGenerators) {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            this.pressurePlate(material.getPressurePlateBlock(),
                    material.getTextureLocation(),
                    material.getTranslucentTextureLocation(),
                    blockModelGenerators);
        }
    }

    public final void pressurePlate(Block block, ResourceLocation textureLocation, ResourceLocation translucentTextureLocation, BlockModelGenerators blockModelGenerators) {
        ResourceLocation upModelLocation = ModelTemplates.PRESSURE_PLATE_UP.create(block,
                TextureMapping.cube(textureLocation),
                blockModelGenerators.modelOutput);
        ResourceLocation downModelLocation = ModelTemplates.PRESSURE_PLATE_DOWN.create(block,
                TextureMapping.cube(textureLocation),
                blockModelGenerators.modelOutput);
        ResourceLocation upShroudedModelLocation = ModelTemplates.PRESSURE_PLATE_UP.createWithSuffix(block,
                "_shrouded",
                TextureMapping.cube(translucentTextureLocation),
                blockModelGenerators.modelOutput);
        ResourceLocation downShroudedModelLocation = ModelTemplates.PRESSURE_PLATE_DOWN.createWithSuffix(block,
                "_shrouded",
                TextureMapping.cube(translucentTextureLocation),
                blockModelGenerators.modelOutput);
        blockModelGenerators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(DirectionalPressurePlateBlock.POWERED,
                                DirectionalPressurePlateBlock.SHROUDED)
                        .select(false, false, BlockModelGenerators.plainVariant(upModelLocation))
                        .select(false, true, BlockModelGenerators.plainVariant(upShroudedModelLocation))
                        .select(true, false, BlockModelGenerators.plainVariant(downModelLocation))
                        .select(true, true, BlockModelGenerators.plainVariant(downShroudedModelLocation)))
                .with(ROTATIONS_COLUMN_WITH_FACING));
    }
}
