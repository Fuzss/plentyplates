package fuzs.plentyplates.data.client;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.client.util.PressurePlateTooltipHelper;
import fuzs.plentyplates.init.ModRegistry;
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
        builder.add(ModRegistry.CREATIVE_MODE_TAB.value(), PlentyPlates.MOD_NAME);
        builder.add(ModRegistry.OBSIDIAN_PRESSURE_PLATE_BLOCK.value(), "Obsidian Pressure Plate");
        builder.add(ModRegistry.COBBLESTONE_PRESSURE_PLATE_BLOCK.value(), "Cobblestone Pressure Plate");
        builder.add(ModRegistry.MOSSY_COBBLESTONE_PRESSURE_PLATE_BLOCK.value(), "Mossy Cobblestone Pressure Plate");
        builder.add(ModRegistry.STONE_BRICK_PRESSURE_PLATE_BLOCK.value(), "Stone Brick Pressure Plate");
        builder.add(ModRegistry.MOSSY_STONE_BRICK_PRESSURE_PLATE_BLOCK.value(), "Mossy Stone Brick Pressure Plate");
        builder.add(ModRegistry.CHISELED_STONE_BRICK_PRESSURE_PLATE_BLOCK.value(),
                "Chiseled Stone Brick Pressure Plate");
        builder.add(PressurePlateTooltipHelper.TooltipComponent.DESCRIPTION.getTranslationKey(),
                "Use %s + %s with an empty hand to configure once placed.");
        builder.add(PressurePlateTooltipHelper.TooltipComponent.ACTIVATED_BY.getTranslationKey(), "Activated By: %s");
        builder.add(SensitivityMaterial.OBSIDIAN.getEntityDescriptionKey(), "Players");
        builder.add(SensitivityMaterial.COBBLESTONE.getEntityDescriptionKey(), "Entities");
        builder.add(SensitivityMaterial.MOSSY_COBBLESTONE.getEntityDescriptionKey(), "Items");
        builder.add(SensitivityMaterial.STONE_BRICKS.getEntityDescriptionKey(), "Mobs");
        builder.add(SensitivityMaterial.MOSSY_STONE_BRICKS.getEntityDescriptionKey(), "Villagers");
        builder.add(SensitivityMaterial.CHISELED_STONE_BRICKS.getEntityDescriptionKey(), "Sheep");
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
