package fuzs.plentyplates.data;

import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.data.v1.AbstractSpriteSourceProvider;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

import java.util.Optional;

public class ModSpriteSourceProvider extends AbstractSpriteSourceProvider {

    public ModSpriteSourceProvider(PackOutput packOutput, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, modId, fileHelper);
    }

    @Override
    protected void addSources() {
        SourceList atlas = this.atlas(SpriteSourceProvider.BLOCKS_ATLAS);
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            atlas.addSource(new SingleFile(material.getTranslucentModelTexture(), Optional.empty()));
        }
    }
}
