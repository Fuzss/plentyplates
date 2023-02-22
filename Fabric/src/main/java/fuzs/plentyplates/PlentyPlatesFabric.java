package fuzs.plentyplates;

import fuzs.puzzleslib.core.CommonFactories;
import net.fabricmc.api.ModInitializer;

public class PlentyPlatesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonFactories.INSTANCE.modConstructor(PlentyPlates.MOD_ID).accept(new PlentyPlates());
    }
}
