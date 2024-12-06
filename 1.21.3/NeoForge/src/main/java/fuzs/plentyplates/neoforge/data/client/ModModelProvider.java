package fuzs.plentyplates.neoforge.data.client;

import com.google.common.base.Preconditions;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.client.AbstractModelProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ModModelProvider extends AbstractModelProvider {
    private final BlockModelProvider blockModels;

    public ModModelProvider(NeoForgeDataProviderContext context) {
        super(context);
        this.blockModels = new UncheckedBlockModelProvider(context.getPackOutput(), context.getModId(), context.getFileHelper());
    }

    @Override
    protected void registerStatesAndModels() {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            this.pressurePlateBlock((PressurePlateBlock) material.getPressurePlateBlock(), material.getModelTexture(), material.getTranslucentModelTexture());
            this.itemModels().withExistingParent(this.name(material.getPressurePlateBlock()), this.extendKey(material.getPressurePlateBlock(), ModelProvider.BLOCK_FOLDER));
        }
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

        public UncheckedBlockModelProvider(PackOutput packOutput, String modId, ExistingFileHelper fileHelper) {
            super(packOutput, modId, fileHelper);
            this.factory = resourceLocation -> new UncheckedBlockModelBuilder(resourceLocation, fileHelper);
        }

        @Override
        public CompletableFuture<?> run(CachedOutput output) {
            return CompletableFuture.allOf();
        }

        @Override
        protected void registerModels() {

        }

        @Override
        public BlockModelBuilder getBuilder(String path) {
            Preconditions.checkNotNull(path, "Path must not be null");
            ResourceLocation outputLoc = this.extendWithFolder(path.contains(":") ? ResourceLocationHelper.withDefaultNamespace(path) : ResourceLocationHelper.fromNamespaceAndPath(this.modid, path));
            this.existingFileHelper.trackGenerated(outputLoc, MODEL);
            // replace with our custom factory
            return this.generatedModels.computeIfAbsent(outputLoc, this.factory);
        }

        private ResourceLocation extendWithFolder(ResourceLocation rl) {
            if (rl.getPath().contains("/")) {
                return rl;
            }
            return ResourceLocationHelper.fromNamespaceAndPath(rl.getNamespace(), this.folder + "/" + rl.getPath());
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
