package fuzs.plentyplates.world.level.block.entity.data;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class PlayerDataProvider implements DataProvider<GameProfile> {

    @Nullable
    @Override
    public GameProfile fromString(String value, HolderLookup.Provider registries) {
        return Optional.of(value)
                .filter(Predicate.not(String::isEmpty))
                .flatMap(ProxyImpl.get().getMinecraftServer().getProfileCache()::get)
                .orElse(null);
    }

    @Override
    public String toString(GameProfile value, HolderLookup.Provider registries) {
        return value.getName();
    }

    @Override
    public List<? extends GameProfile> getAllValues(HolderLookup.Provider registries) {
        return Collections.emptyList();
    }

    @Override
    public GameProfile fromTag(Tag tag, HolderLookup.Provider registries) {
        return ExtraCodecs.GAME_PROFILE_WITHOUT_PROPERTIES.codec()
                .parse(registries.createSerializationContext(NbtOps.INSTANCE), tag)
                .resultOrPartial()
                .orElse(null);
    }

    @Override
    public Tag toTag(GameProfile value, HolderLookup.Provider registries) {
        return ExtraCodecs.GAME_PROFILE_WITHOUT_PROPERTIES.codec()
                .encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), value)
                .resultOrPartial()
                .orElseGet(CompoundTag::new);
    }

    @Override
    public @Nullable GameProfile fromEntity(Entity entity) {
        if (entity instanceof Player player) {
            return player.getGameProfile();
        } else {
            return null;
        }
    }
}
