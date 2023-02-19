package fuzs.plentyplates.init;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.core.CommonAbstractions;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.init.RegistryManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    public static final CreativeModeTab CREATIVE_MODE_TAB = CommonAbstractions.INSTANCE.creativeModeTab(PlentyPlates.MOD_ID, () -> new ItemStack(SensitivityMaterial.OBSIDIAN.getPressurePlateBlock()));
    private static final RegistryManager REGISTRY = CommonFactories.INSTANCE.registration(PlentyPlates.MOD_ID);

    public static void touch() {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            REGISTRY.registerBlockWithItem(material.id().getPath(), () -> new DirectionalPressurePlateBlock(material, BlockBehaviour.Properties.copy(material.getMaterialBlock()).noCollission().lightLevel(state -> {
                return state.getValue(DirectionalPressurePlateBlock.LIT) ? 15 : 0;
            })), CREATIVE_MODE_TAB);
        }
    }
}
