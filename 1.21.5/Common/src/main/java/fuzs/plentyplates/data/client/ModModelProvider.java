package fuzs.plentyplates.data.client;

import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ModModelProvider extends AbstractModelProvider {

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
        blockModelGenerators.blockStateOutput.accept(createPressurePlateWithFacing(block,
                upModelLocation,
                downModelLocation,
                upShroudedModelLocation,
                downShroudedModelLocation));
    }

    public static BlockStateGenerator createPressurePlateWithFacing(Block block, ResourceLocation upModelLocation, ResourceLocation downModelLocation, ResourceLocation upShroudedModelLocation, ResourceLocation downShroudedModelLocation) {
        return MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.properties(DirectionalPressurePlateBlock.POWERED,
                                DirectionalPressurePlateBlock.SHROUDED)
                        .select(false, false, Variant.variant().with(VariantProperties.MODEL, upModelLocation))
                        .select(false, true, Variant.variant().with(VariantProperties.MODEL, upShroudedModelLocation))
                        .select(true, false, Variant.variant().with(VariantProperties.MODEL, downModelLocation))
                        .select(true, true, Variant.variant().with(VariantProperties.MODEL, downShroudedModelLocation)))
                .with(createColumnWithFacing());
    }

    /**
     * Similar to {@link BlockModelGenerators#createColumnWithFacing()}, but horizontal direction textures line up with
     * simple cube model.
     */
    public static PropertyDispatch createColumnWithFacing() {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.DOWN,
                        Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(Direction.UP, Variant.variant())
                .select(Direction.SOUTH,
                        Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                .select(Direction.NORTH,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                .select(Direction.EAST,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                .select(Direction.WEST,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
    }
}
