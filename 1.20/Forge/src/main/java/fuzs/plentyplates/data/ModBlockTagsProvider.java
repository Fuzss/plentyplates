package fuzs.plentyplates.data;

import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends AbstractTagProvider.Blocks {

    public ModBlockTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, lookupProvider, modId, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.STONE_PRESSURE_PLATES).add(SensitivityMaterial.allBlocks());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(SensitivityMaterial.allBlocks());
    }
}
