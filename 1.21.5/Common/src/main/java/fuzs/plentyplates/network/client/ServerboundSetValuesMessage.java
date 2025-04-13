package fuzs.plentyplates.network.client;

import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ServerboundSetValuesMessage(int containerId,
                                          List<String> currentValues) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundSetValuesMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.SHORT.map(Short::intValue, Integer::shortValue),
            ServerboundSetValuesMessage::containerId,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            ServerboundSetValuesMessage::currentValues,
            ServerboundSetValuesMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                if (context.player().containerMenu instanceof PressurePlateMenu menu &&
                        menu.containerId == ServerboundSetValuesMessage.this.containerId) {
                    menu.setCurrentValues(ServerboundSetValuesMessage.this.currentValues);
                }
            }
        };
    }
}
