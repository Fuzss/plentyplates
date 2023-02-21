package fuzs.plentyplates.client;

import fuzs.plentyplates.client.gui.screens.PressurePlateScreen;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.client.core.ClientModConstructor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class PlentyPlatesClient implements ClientModConstructor {

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            context.registerMenuScreen(material.getMenuType(), PressurePlateScreen::new);
        }
    }

    @Override
    public void onRegisterBlockRenderTypesV2(RenderTypesContext<Block> context) {
        context.registerRenderType(RenderType.translucent(), SensitivityMaterial.allBlocks());
    }
}
