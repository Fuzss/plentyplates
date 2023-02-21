package fuzs.plentyplates.data;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.init.ModRegistry;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(DataGenerator dataGenerator, String modId) {
        super(dataGenerator, modId, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(ModRegistry.CREATIVE_MODE_TAB, PlentyPlates.MOD_NAME);
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
        this.add("container.pressure_plate", "Pressure Plate");
        this.add("gui.pressure_plate.whitelist", "Whitelist");
        this.add("gui.pressure_plate.blacklist", "Blacklist");
        this.add("gui.pressure_plate.illuminated", "Illuminated");
        this.add("gui.pressure_plate.silent", "Silent");
        this.add("gui.pressure_plate.locked", "Locked");
        this.add("gui.pressure_plate.shrouded", "Shrouded");
        this.add("gui.pressure_plate.baby", "Baby");
    }

    public void add(CreativeModeTab tab, String name) {
        this.add(((TranslatableContents) tab.getDisplayName().getContents()).getKey(), name);
    }

    public void add(Potion potion, String name) {
        String potionName = potion.getName("");
        this.add("item.minecraft.tipped_arrow.effect." + potionName, "Arrow of " + name);
        this.add("item.minecraft.potion.effect." + potionName, "Potion of " + name);
        this.add("item.minecraft.splash_potion.effect." + potionName, "Splash Potion of " + name);
        this.add("item.minecraft.lingering_potion.effect." + potionName, "Lingering Potion of " + name);
    }

    public void add(SoundEvent soundEvent, String name) {
        this.add("subtitles." + soundEvent.getLocation().getPath(), name);
    }
}
