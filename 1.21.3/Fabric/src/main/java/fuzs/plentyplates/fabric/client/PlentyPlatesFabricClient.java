package fuzs.plentyplates.fabric.client;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.client.PlentyPlatesClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class PlentyPlatesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(PlentyPlates.MOD_ID, PlentyPlatesClient::new);
    }
}
