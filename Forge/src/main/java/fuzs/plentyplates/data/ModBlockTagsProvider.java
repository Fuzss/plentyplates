package fuzs.plentyplates.data;

import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(DataGenerator dataGenerator, String modId, @Nullable ExistingFileHelper fileHelper) {
        super(dataGenerator, modId, fileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(BlockTags.STONE_PRESSURE_PLATES).add(SensitivityMaterial.allBlocks());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(SensitivityMaterial.allBlocks());
    }
}
