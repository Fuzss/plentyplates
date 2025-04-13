package fuzs.plentyplates.data.client;

import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.client.data.v2.AbstractAtlasProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.resources.model.AtlasIds;

import java.util.Arrays;

public class ModAtlasProvider extends AbstractAtlasProvider {

    public ModAtlasProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addAtlases() {
        this.add(AtlasIds.BLOCKS,
                Arrays.stream(SensitivityMaterial.values())
                        .map(SensitivityMaterial::getTranslucentTextureLocation)
                        .map(SingleFile::new)
                        .toArray(SingleFile[]::new));
    }
}
