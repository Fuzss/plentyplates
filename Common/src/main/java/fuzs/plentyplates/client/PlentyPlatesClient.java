package fuzs.plentyplates.client;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.client.gui.screens.PressurePlateScreen;
import fuzs.plentyplates.client.packs.TranslucentPackResources;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BuildCreativeModeTabContentsContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.core.v1.context.ModLifecycleContext;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;

public class PlentyPlatesClient implements ClientModConstructor {

    @Override
    public void onClientSetup(ModLifecycleContext context) {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            MenuScreens.register(material.getMenuType(), PressurePlateScreen::new);
        }
    }

    @Override
    public void onRegisterBlockRenderTypes(RenderTypesContext<Block> context) {
        Block[] blocks = SensitivityMaterial.allBlocks();
        // bad idea with the context signature yes
        context.registerRenderType(RenderType.translucent(), blocks[0], Arrays.copyOfRange(blocks, 1, blocks.length));
    }

    @Override
    public void onAddResourcePackFinders(PackRepositorySourcesContext context) {
        context.addRepositorySources(PackResourcesHelper.buildClientPack(TranslucentPackResources::new, PlentyPlates.MOD_ID, Component.literal(PlentyPlates.MOD_NAME), Component.literal("Provides translucent textures for " + PlentyPlates.MOD_NAME), true, false));
    }

    @Override
    public void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsContext context) {
        context.registerBuildListener(PlentyPlates.MOD_ID, (featureFlagSet, output, bl) -> {
            for (Block block : SensitivityMaterial.allBlocks()) {
                output.accept(block);
            }
        });
    }
}
