package fuzs.plentyplates.world.level.block.entity.data;

import fuzs.puzzleslib.proxy.Proxy;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PlayerDataProvider implements DataProvider<Player> {

    @Nullable
    @Override
    public Player fromString(String value) {
        return Proxy.INSTANCE.getGameServer().getPlayerList().getPlayerByName(value);
    }

    @Override
    public String toString(Player value) {
        return value.getName().getString();
    }

    @Override
    public List<? extends Player> getAllValues() {
        return Proxy.INSTANCE.getGameServer().getPlayerList().getPlayers();
    }

    @Override
    public Player fromTag(Tag tag) {
        return Proxy.INSTANCE.getGameServer().getPlayerList().getPlayer(UUID.fromString(tag.getAsString()));
    }

    @Override
    public Tag toTag(Player value) {
        return StringTag.valueOf(value.getStringUUID());
    }

    @Override
    public @Nullable Player fromEntity(Entity entity) {
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }
}
