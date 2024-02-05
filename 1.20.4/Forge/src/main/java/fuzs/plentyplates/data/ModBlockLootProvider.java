package fuzs.plentyplates.data;

import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.plentyplates.world.level.block.entity.PressurePlateBlockEntity;
import fuzs.plentyplates.world.level.block.entity.data.DataProvider;
import fuzs.puzzleslib.api.data.v1.AbstractLootProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ModBlockLootProvider extends AbstractLootProvider.Blocks {

    public ModBlockLootProvider(PackOutput packOutput, String modId) {
        super(packOutput, modId);
    }

    @Override
    public void generate() {
        for (Block block : SensitivityMaterial.allBlocks()) {
            this.add(block, this.createPressurePlateDrop(block));
        }
    }

    protected LootTable.Builder createPressurePlateDrop(Block block) {
        return LootTable.lootTable().withPool(this.applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(block).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(PressurePlateBlockEntity.TAG_SETTINGS, "BlockEntityTag." + PressurePlateBlockEntity.TAG_SETTINGS)).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(DataProvider.TAG_DATA, "BlockEntityTag." + DataProvider.TAG_DATA)).apply(CopyBlockState.copyState(block).copy(DirectionalPressurePlateBlock.LIT)).apply(CopyBlockState.copyState(block).copy(DirectionalPressurePlateBlock.SILENT)).apply(CopyBlockState.copyState(block).copy(DirectionalPressurePlateBlock.SHROUDED)))));
    }
}
