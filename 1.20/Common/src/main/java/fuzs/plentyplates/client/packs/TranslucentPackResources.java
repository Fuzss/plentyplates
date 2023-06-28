package fuzs.plentyplates.client.packs;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.NativeImage;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.resources.v1.AbstractModPackResources;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class TranslucentPackResources extends AbstractModPackResources {
    private static final Map<ResourceLocation, ResourceLocation> TRANSLUCENT_LOCATIONS = Stream.of(SensitivityMaterial.values())
            .collect(ImmutableMap.<SensitivityMaterial, ResourceLocation, ResourceLocation>toImmutableMap(SensitivityMaterial::getTranslucentTextureFile, SensitivityMaterial::getTextureFile));

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        if (packType == PackType.SERVER_DATA) return null;
        if (TRANSLUCENT_LOCATIONS.containsKey(location)) {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            return () -> makeImageTranslucent(resourceManager, location);
        }
        return null;
    }

    private static ByteArrayInputStream makeImageTranslucent(ResourceManager resourceManager, ResourceLocation location) throws IOException {
        try (InputStream inputStream = resourceManager.open(TRANSLUCENT_LOCATIONS.get(location)); NativeImage image = NativeImage.read(inputStream)) {
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int pixel = image.getPixelRGBA(x, y);
                    int alpha = getA(pixel);
                    if (alpha != 0) {
                        image.setPixelRGBA(x, y, pixel & 0xFFFFFF | (int) (alpha * 0.25) << 24);
                    }
                }
            }
            return new ByteArrayInputStream(image.asByteArray());
        }
    }

    public static int getA(int abgrColor) {
        return abgrColor >> 24 & 255;
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return TRANSLUCENT_LOCATIONS.keySet().stream().map(ResourceLocation::getNamespace).distinct().collect(ImmutableSet.toImmutableSet());
    }
}
