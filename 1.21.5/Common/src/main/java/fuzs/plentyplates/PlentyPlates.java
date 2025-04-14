package fuzs.plentyplates;

import fuzs.plentyplates.init.ModRegistry;
import fuzs.plentyplates.network.client.ServerboundSetValuesMessage;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlentyPlates implements ModConstructor {
    public static final String MOD_ID = "plentyplates";
    public static final String MOD_NAME = "Plenty Plates";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.playToServer(ServerboundSetValuesMessage.class, ServerboundSetValuesMessage.STREAM_CODEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
