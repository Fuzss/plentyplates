package fuzs.plentyplates.world.level.block.entity.data;

import com.google.common.base.Predicates;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class RegistryDataProvider<T> implements DataProvider<Holder<T>> {
    private final ResourceKey<Registry<T>> registryKey;
    private final Function<Entity, Holder<T>> entityExtractor;
    private final Predicate<Holder<T>> entryFilter;

    public RegistryDataProvider(ResourceKey<Registry<T>> registryKey, Function<Entity, Holder<T>> entityExtractor, Predicate<Holder<T>> entryFilter) {
        this.registryKey = registryKey;
        this.entityExtractor = entityExtractor;
        this.entryFilter = entryFilter;
    }

    public static RegistryDataProvider<EntityType<?>> entityType(boolean filterMisc) {
        return new RegistryDataProvider<>(Registries.ENTITY_TYPE, (Entity entity) -> entity.getType().builtInRegistryHolder(),
                (Holder<EntityType<?>> entityType) -> {
                    return !filterMisc || entityType.value().getCategory() != MobCategory.MISC;
                }
        );
    }

    public static RegistryDataProvider<Item> item() {
        return new RegistryDataProvider<>(Registries.ITEM, (Entity entity) -> {
            Item item;
            if (entity instanceof ItemEntity itemEntity) {
                item = itemEntity.getItem().getItem();
            } else {
                item = Items.AIR;
            }

            return item.builtInRegistryHolder();
        }, (Holder<Item> item) -> item.value() != Items.AIR);
    }

    public static RegistryDataProvider<VillagerProfession> villagerProfession() {
        return new RegistryDataProvider<>(Registries.VILLAGER_PROFESSION, (Entity entity) -> {
            if (entity instanceof Villager villager) {
                VillagerProfession villagerProfession = villager.getVillagerData().getProfession();
                Registry<VillagerProfession> registry = villager.registryAccess().registryOrThrow(
                        Registries.VILLAGER_PROFESSION);
                return registry.wrapAsHolder(villagerProfession);
            } else {
                return null;
            }
        }, Predicates.alwaysTrue());
    }

    @Nullable
    @Override
    public Holder<T> fromString(String value, HolderLookup.Provider registries) {
        ResourceLocation resourceLocation = ResourceLocationHelper.tryParse(value);
        if (resourceLocation != null) {
            ResourceKey<T> resourceKey = ResourceKey.create(this.registryKey, resourceLocation);
            Optional<Holder.Reference<T>> optional = registries.lookupOrThrow(this.registryKey).get(resourceKey);
            return optional.orElse(null);
        } else {
            return null;
        }
    }

    @Override
    public String toString(Holder<T> value, HolderLookup.Provider registries) {
        return value.unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).orElseThrow();
    }

    @Override
    public List<? extends Holder<T>> getAllValues(HolderLookup.Provider registries) {
        return registries.lookupOrThrow(this.registryKey).listElements().filter(this.entryFilter).toList();
    }

    @Override
    public Holder<T> fromTag(Tag tag, HolderLookup.Provider registries) {
        if (tag.getId() == Tag.TAG_STRING) {
            return this.fromString(tag.getAsString(), registries);
        } else {
            return null;
        }
    }

    @Override
    public Tag toTag(Holder<T> value, HolderLookup.Provider registries) {
        return StringTag.valueOf(this.toString(value, registries));
    }

    @Nullable
    @Override
    public Holder<T> fromEntity(Entity entity) {
        return this.entityExtractor.apply(entity);
    }
}
