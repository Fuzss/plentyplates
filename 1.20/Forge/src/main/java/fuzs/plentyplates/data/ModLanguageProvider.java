package fuzs.plentyplates.data;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.data.v1.AbstractLanguageProvider;
import net.minecraft.data.PackOutput;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(PackOutput packOutput, String modId) {
        super(packOutput, modId);
    }

    @Override
    protected void addTranslations() {
        this.addCreativeModeTab(PlentyPlates.MOD_NAME);
        this.add(SensitivityMaterial.OBSIDIAN.translationKey(), "Obsidian Pressure Plate");
        this.add(SensitivityMaterial.COBBLESTONE.translationKey(), "Cobblestone Pressure Plate");
        this.add(SensitivityMaterial.MOSSY_COBBLESTONE.translationKey(), "Mossy Cobblestone Pressure Plate");
        this.add(SensitivityMaterial.STONE_BRICKS.translationKey(), "Stone Bricks Pressure Plate");
        this.add(SensitivityMaterial.MOSSY_STONE_BRICKS.translationKey(), "Mossy Stone Bricks Pressure Plate");
        this.add(SensitivityMaterial.CHISELED_STONE_BRICKS.translationKey(), "Chiseled Stone Bricks Pressure Plate");
        this.add("block.plentyplates.pressure_plate.description", "Use %s + %s with an empty hand to configure once placed.");
        this.add("block.plentyplates.pressure_plate.activated_by", "Activated By: %s");
        this.add(SensitivityMaterial.OBSIDIAN.descriptionKey(), "Players");
        this.add(SensitivityMaterial.COBBLESTONE.descriptionKey(), "Entities");
        this.add(SensitivityMaterial.MOSSY_COBBLESTONE.descriptionKey(), "Items");
        this.add(SensitivityMaterial.STONE_BRICKS.descriptionKey(), "Mobs");
        this.add(SensitivityMaterial.MOSSY_STONE_BRICKS.descriptionKey(), "Villagers");
        this.add(SensitivityMaterial.CHISELED_STONE_BRICKS.descriptionKey(), "Sheep");
        this.add("container.pressure_plate", "Pressure Plate");
        this.add("gui.pressure_plate.whitelist.on", "Whitelist");
        this.add("gui.pressure_plate.whitelist.off", "Blacklist");
        this.add("gui.pressure_plate.illuminated.on", "Illuminated");
        this.add("gui.pressure_plate.illuminated.off", "Darkened");
        this.add("gui.pressure_plate.silent.on", "Noisy");
        this.add("gui.pressure_plate.silent.off", "Silent");
        this.add("gui.pressure_plate.locked.on", "Unlocked");
        this.add("gui.pressure_plate.locked.off", "Locked");
        this.add("gui.pressure_plate.shrouded.on", "Visible");
        this.add("gui.pressure_plate.shrouded.off", "Shrouded");
        this.add("gui.pressure_plate.baby.on", "Baby Mobs Only");
        this.add("gui.pressure_plate.baby.off", "All Mobs");
    }
}
