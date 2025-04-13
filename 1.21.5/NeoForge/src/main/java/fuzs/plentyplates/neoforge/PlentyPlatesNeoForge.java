package fuzs.plentyplates.neoforge;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.data.ModBlockLootProvider;
import fuzs.plentyplates.data.ModBlockTagProvider;
import fuzs.plentyplates.data.ModRecipeProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;

@Mod(PlentyPlates.MOD_ID)
public class PlentyPlatesNeoForge {

    public PlentyPlatesNeoForge() {
        ModConstructor.construct(PlentyPlates.MOD_ID, PlentyPlates::new);
        DataProviderHelper.registerDataProviders(PlentyPlates.MOD_ID,
                ModBlockTagProvider::new,
                ModBlockLootProvider::new,
                ModRecipeProvider::new);
    }
}
