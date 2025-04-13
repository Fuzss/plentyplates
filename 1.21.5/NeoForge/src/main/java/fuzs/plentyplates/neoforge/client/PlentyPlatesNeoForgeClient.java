package fuzs.plentyplates.neoforge.client;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.client.PlentyPlatesClient;
import fuzs.plentyplates.data.client.ModAtlasProvider;
import fuzs.plentyplates.data.client.ModLanguageProvider;
import fuzs.plentyplates.data.client.ModModelProvider;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = PlentyPlates.MOD_ID, dist = Dist.CLIENT)
public class PlentyPlatesNeoForgeClient {

    public PlentyPlatesNeoForgeClient() {
        ClientModConstructor.construct(PlentyPlates.MOD_ID, PlentyPlatesClient::new);
        DataProviderHelper.registerDataProviders(PlentyPlates.MOD_ID,
                ModLanguageProvider::new,
                ModModelProvider::new,
                ModAtlasProvider::new);
    }
}
