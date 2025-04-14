package fuzs.plentyplates.init;

import com.google.common.collect.ImmutableSet;
import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.plentyplates.world.level.block.entity.PressurePlateBlockEntity;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(PlentyPlates.MOD_ID);
    public static final Holder.Reference<Block> OBSIDIAN_PRESSURE_PLATE_BLOCK = registerPressurePlateBlock(
            SensitivityMaterial.OBSIDIAN);
    public static final Holder.Reference<Block> COBBLESTONE_PRESSURE_PLATE_BLOCK = registerPressurePlateBlock(
            SensitivityMaterial.COBBLESTONE);
    public static final Holder.Reference<Block> MOSSY_COBBLESTONE_PRESSURE_PLATE_BLOCK = registerPressurePlateBlock(
            SensitivityMaterial.MOSSY_COBBLESTONE);
    public static final Holder.Reference<Block> STONE_BRICK_PRESSURE_PLATE_BLOCK = registerPressurePlateBlock(
            SensitivityMaterial.STONE_BRICKS);
    public static final Holder.Reference<Block> MOSSY_STONE_BRICK_PRESSURE_PLATE_BLOCK = registerPressurePlateBlock(
            SensitivityMaterial.MOSSY_STONE_BRICKS);
    public static final Holder.Reference<Block> CHISELED_STONE_BRICK_PRESSURE_PLATE_BLOCK = registerPressurePlateBlock(
            SensitivityMaterial.CHISELED_STONE_BRICKS);
    public static final Holder.Reference<Item> OBSIDIAN_PRESSURE_PLATE_ITEM = REGISTRIES.registerBlockItem(
            OBSIDIAN_PRESSURE_PLATE_BLOCK);
    public static final Holder.Reference<Item> COBBLESTONE_PRESSURE_PLATE_ITEM = REGISTRIES.registerBlockItem(
            COBBLESTONE_PRESSURE_PLATE_BLOCK);
    public static final Holder.Reference<Item> MOSSY_COBBLESTONE_PRESSURE_PLATE_ITEM = REGISTRIES.registerBlockItem(
            MOSSY_COBBLESTONE_PRESSURE_PLATE_BLOCK);
    public static final Holder.Reference<Item> STONE_BRICK_PRESSURE_PLATE_ITEM = REGISTRIES.registerBlockItem(
            STONE_BRICK_PRESSURE_PLATE_BLOCK);
    public static final Holder.Reference<Item> MOSSY_STONE_BRICK_PRESSURE_PLATE_ITEM = REGISTRIES.registerBlockItem(
            MOSSY_STONE_BRICK_PRESSURE_PLATE_BLOCK);
    public static final Holder.Reference<Item> CHISELED_STONE_BRICK_PRESSURE_PLATE_ITEM = REGISTRIES.registerBlockItem(
            CHISELED_STONE_BRICK_PRESSURE_PLATE_BLOCK);
    public static final Holder.Reference<BlockEntityType<PressurePlateBlockEntity>> PRESSURE_PLATE_BLOCK_ENTITY_TYPE = REGISTRIES.registerBlockEntityType(
            "pressure_plate",
            PressurePlateBlockEntity::new,
            () -> ImmutableSet.copyOf(SensitivityMaterial.allBlocks()));
    public static final Holder.Reference<MenuType<PressurePlateMenu>> PRESSURE_PLATE_MENU_TYPE = REGISTRIES.registerMenuType(
            "pressure_plate",
            PressurePlateMenu::new,
            PressurePlateMenu.Data.STREAM_CODEC);
    public static final Holder.Reference<CreativeModeTab> CREATIVE_MODE_TAB = REGISTRIES.registerCreativeModeTab(
            OBSIDIAN_PRESSURE_PLATE_ITEM);

    public static void bootstrap() {
        // NO-OP
    }

    static Holder.Reference<Block> registerPressurePlateBlock(SensitivityMaterial material) {
        return REGISTRIES.registerBlock(material.getSerializedName() + "_pressure_plate",
                (BlockBehaviour.Properties properties) -> new DirectionalPressurePlateBlock(material, properties),
                () -> BlockBehaviour.Properties.ofFullCopy(material.getMaterialBlock())
                        .noCollission()
                        .lightLevel(Blocks.litBlockEmission(15)));
    }

}
