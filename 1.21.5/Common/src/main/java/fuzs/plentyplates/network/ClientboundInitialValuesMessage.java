package fuzs.plentyplates.network;

import fuzs.plentyplates.client.gui.screens.PressurePlateScreen;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record ClientboundInitialValuesMessage(int containerId,
                                              Collection<String> allowedValues,
                                              List<String> currentValues) implements ClientboundPlayMessage {
    public static final StreamCodec<ByteBuf, ClientboundInitialValuesMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.SHORT.map(Short::intValue, Integer::shortValue),
            ClientboundInitialValuesMessage::containerId,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(ArrayList::new)),
            ClientboundInitialValuesMessage::allowedValues,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            ClientboundInitialValuesMessage::currentValues,
            ClientboundInitialValuesMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                if (context.player().containerMenu.containerId == ClientboundInitialValuesMessage.this.containerId) {
                    if (context.client().screen instanceof PressurePlateScreen screen) {
                        screen.setInitialValues(ClientboundInitialValuesMessage.this.allowedValues,
                                ClientboundInitialValuesMessage.this.currentValues);
                    }
                }
            }
        };
    }
}
