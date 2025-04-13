package fuzs.plentyplates.neoforge.data.client;

import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.neoforge.api.client.data.v2.AbstractAtlasProvider;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;

import java.util.Optional;

public class ModAtlasProvider extends AbstractAtlasProvider {

    public ModAtlasProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addAtlases() {
        SourceList atlas = this.atlas(BLOCKS_ATLAS);
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            atlas.addSource(new SingleFile(material.getTranslucentTextureLocation(), Optional.empty()));
        }
    }
}
