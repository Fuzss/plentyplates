package fuzs.plentyplates.networking;

import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.puzzleslib.api.network.v3.ServerMessageListener;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.List;

public record ServerboundSetValuesMessage(int containerId, List<String> currentValues) implements ServerboundMessage<ServerboundSetValuesMessage> {

    @Override
    public ServerMessageListener<ServerboundSetValuesMessage> getHandler() {
        return new ServerMessageListener<>() {

            @Override
            public void handle(ServerboundSetValuesMessage message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level) {
                if (player.containerMenu.containerId == ServerboundSetValuesMessage.this.containerId && player.containerMenu instanceof PressurePlateMenu menu) {
                    menu.setCurrentValues(ServerboundSetValuesMessage.this.currentValues);
                }
            }
        };
    }
}
