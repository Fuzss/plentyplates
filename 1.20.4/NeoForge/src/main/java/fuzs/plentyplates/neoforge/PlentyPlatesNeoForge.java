package fuzs.plentyplates.neoforge;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.data.ModBlockLootProvider;
import fuzs.plentyplates.data.ModBlockTagProvider;
import fuzs.plentyplates.data.ModRecipeProvider;
import fuzs.plentyplates.data.client.ModLanguageProvider;
import fuzs.plentyplates.neoforge.data.client.ModModelProvider;
import fuzs.plentyplates.neoforge.data.client.ModSpriteSourceProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(PlentyPlates.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlentyPlatesNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(PlentyPlates.MOD_ID, PlentyPlates::new);
        DataProviderHelper.registerDataProviders(PlentyPlates.MOD_ID,
                ModBlockTagProvider::new,
                ModLanguageProvider::new,
                ModBlockLootProvider::new,
                ModModelProvider::new,
                ModRecipeProvider::new,
                ModSpriteSourceProvider::new
        );
    }
}
