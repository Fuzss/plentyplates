package fuzs.plentyplates.world.level.block.entity.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DataStorage<T> {
    public static final String TAG_DATA = "Data";

    private final DataProvider<T> provider;
    private List<T> data = List.of();

    public DataStorage(DataProvider<T> provider) {
        this.provider = provider;
    }

    public boolean contains(Entity entity) {
        return this.data.contains(this.provider.fromEntity(entity));
    }

    public Collection<String> getAllowedValues() {
        return this.provider.getSerializedValues();
    }

    public void setCurrentValues(List<String> values) {
        this.data = values.stream().map(this.provider::fromString).filter(Objects::nonNull).toList();
    }

    public List<String> getCurrentValues() {
        return this.data.stream().map(this.provider::toString).toList();
    }

    public void load(CompoundTag tag) {
        ListTag list = (ListTag) tag.get(TAG_DATA);
        if (list == null) list = new ListTag();
        this.data = list.stream().map(this.provider::fromTag).toList();
    }

    public void save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (T datum : this.data) {
            list.add(this.provider.toTag(datum));
        }
        tag.put(TAG_DATA, list);
    }
}
