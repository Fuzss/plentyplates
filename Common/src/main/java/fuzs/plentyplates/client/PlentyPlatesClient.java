package fuzs.plentyplates.client;

import fuzs.plentyplates.init.ModRegistry;
import fuzs.puzzleslib.client.core.ClientModConstructor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class PlentyPlatesClient implements ClientModConstructor {

    @Override
    public void onRegisterBlockRenderTypesV2(RenderTypesContext<Block> context) {
        context.registerRenderType(RenderType.translucent(), ModRegistry.PRESSURE_PLATE_BLOCK.get());
    }
}
