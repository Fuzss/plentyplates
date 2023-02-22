package fuzs.plentyplates.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.plentyplates.world.level.block.entity.PressurePlateBlockEntity;
import fuzs.plentyplates.world.level.block.entity.data.DataProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModLootTableProvider extends LootTableProvider {
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> subProviders = ImmutableList.of(Pair.of(ModBlockLoot::new, LootContextParamSets.BLOCK));
    private final String modId;

    public ModLootTableProvider(DataGenerator dataGenerator, String modId) {
        super(dataGenerator);
        this.modId = modId;
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return this.subProviders;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {

    }

    private class ModBlockLoot extends BlockLoot {

        @Override
        protected void addTables() {
            for (Block block : SensitivityMaterial.allBlocks()) {
                this.add(block, createPressurePlateDrop(block));
            }
        }

        protected static LootTable.Builder createPressurePlateDrop(Block block) {
            return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(block).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(PressurePlateBlockEntity.TAG_SETTINGS, "BlockEntityTag." + PressurePlateBlockEntity.TAG_SETTINGS)).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(DataProvider.TAG_DATA, "BlockEntityTag." + DataProvider.TAG_DATA)).apply(CopyBlockState.copyState(block).copy(DirectionalPressurePlateBlock.LIT)).apply(CopyBlockState.copyState(block).copy(DirectionalPressurePlateBlock.SILENT)).apply(CopyBlockState.copyState(block).copy(DirectionalPressurePlateBlock.SHROUDED)))));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ForgeRegistries.BLOCKS.getEntries().stream()
                    .filter(e -> e.getKey().location().getNamespace().equals(ModLootTableProvider.this.modId))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toSet());
        }
    }
}
