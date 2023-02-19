package fuzs.plentyplates.init;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.puzzleslib.core.CommonAbstractions;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.init.RegistryReference;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    public static final CreativeModeTab CREATIVE_MODE_TAB = CommonAbstractions.INSTANCE.creativeModeTab(PlentyPlates.MOD_ID, () -> new ItemStack(Items.ACACIA_PRESSURE_PLATE));
    private static final RegistryManager REGISTRY = CommonFactories.INSTANCE.registration(PlentyPlates.MOD_ID);
    public static final RegistryReference<Block> PRESSURE_PLATE_BLOCK = REGISTRY.registerBlockWithItem("pressure_plate", () -> new DirectionalPressurePlateBlock(DirectionalPressurePlateBlock.SensitivityMaterial.OBSIDIAN, BlockBehaviour.Properties.copy(Blocks.STONE_PRESSURE_PLATE).lightLevel(state -> {
        return state.getValue(DirectionalPressurePlateBlock.LIT) ? 15 : 0;
    })), CreativeModeTab.TAB_REDSTONE);

    public static void touch() {

    }
}
