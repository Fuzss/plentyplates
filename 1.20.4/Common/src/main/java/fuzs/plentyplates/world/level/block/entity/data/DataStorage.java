package fuzs.plentyplates.world.level.block.entity.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataStorage<T> {
    private final DataProvider<T> provider;
    private List<T> data = List.of();

    public DataStorage(DataProvider<T> provider) {
        this.provider = provider;
    }

    public boolean permits(Entity entity, boolean whitelist) {
        if (this.data.isEmpty()) return true;
        T providedValue = this.provider.fromEntity(entity);
        if (providedValue == null) return false;
        return this.data.contains(providedValue) == whitelist;
    }

    public Collection<String> getAllowedValues() {
        return this.provider.getSerializedValues();
    }

    public void setCurrentValues(List<String> values) {
        this.data = values.stream().map(this.provider::fromString).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<String> getCurrentValues() {
        return this.data.stream().map(this.provider::toString).collect(Collectors.toList());
    }

    public void loadFrom(CompoundTag tag) {
        this.data = this.provider.loadFrom(tag);
    }

    public void saveTo(CompoundTag tag) {
        this.provider.saveTo(tag, this.data);
    }
}
