package fuzs.plentyplates.client;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.NativeImage;
import fuzs.plentyplates.PlentyPlates;
import fuzs.puzzleslib.client.core.ClientFactories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = PlentyPlates.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PlentyPlatesForgeClient {
    public static final ResourceLocation MODEL_ID = PlentyPlates.id("block/transparent_obsidian");
    public static final ResourceLocation ID = PlentyPlates.id("textures/block/transparent_obsidian.png");
    public static final ResourceLocation ID2 = new ResourceLocation("textures/block/obsidian.png");

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientFactories.INSTANCE.clientModConstructor(PlentyPlates.MOD_ID).accept(new PlentyPlatesClient());
    }

    @SubscribeEvent
    public static void onAddPackFinders(final AddPackFindersEvent evt) {
        if (evt.getPackType() == PackType.CLIENT_RESOURCES) {
            evt.addRepositorySource(new RepositorySource() {
                @Override
                public void loadPacks(Consumer<Pack> packConsumer, Pack.PackConstructor packConstructor) {
                    Pack pack = Pack.create(PlentyPlates.MOD_ID, true, () -> {
                        return new PackResources() {
                            @Nullable
                            @Override
                            public InputStream getRootResource(String fileName) throws IOException {
                                throw new FileNotFoundException("\"" + fileName + "\" in Fabric mod resource pack");
                            }

                            @Override
                            public InputStream getResource(PackType pType, ResourceLocation pLocation) throws IOException {
                                if (pLocation.equals(ID)) {
                                    VanillaPackResources vanillaPack = Minecraft.getInstance().getClientPackSource().getVanillaPack();
                                    try (InputStream resource = vanillaPack.getResource(pType, ID2); NativeImage image = NativeImage.read(resource)) {
                                        for (int x = 0; x < image.getWidth(); x++) {
                                            for (int y = 0; y < image.getHeight(); y++) {
                                                int pixel = image.getPixelRGBA(x, y);
                                                int alpha = NativeImage.getA(pixel);
                                                if (alpha != 0) {
                                                    image.setPixelRGBA(x, y, pixel & 0xFFFFFF | (int) (alpha * 0.25) << 24);
                                                }
                                            }
                                        }
                                        return new ByteArrayInputStream(image.asByteArray());
                                    }
                                }
                                return null;
                            }

                            @Override
                            public Collection<ResourceLocation> getResources(PackType pType, String pNamespace, String pPath, Predicate<ResourceLocation> pFilter) {
                                return Collections.emptyList();
                            }

                            @Override
                            public boolean hasResource(PackType pType, ResourceLocation pLocation) {
                                return pLocation.equals(ID);
                            }

                            @Override
                            public Set<String> getNamespaces(PackType pType) {
                                return ImmutableSet.of(PlentyPlates.MOD_ID);
                            }

                            @Nullable
                            @Override
                            public <T> T getMetadataSection(MetadataSectionSerializer<T> pDeserializer) throws IOException {
                                if (pDeserializer == PackMetadataSection.SERIALIZER)
                                return (T) ClientPackSource.BUILT_IN;
                                return null;
                            }

                            @Override
                            public String getName() {
                                return PlentyPlates.MOD_NAME;
                            }

                            @Override
                            public void close() {

                            }
                        };
                    }, packConstructor, Pack.Position.TOP, PackSource.BUILT_IN);
                    if (pack != null) {
                        packConsumer.accept(pack);
                    }
                }
            });
        }
    }
}
