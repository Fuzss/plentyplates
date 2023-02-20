package fuzs.plentyplates.world.level.block.entity.data;

import net.minecraft.core.Registry;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class RegistryDataProvider<T> implements DataProvider<T> {
    private final Registry<T> registry;
    private final Function<Entity, T> extractor;

    public RegistryDataProvider(Registry<T> registry, Function<Entity, T> extractor) {
        this.registry = registry;
        this.extractor = extractor;
    }

    public static RegistryDataProvider<EntityType<?>> entityType() {
        return new RegistryDataProvider<>(Registry.ENTITY_TYPE, Entity::getType);
    }

    public static RegistryDataProvider<VillagerProfession> villagerProfession() {
        return new RegistryDataProvider<>(Registry.VILLAGER_PROFESSION, entity -> {
            if (entity instanceof Villager villager) {
                return villager.getVillagerData().getProfession();
            }
            return null;
        });
    }

    @Override
    public T fromString(String value) {
        return this.registry.get(new ResourceLocation(value));
    }

    @Override
    public String toString(T value) {
        return this.registry.getKey(value).toString();
    }

    @Override
    public List<? extends T> getAllValues() {
        return this.registry.stream().toList();
    }

    @Override
    public T fromTag(Tag tag) {
        return this.fromString(tag.getAsString());
    }

    @Override
    public Tag toTag(T value) {
        return StringTag.valueOf(this.toString(value));
    }

    @Nullable
    @Override
    public T fromEntity(Entity entity) {
        return this.extractor.apply(entity);
    }
}
