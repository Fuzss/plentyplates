package fuzs.plentyplates.init;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.plentyplates.world.level.block.entity.PressurePlateBlockEntity;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    private static final RegistryManager REGISTRY = RegistryManager.instant(PlentyPlates.MOD_ID);
    public static final RegistryReference<BlockEntityType<PressurePlateBlockEntity>> PRESSURE_PLATE_BLOCK_ENTITY_TYPE;

    public static void touch() {

    }

    static {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            RegistryReference<Block> block = REGISTRY.registerBlock(material.id().getPath(), () -> new DirectionalPressurePlateBlock(material, BlockBehaviour.Properties.copy(material.getMaterialBlock()).noCollission().lightLevel(state -> {
                return state.getValue(DirectionalPressurePlateBlock.LIT) ? 15 : 0;
            })));
            REGISTRY.registerBlockItem(block);
            REGISTRY.registerMenuType(material.id().getPath(), () -> PressurePlateMenu.create(material));
        }
        PRESSURE_PLATE_BLOCK_ENTITY_TYPE = REGISTRY.registerBlockEntityType("pressure_plate", () -> BlockEntityType.Builder.of(PressurePlateBlockEntity::new, SensitivityMaterial.allBlocks()));
    }
}
