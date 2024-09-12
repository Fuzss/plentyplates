package fuzs.plentyplates.init;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.plentyplates.world.level.block.entity.PressurePlateBlockEntity;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(PlentyPlates.MOD_ID);
    public static final Holder.Reference<BlockEntityType<PressurePlateBlockEntity>> PRESSURE_PLATE_BLOCK_ENTITY_TYPE;

    public static void touch() {
        // NO-OP
    }

    static {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            Holder.Reference<Block> block = REGISTRIES.registerBlock(material.id().getPath(),
                    () -> new DirectionalPressurePlateBlock(material, BlockBehaviour.Properties.ofFullCopy(
                            material.getMaterialBlock()).noCollission().lightLevel(state -> {
                        return state.getValue(DirectionalPressurePlateBlock.LIT) ? 15 : 0;
                    }))
            );
            REGISTRIES.registerBlockItem(block);
            REGISTRIES.registerMenuType(material.id().getPath(), () -> PressurePlateMenu.create(material));
        }
        PRESSURE_PLATE_BLOCK_ENTITY_TYPE = REGISTRIES.registerBlockEntityType("pressure_plate",
                () -> BlockEntityType.Builder.of(PressurePlateBlockEntity::new, SensitivityMaterial.allBlocks())
        );
    }
}
