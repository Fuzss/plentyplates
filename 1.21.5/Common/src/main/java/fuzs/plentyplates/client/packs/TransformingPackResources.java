package fuzs.plentyplates.client.packs;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.NativeImage;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.client.packs.v1.NativeImageHelper;
import fuzs.puzzleslib.api.resources.v1.AbstractModPackResources;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class TransformingPackResources extends AbstractModPackResources {
    private static final Map<ResourceLocation, ResourceLocation> RESOURCE_LOCATIONS;

    private final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

    static {
        ImmutableMap.Builder<ResourceLocation, ResourceLocation> builder = ImmutableMap.builder();
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            registerTextureMapping(builder::put,
                    material.getFullTextureLocation(),
                    material.getFullTranslucentTextureLocation());
        }
        RESOURCE_LOCATIONS = builder.build();
    }

    protected static void registerTextureMapping(BiConsumer<ResourceLocation, ResourceLocation> consumer, ResourceLocation originalResourceLocation, ResourceLocation providedResourceLocation) {
        consumer.accept(providedResourceLocation, originalResourceLocation);
        consumer.accept(providedResourceLocation.withSuffix(".mcmeta"), originalResourceLocation.withSuffix(".mcmeta"));
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType packType, ResourceLocation resourceLocation) {
        if (RESOURCE_LOCATIONS.containsKey(resourceLocation)) {
            Optional<Resource> optional = this.resourceManager.getResource(RESOURCE_LOCATIONS.get(resourceLocation));
            if (optional.isPresent()) {
                if (resourceLocation.getPath().endsWith(".png")) {
                    try (NativeImage nativeImage = NativeImage.read(optional.get().open())) {
                        for (int x = 0; x < nativeImage.getWidth(); x++) {
                            for (int y = 0; y < nativeImage.getHeight(); y++) {
                                int pixel = nativeImage.getPixel(x, y);
                                int alpha = ARGB.alpha(pixel);
                                if (alpha != 0) {
                                    nativeImage.setPixel(x, y, this.transformPixel(pixel));
                                }
                            }
                        }
                        byte[] byteArray = NativeImageHelper.asByteArray(nativeImage);
                        return () -> new ByteArrayInputStream(byteArray);
                    } catch (IOException ignored) {
                        // NO-OP
                    }
                }

                return optional.get()::open;
            } else {
                return null;
            }
        } else {
            return super.getResource(packType, resourceLocation);
        }
    }

    protected int transformPixel(int pixel) {
        return ARGB.color((int) (ARGB.alpha(pixel) * 0.25F), pixel);
    }
}
