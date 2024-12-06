package fuzs.plentyplates.data.client;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.PressurePlateSetting;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.plentyplates.world.level.block.entity.PressurePlateBlockEntity;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.addCreativeModeTab(PlentyPlates.MOD_ID, PlentyPlates.MOD_NAME);
        builder.add(SensitivityMaterial.OBSIDIAN.translationKey(), "Obsidian Pressure Plate");
        builder.add(SensitivityMaterial.COBBLESTONE.translationKey(), "Cobblestone Pressure Plate");
        builder.add(SensitivityMaterial.MOSSY_COBBLESTONE.translationKey(), "Mossy Cobblestone Pressure Plate");
        builder.add(SensitivityMaterial.STONE_BRICKS.translationKey(), "Stone Bricks Pressure Plate");
        builder.add(SensitivityMaterial.MOSSY_STONE_BRICKS.translationKey(), "Mossy Stone Bricks Pressure Plate");
        builder.add(SensitivityMaterial.CHISELED_STONE_BRICKS.translationKey(), "Chiseled Stone Bricks Pressure Plate");
        builder.add(DirectionalPressurePlateBlock.KEY_PRESSURE_PLATE_DESCRIPTION, "Use %s + %s with an empty hand to configure once placed.");
        builder.add(DirectionalPressurePlateBlock.KEY_PRESSURE_PLATE_ACTIVATED_BY, "Activated By: %s");
        builder.add(SensitivityMaterial.OBSIDIAN.descriptionKey(), "Players");
        builder.add(SensitivityMaterial.COBBLESTONE.descriptionKey(), "Entities");
        builder.add(SensitivityMaterial.MOSSY_COBBLESTONE.descriptionKey(), "Items");
        builder.add(SensitivityMaterial.STONE_BRICKS.descriptionKey(), "Mobs");
        builder.add(SensitivityMaterial.MOSSY_STONE_BRICKS.descriptionKey(), "Villagers");
        builder.add(SensitivityMaterial.CHISELED_STONE_BRICKS.descriptionKey(), "Sheep");
        builder.add(PressurePlateBlockEntity.COMPONENT_PRESSURE_PLATE, "Pressure Plate");
        builder.add(PressurePlateSetting.WHITELIST.getComponent(true), "Whitelist");
        builder.add(PressurePlateSetting.WHITELIST.getComponent(false), "Blacklist");
        builder.add(PressurePlateSetting.ILLUMINATED.getComponent(true), "Illuminated");
        builder.add(PressurePlateSetting.ILLUMINATED.getComponent(false), "Darkened");
        builder.add(PressurePlateSetting.SILENT.getComponent(true), "Noisy");
        builder.add(PressurePlateSetting.SILENT.getComponent(false), "Silent");
        builder.add(PressurePlateSetting.LOCKED.getComponent(true), "Unlocked");
        builder.add(PressurePlateSetting.LOCKED.getComponent(false), "Locked");
        builder.add(PressurePlateSetting.SHROUDED.getComponent(true), "Visible");
        builder.add(PressurePlateSetting.SHROUDED.getComponent(false), "Shrouded");
        builder.add(PressurePlateSetting.BABY.getComponent(true), "Baby Mobs Only");
        builder.add(PressurePlateSetting.BABY.getComponent(false), "All Mobs");
    }
}
