package fuzs.plentyplates.data;

import com.google.common.base.Preconditions;
import fuzs.plentyplates.client.PlentyPlatesForgeClient;
import fuzs.plentyplates.init.ModRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    private final BlockModelProvider blockModels;

    public ModBlockStateProvider(DataGenerator dataGenerator, String modId, ExistingFileHelper fileHelper) {
        super(dataGenerator, modId, fileHelper);
        this.blockModels = new UncheckedBlockModelProvider(dataGenerator, modId, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.pressurePlateBlock((PressurePlateBlock) ModRegistry.PRESSURE_PLATE_BLOCK.get(), PlentyPlatesForgeClient.MODEL_ID);
        this.itemModels().withExistingParent(this.name(ModRegistry.PRESSURE_PLATE_BLOCK.get()), this.extendKey(ModRegistry.PRESSURE_PLATE_BLOCK.get(), ModelProvider.BLOCK_FOLDER));
//        this.itemModels().pressurePlate()
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

    private ResourceLocation extend(ResourceLocation rl, String suffix) {
        return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
    }

    public BlockModelProvider models() {
        return this.blockModels;
    }

//    private String name(Block block) {
//        return this.key(block).getPath();
//    }
//
//    private ResourceLocation key(Block block) {
//        return ForgeRegistries.BLOCKS.getKey(block);
//    }
//
//    public void pressurePlateBlock(PressurePlateBlock block, ResourceLocation texture) {
//        ModelFile pressurePlate = this.models().pressurePlate(this.name(block), texture);
//        ModelFile pressurePlateDown = this.models().pressurePlateDown(this.name(block) + "_down", texture);
//        this.pressurePlateBlock(block, pressurePlate, pressurePlateDown);
//    }
//
//    public void pressurePlateBlock(PressurePlateBlock block, ModelFile pressurePlate, ModelFile pressurePlateDown) {
//        this.getVariantBuilder(block)
//                .partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(pressurePlateDown))
//                .partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(pressurePlate));
//    }

    private static class UncheckedBlockModelProvider extends BlockModelProvider {
        protected final Function<ResourceLocation, BlockModelBuilder> factory;

        public UncheckedBlockModelProvider(DataGenerator dataGenerator, String modId, ExistingFileHelper fileHelper) {
            super(dataGenerator, modId, fileHelper);
            this.factory = resourceLocation -> new UncheckedBlockModelBuilder(resourceLocation, fileHelper);
        }

        public void run(CachedOutput output) throws IOException {

        }

        @Override
        protected void registerModels() {

        }

        @Override
        public String getName() {
            return "Block Models: " + this.modid;
        }

        public BlockModelBuilder getBuilder(String path) {
            Preconditions.checkNotNull(path, "Path must not be null");
            ResourceLocation outputLoc = this.extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(this.modid, path));
            this.existingFileHelper.trackGenerated(outputLoc, MODEL);
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
        protected static final ExistingFileHelper.ResourceType TEXTURE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");

        public UncheckedBlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
            super(outputLocation, existingFileHelper);
        }

        public UncheckedBlockModelBuilder texture(String key, ResourceLocation texture) {
            Preconditions.checkNotNull(key, "Key must not be null");
            Preconditions.checkNotNull(texture, "Texture must not be null");
//            Preconditions.checkArgument(this.existingFileHelper.exists(texture, TEXTURE),
//                    "Texture %s does not exist in any known resource pack", texture);
            this.textures.put(key, texture.toString());
            return this;
        }
    }
}
