package fuzs.plentyplates.network;

import fuzs.plentyplates.client.gui.screens.PressurePlateScreen;
import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

import java.util.Collection;
import java.util.List;

public record ClientboundInitialValuesMessage(int containerId, Collection<String> allowedValues, List<String> currentValues) implements ClientboundMessage<ClientboundInitialValuesMessage> {

    @Override
    public ClientMessageListener<ClientboundInitialValuesMessage> getHandler() {
        return new ClientMessageListener<>() {

            @Override
            public void handle(ClientboundInitialValuesMessage message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                if (player.containerMenu.containerId == ClientboundInitialValuesMessage.this.containerId) {
                    if (client.screen instanceof PressurePlateScreen screen) {
                        screen.setInitialValues(ClientboundInitialValuesMessage.this.allowedValues, ClientboundInitialValuesMessage.this.currentValues);
                    }
                }
            }
        };
    }
}
