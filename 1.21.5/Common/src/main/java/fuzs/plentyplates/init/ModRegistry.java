package fuzs.plentyplates.init;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.plentyplates.world.level.block.entity.PressurePlateBlockEntity;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Arrays;
import java.util.HashSet;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(PlentyPlates.MOD_ID);
    public static final Holder.Reference<CreativeModeTab> CREATIVE_MODE_TAB;
    public static final Holder.Reference<BlockEntityType<PressurePlateBlockEntity>> PRESSURE_PLATE_BLOCK_ENTITY_TYPE;

    public static void bootstrap() {
        // NO-OP
    }

    static {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            Holder.Reference<Block> block = REGISTRIES.registerBlock(material.id().getPath(),
                    (BlockBehaviour.Properties properties) -> new DirectionalPressurePlateBlock(material, properties),
                    () -> pressurePlateProperties(material));
            REGISTRIES.registerBlockItem(block);
            REGISTRIES.registerMenuType(material.id().getPath(), () -> PressurePlateMenu.create(material));
        }
        CREATIVE_MODE_TAB = REGISTRIES.registerCreativeModeTab(() -> new ItemStack(SensitivityMaterial.OBSIDIAN.getPressurePlateBlock()));
        PRESSURE_PLATE_BLOCK_ENTITY_TYPE = REGISTRIES.registerBlockEntityType("pressure_plate",
                PressurePlateBlockEntity::new,
                () -> new HashSet<>(Arrays.asList(SensitivityMaterial.allBlocks())));
    }

    private static BlockBehaviour.Properties pressurePlateProperties(SensitivityMaterial material) {
        return BlockBehaviour.Properties.ofFullCopy(material.getMaterialBlock())
                .noCollission()
                .lightLevel(Blocks.litBlockEmission(15));
    }
}
