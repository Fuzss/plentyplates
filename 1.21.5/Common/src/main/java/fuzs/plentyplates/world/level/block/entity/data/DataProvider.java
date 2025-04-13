package fuzs.plentyplates.world.level.block.entity.data;

import com.google.common.collect.ImmutableSet;
import fuzs.plentyplates.PlentyPlates;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public interface DataProvider<T> {
    String TAG_DATA = PlentyPlates.id("data").toString();

    @Nullable T fromString(String value, HolderLookup.Provider registries);

    String toString(T value, HolderLookup.Provider registries);

    List<? extends T> getAllValues(HolderLookup.Provider registries);

    default Collection<String> getSerializedValues(HolderLookup.Provider registries) {
        return this.getAllValues(registries)
                .stream()
                .map(value -> this.toString(value, registries))
                .collect(ImmutableSet.toImmutableSet());
    }

    @Nullable T fromTag(Tag tag, HolderLookup.Provider registries);

    Tag toTag(T value, HolderLookup.Provider registries);

    @Nullable T fromEntity(Entity entity);

    default List<T> loadFrom(CompoundTag tag, HolderLookup.Provider registries) {
        return tag.getList(TAG_DATA).map((ListTag listTag) -> {
            return listTag.stream().map((Tag tagX) -> this.fromTag(tagX, registries)).filter(Objects::nonNull).toList();
        }).orElse(Collections.emptyList());
    }

    default void saveTo(CompoundTag tag, List<T> data, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (T datum : data) {
            list.add(this.toTag(datum, registries));
        }
        tag.put(TAG_DATA, list);
    }
}
