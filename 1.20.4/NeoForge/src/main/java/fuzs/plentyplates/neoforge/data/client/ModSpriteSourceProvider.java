package fuzs.plentyplates.neoforge.data.client;

import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.neoforge.api.data.v2.client.AbstractSpriteSourceProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.ForgeDataProviderContext;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;

import java.util.Optional;

public class ModSpriteSourceProvider extends AbstractSpriteSourceProvider {

    public ModSpriteSourceProvider(ForgeDataProviderContext context) {
        super(context);
    }

    @Override
    public void addSpriteSources() {
        SourceList atlas = this.atlas(BLOCKS_ATLAS);
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            atlas.addSource(new SingleFile(material.getTranslucentModelTexture(), Optional.empty()));
        }
    }
}
