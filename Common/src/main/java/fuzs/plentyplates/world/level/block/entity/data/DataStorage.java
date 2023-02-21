package fuzs.plentyplates.world.level.block.entity.data;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;

import java.util.Set;
import java.util.stream.Collectors;

public class DataStorage<T> {
    public static final String TAG_DATA = "Data";

    private final DataProvider<T> provider;
    private Set<T> data = Sets.newHashSet();

    public DataStorage(DataProvider<T> provider) {
        this.provider = provider;
    }

    public boolean contains(Entity entity) {
        return this.data.contains(this.provider.fromEntity(entity));
    }

    public void load(CompoundTag tag) {
        ListTag list = (ListTag) tag.get(TAG_DATA);
        if (list == null) list = new ListTag();
        this.data = list.stream().map(this.provider::fromTag).collect(Collectors.toSet());
    }

    public void save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (T datum : this.data) {
            list.add(this.provider.toTag(datum));
        }
        tag.put(TAG_DATA, list);
    }
}
