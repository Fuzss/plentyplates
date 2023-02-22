package fuzs.plentyplates.world.level.block.entity.data;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.proxy.Proxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class PlayerDataProvider implements DataProvider<GameProfile> {

    @Nullable
    @Override
    public GameProfile fromString(String value) {
        return Optional.of(value).filter(Predicate.not(String::isEmpty)).flatMap(Proxy.INSTANCE.getGameServer().getProfileCache()::get).orElse(null);
    }

    @Override
    public String toString(GameProfile value) {
        return value.getName();
    }

    @Override
    public List<? extends GameProfile> getAllValues() {
        return List.of();
    }

    @Override
    public GameProfile fromTag(Tag tag) {
        if (tag.getId() == Tag.TAG_COMPOUND) {
            CompoundTag compoundTag = (CompoundTag) tag;
            UUID uuid = null;
            if (compoundTag.contains("UUID", Tag.TAG_INT_ARRAY)) {
                uuid = compoundTag.getUUID("UUID");
            }
            String name = "";
            if (compoundTag.contains("Name", Tag.TAG_STRING)) {
                name = compoundTag.getString("Name");
            }
            if (uuid != null || !name.isEmpty()) {
                return new GameProfile(uuid, name);
            }
        }
        return null;
    }

    @Override
    public Tag toTag(GameProfile value) {
        CompoundTag tag = new CompoundTag();
        if (value.getId() != null) {
            tag.putUUID("UUID", value.getId());
        }
        if (StringUtils.isNotBlank(value.getName())) {
            tag.putString("Name", value.getName());
        }
        return tag;
    }

    @Override
    public @Nullable GameProfile fromEntity(Entity entity) {
        if (entity instanceof Player player) {
            return player.getGameProfile();
        }
        return null;
    }
}
