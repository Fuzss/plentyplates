package fuzs.plentyplates.world.level.block.entity.data;

import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface DataProvider<T> {

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
}
