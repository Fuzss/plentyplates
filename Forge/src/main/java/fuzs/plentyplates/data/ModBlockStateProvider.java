package fuzs.plentyplates.data;

import com.google.common.base.Preconditions;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    private final BlockModelProvider blockModels;

    public ModBlockStateProvider(DataGenerator dataGenerator, String modId, ExistingFileHelper fileHelper) {
        super(dataGenerator, modId, fileHelper);
        this.blockModels = new UncheckedBlockModelProvider(dataGenerator, modId, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            this.pressurePlateBlock((PressurePlateBlock) material.getPressurePlateBlock(), material.getModelTexture(), material.getTranslucentModelTexture());
            this.itemModels().withExistingParent(this.name(material.getPressurePlateBlock()), this.extendKey(material.getPressurePlateBlock(), ModelProvider.BLOCK_FOLDER));
        }
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private String name(Block block) {
        return this.key(block).getPath();
    }

    private ResourceLocation extendKey(Block block, String... extensions) {
        ResourceLocation loc = this.key(block);
        extensions = ArrayUtils.add(extensions, loc.getPath());
        return new ResourceLocation(loc.getNamespace(), String.join("/", extensions));
    }

    @Override
    public BlockModelProvider models() {
        return this.blockModels;
    }

    public void pressurePlateBlock(PressurePlateBlock block, ResourceLocation texture, ResourceLocation transparentTexture) {
        ModelFile pressurePlate = this.models().pressurePlate(this.name(block), texture);
        ModelFile pressurePlateDown = this.models().pressurePlateDown(this.name(block) + "_down", texture);
        ModelFile shroudedPressurePlate = this.models().pressurePlate("shrouded_" + this.name(block), transparentTexture);
        ModelFile shroudedPressurePlateDown = this.models().pressurePlateDown("shrouded_" + this.name(block) + "_down", transparentTexture);
        this.pressurePlateBlock(block, pressurePlate, pressurePlateDown, shroudedPressurePlate, shroudedPressurePlateDown);
    }

    public void pressurePlateBlock(PressurePlateBlock block, ModelFile pressurePlate, ModelFile pressurePlateDown, ModelFile shroudedPressurePlate, ModelFile shroudedPressurePlateDown) {
        this.getVariantBuilder(block).forAllStatesExcept(state -> {
            Direction facing = state.getValue(DirectionalPressurePlateBlock.FACING);
            boolean powered = state.getValue(DirectionalPressurePlateBlock.POWERED);
            boolean shrouded = state.getValue(DirectionalPressurePlateBlock.SHROUDED);
            ModelFile modelFile;
            if (shrouded) {
                modelFile = powered ? shroudedPressurePlateDown : shroudedPressurePlate;
            } else {
                modelFile = powered ? pressurePlateDown : pressurePlate;
            }
            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .rotationX(facing == Direction.DOWN ? 180 : facing.getAxis().isHorizontal() ? 90 : 0)
                    .rotationY(facing.getAxis().isVertical() ? 0 : (((int) facing.toYRot()) + 180) % 360)
                    .build();
        }, DirectionalPressurePlateBlock.WATERLOGGED, DirectionalPressurePlateBlock.SILENT, DirectionalPressurePlateBlock.LIT);
    }

    private static class UncheckedBlockModelProvider extends BlockModelProvider {
        protected final Function<ResourceLocation, BlockModelBuilder> factory;

        public UncheckedBlockModelProvider(DataGenerator dataGenerator, String modId, ExistingFileHelper fileHelper) {
            super(dataGenerator, modId, fileHelper);
            this.factory = resourceLocation -> new UncheckedBlockModelBuilder(resourceLocation, fileHelper);
        }

        @Override
        public void run(CachedOutput output) {

        }

        @Override
        protected void registerModels() {

        }

        @Override
        public BlockModelBuilder getBuilder(String path) {
            Preconditions.checkNotNull(path, "Path must not be null");
            ResourceLocation outputLoc = this.extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(this.modid, path));
            this.existingFileHelper.trackGenerated(outputLoc, MODEL);
            // replace with our custom factory
            return this.generatedModels.computeIfAbsent(outputLoc, this.factory);
        }

        private ResourceLocation extendWithFolder(ResourceLocation rl) {
            if (rl.getPath().contains("/")) {
                return rl;
            }
            return new ResourceLocation(rl.getNamespace(), this.folder + "/" + rl.getPath());
        }
    }

    private static class UncheckedBlockModelBuilder extends BlockModelBuilder {

        public UncheckedBlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
            super(outputLocation, existingFileHelper);
        }

        @Override
        public UncheckedBlockModelBuilder texture(String key, ResourceLocation texture) {
            Preconditions.checkNotNull(key, "Key must not be null");
            Preconditions.checkNotNull(texture, "Texture must not be null");
            // removed check for texture file here since we generate that at runtime
            this.textures.put(key, texture.toString());
            return this;
        }
    }
}
