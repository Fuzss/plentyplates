package fuzs.plentyplates.data;

import fuzs.plentyplates.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(DataGenerator dataGenerator, String modId) {
        super(dataGenerator, modId, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(ModRegistry.PRESSURE_PLATE_BLOCK.get(), "Pressure Plate");
    }
}
