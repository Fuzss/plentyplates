package fuzs.plentyplates.data;

import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(DataGenerator dataGenerator, String modId) {
        super(dataGenerator, modId, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(SensitivityMaterial.OBSIDIAN.translationKey(), "Obsidian Pressure Plate");
        this.add(SensitivityMaterial.DEEPSLATE.translationKey(), "Deepslate Pressure Plate");
        this.add(SensitivityMaterial.CALCITE.translationKey(), "Calcite Pressure Plate");
        this.add(SensitivityMaterial.TUFF.translationKey(), "Tuff Pressure Plate");
        this.add(SensitivityMaterial.SMOOTH_BASALT.translationKey(), "Smooth Basalt Pressure Plate");
        this.add("block.plentyplates.pressure_plate.activated_by", "Activated By: %s");
        this.add(SensitivityMaterial.OBSIDIAN.descriptionKey(), "Players");
        this.add(SensitivityMaterial.DEEPSLATE.descriptionKey(), "Mobs");
        this.add(SensitivityMaterial.CALCITE.descriptionKey(), "Animals");
        this.add(SensitivityMaterial.TUFF.descriptionKey(), "Villagers");
        this.add(SensitivityMaterial.SMOOTH_BASALT.descriptionKey(), "Sheep");
    }
}
