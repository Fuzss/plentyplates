package fuzs.plentyplates.world.level.block.entity.data;

import fuzs.puzzleslib.api.core.v1.utility.NbtSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataStorage<T> implements NbtSerializable {
    private final DataProvider<T> provider;
    private List<T> data = Collections.emptyList();

    public DataStorage(DataProvider<T> provider) {
        this.provider = provider;
    }

    public boolean permits(Entity entity, boolean whitelist) {
        if (this.data.isEmpty()) {
            return true;
        } else {
            T providedValue = this.provider.fromEntity(entity);
            return providedValue != null && this.data.contains(providedValue) == whitelist;
        }
    }

    public Collection<String> getAllowedValues(HolderLookup.Provider registries) {
        return this.provider.getSerializedValues(registries);
    }

    public void setCurrentValues(List<String> values, HolderLookup.Provider registries) {
        this.data = values.stream()
                .map((String value) -> this.provider.fromString(value, registries))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<String> getCurrentValues(HolderLookup.Provider registries) {
        return this.data.stream().map(value -> this.provider.toString(value, registries)).collect(Collectors.toList());
    }

    @Override
    public void read(CompoundTag compoundTag, HolderLookup.Provider registries) {
        this.data = this.provider.loadFrom(compoundTag, registries);
    }

    @Override
    public void write(CompoundTag compoundTag, HolderLookup.Provider registries) {
        this.provider.saveTo(compoundTag, this.data, registries);
    }
}
