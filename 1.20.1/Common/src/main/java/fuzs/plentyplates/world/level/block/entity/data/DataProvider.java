package fuzs.plentyplates.world.level.block.entity.data;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public interface DataProvider<T> {
    String TAG_DATA = "Data";

    @Nullable
    T fromString(String value);

    String toString(T value);

    List<? extends T> getAllValues();

    default Collection<String> getSerializedValues() {
        return this.getAllValues().stream().map(this::toString).collect(ImmutableSet.toImmutableSet());
    }

    T fromTag(Tag tag);

    Tag toTag(T value);

    @Nullable
    T fromEntity(Entity entity);

    default List<T> loadFrom(CompoundTag tag) {
        Tag data = tag.get(TAG_DATA);
        if (data != null && data.getId() == Tag.TAG_LIST) {
            ListTag list = (ListTag) data;
            return list.stream().map(this::fromTag).filter(Objects::nonNull).toList();
        }
        return List.of();
    }

    default void saveTo(CompoundTag tag, List<T> data) {
        ListTag list = new ListTag();
        for (T datum : data) {
            list.add(this.toTag(datum));
        }
        tag.put(TAG_DATA, list);
    }
}
