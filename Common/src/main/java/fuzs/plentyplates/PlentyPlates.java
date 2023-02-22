package fuzs.plentyplates;

import fuzs.plentyplates.init.ModRegistry;
import fuzs.plentyplates.networking.ClientboundInitialValuesMessage;
import fuzs.plentyplates.networking.ServerboundSetValuesMessage;
import fuzs.puzzleslib.api.networking.v3.NetworkHandlerV3;
import fuzs.puzzleslib.core.ModConstructor;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlentyPlates implements ModConstructor {
    public static final String MOD_ID = "plentyplates";
    public static final String MOD_NAME = "Plenty Plates";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandlerV3 NETWORKING = NetworkHandlerV3.builder(MOD_ID).registerClientbound(ClientboundInitialValuesMessage.class).registerServerbound(ServerboundSetValuesMessage.class).build();

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
