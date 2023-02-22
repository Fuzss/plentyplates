package fuzs.plentyplates.world.level.block.entity.data;

import net.minecraft.core.Registry;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class RegistryDataProvider<T> implements DataProvider<T> {
    private final Registry<T> registry;
    private final Function<Entity, T> extractor;
    private final Predicate<T> filter;

    public RegistryDataProvider(Registry<T> registry, Function<Entity, T> extractor, Predicate<T> filter) {
        this.registry = registry;
        this.extractor = extractor;
        this.filter = filter;
    }

    public static RegistryDataProvider<EntityType<?>> entityType() {
        return new RegistryDataProvider<>(Registry.ENTITY_TYPE, Entity::getType, entityType -> entityType.getCategory() != MobCategory.MISC);
    }

    public static RegistryDataProvider<VillagerProfession> villagerProfession() {
        return new RegistryDataProvider<>(Registry.VILLAGER_PROFESSION, entity -> {
            if (entity instanceof Villager villager) {
                return villager.getVillagerData().getProfession();
            }
            return null;
        }, profession -> true);
    }

    @Nullable
    @Override
    public T fromString(String value) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(value);
        if (resourceLocation != null && this.registry.containsKey(resourceLocation)) {
            return this.registry.get(resourceLocation);
        }
        return null;
    }

    @Override
    public String toString(T value) {
        return this.registry.getKey(value).toString();
    }

    @Override
    public List<? extends T> getAllValues() {
        return this.registry.stream().filter(this.filter).toList();
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
