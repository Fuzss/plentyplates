package fuzs.plentyplates;

import fuzs.plentyplates.init.ModRegistry;
import fuzs.plentyplates.network.ClientboundInitialValuesMessage;
import fuzs.plentyplates.network.ServerboundSetValuesMessage;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlentyPlates implements ModConstructor {
    public static final String MOD_ID = "plentyplates";
    public static final String MOD_NAME = "Plenty Plates";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.builder(MOD_ID)
            .registerClientbound(ClientboundInitialValuesMessage.class)
            .registerServerbound(ServerboundSetValuesMessage.class);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
    }

    @Override
    public void onRegisterCreativeModeTabs(CreativeModeTabContext context) {
        context.registerCreativeModeTab(CreativeModeTabConfigurator.from(MOD_ID)
                .icon(() -> new ItemStack(SensitivityMaterial.OBSIDIAN.getPressurePlateBlock()))
                .displayItems((itemDisplayParameters, output) -> {
                    for (Block block : SensitivityMaterial.allBlocks()) {
                        output.accept(block);
                    }
                }));
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
