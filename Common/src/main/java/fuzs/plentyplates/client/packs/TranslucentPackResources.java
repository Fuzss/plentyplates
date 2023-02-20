package fuzs.plentyplates.client.packs;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.NativeImage;
import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TranslucentPackResources implements PackResources {
    public static final PackMetadataSection METADATA_SECTION = new PackMetadataSection(Component.literal("Provides translucent textures for " + PlentyPlates.MOD_NAME), PackType.CLIENT_RESOURCES.getVersion(SharedConstants.getCurrentVersion()));

    private final Map<ResourceLocation, ResourceLocation> translucentLocations = Stream.of(SensitivityMaterial.values()).collect(ImmutableMap.<SensitivityMaterial, ResourceLocation, ResourceLocation>toImmutableMap(SensitivityMaterial::getTranslucentTextureFile, SensitivityMaterial::getTextureFile));

    @Nullable
    @Override
    public InputStream getRootResource(String fileName) throws IOException {
        throw new FileNotFoundException(fileName);
    }

    @Override
    public InputStream getResource(PackType type, ResourceLocation location) throws IOException {
        if (type != PackType.SERVER_DATA) {
            if (this.translucentLocations.containsKey(location)) {
                ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
                return makeImageTranslucent(resourceManager.open(this.translucentLocations.get(location)));
            }
        }
        return null;
    }

    private static ByteArrayInputStream makeImageTranslucent(InputStream inputStream) throws IOException {
        try (InputStream resource = inputStream; NativeImage image = NativeImage.read(resource)) {
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

    @Override
    public Collection<ResourceLocation> getResources(PackType pType, String pNamespace, String pPath, Predicate<ResourceLocation> pFilter) {
        return Collections.emptyList();
    }

    @Override
    public boolean hasResource(PackType type, ResourceLocation location) {
        return this.translucentLocations.containsKey(location);
    }

    @Override
    public Set<String> getNamespaces(PackType pType) {
        return this.translucentLocations.keySet().stream().map(ResourceLocation::getNamespace).distinct().collect(ImmutableSet.toImmutableSet());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
        return serializer == PackMetadataSection.SERIALIZER ? (T) METADATA_SECTION : null;
    }

    @Override
    public String getName() {
        return PlentyPlates.MOD_NAME;
    }

    @Override
    public void close() {

    }

    // overrides a method patched in on Forge
    public boolean isHidden() {
        return true;
    }
}
