package fuzs.plentyplates.client;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.client.gui.screens.PressurePlateScreen;
import fuzs.plentyplates.client.packs.TranslucentPackResources;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
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
    public void onRegisterBlockRenderTypes(RenderTypesContext<Block> context) {
        context.registerRenderType(RenderType.translucent(), SensitivityMaterial.allBlocks());
    }

    @Override
    public void onAddResourcePackFinders(PackRepositorySourcesContext context) {
        context.addRepositorySource(PackResourcesHelper.buildClientPack(PlentyPlates.id("translucent_texture"),
                TranslucentPackResources::new, false
        ));
    }
}
