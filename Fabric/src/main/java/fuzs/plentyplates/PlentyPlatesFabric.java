package fuzs.plentyplates;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class PlentyPlatesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(PlentyPlates.MOD_ID, PlentyPlates::new);
    }
}
