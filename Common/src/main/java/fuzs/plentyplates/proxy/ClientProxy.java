package fuzs.plentyplates.proxy;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClientProxy extends ServerProxy {

    @Override
    public void appendPressurePlateHoverText(List<Component> lines) {
        Component shiftComponent = Component.empty().append(Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage()).withStyle(ChatFormatting.LIGHT_PURPLE);
        Component useComponent = Component.empty().append(Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage()).withStyle(ChatFormatting.LIGHT_PURPLE);
        lines.add(Component.translatable("block.plentyplates.pressure_plate.description", shiftComponent, useComponent).withStyle(ChatFormatting.GRAY));
    }
}
