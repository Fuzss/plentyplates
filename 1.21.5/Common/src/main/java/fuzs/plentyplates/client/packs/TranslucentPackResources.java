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
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TranslucentPackResources extends AbstractModPackResources {
    private static final Map<ResourceLocation, ResourceLocation> TRANSLUCENT_LOCATIONS = Stream.of(SensitivityMaterial.values())
            .collect(ImmutableMap.<SensitivityMaterial, ResourceLocation, ResourceLocation>toImmutableMap(
                    SensitivityMaterial::getFullTranslucentTextureLocation,
                    SensitivityMaterial::getFullTextureLocation));

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        if (packType == PackType.SERVER_DATA) {
            return null;
        } else if (TRANSLUCENT_LOCATIONS.containsKey(location)) {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            return () -> makeImageTranslucent(resourceManager, location);
        } else {
            return null;
        }
    }

    private static ByteArrayInputStream makeImageTranslucent(ResourceManager resourceManager, ResourceLocation location) throws IOException {
        try (InputStream inputStream = resourceManager.open(TRANSLUCENT_LOCATIONS.get(location)); NativeImage nativeImage = NativeImage.read(
                inputStream)) {
            for (int x = 0; x < nativeImage.getWidth(); x++) {
                for (int y = 0; y < nativeImage.getHeight(); y++) {
                    int pixel = nativeImage.getPixel(x, y);
                    int alpha = ARGB.alpha(pixel);
                    if (alpha != 0) {
                        nativeImage.setPixel(x, y, ARGB.color((int) (alpha * 0.25F), pixel));
                    }
                }
            }

            return new ByteArrayInputStream(NativeImageHelper.asByteArray(nativeImage));
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return TRANSLUCENT_LOCATIONS.keySet()
                .stream()
                .map(ResourceLocation::getNamespace)
                .distinct()
                .collect(Collectors.toSet());
    }
}
