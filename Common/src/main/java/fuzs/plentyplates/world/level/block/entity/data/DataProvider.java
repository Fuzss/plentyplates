package fuzs.plentyplates.world.level.block.entity.data;

import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DataProvider<T> {

    T fromString(String value);

    String toString(T value);

    List<? extends T> getAllValues();

    default List<String> getSerializedValues() {
        return this.getAllValues().stream().map(this::toString).toList();
    }

    T fromTag(Tag tag);

    Tag toTag(T value);

    @Nullable
    T fromEntity(Entity entity);
}
