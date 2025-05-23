package fuzs.plentyplates.world.level.block.entity.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ColorDataProvider implements DataProvider<DyeColor> {

    @Nullable
    @Override
    public DyeColor fromString(String value, HolderLookup.Provider registries) {
        return DyeColor.byName(value, null);
    }

    @Override
    public String toString(DyeColor value, HolderLookup.Provider registries) {
        return value.getSerializedName();
    }

    @Override
    public List<? extends DyeColor> getAllValues(HolderLookup.Provider registries) {
        return Arrays.asList(DyeColor.values());
    }

    @Override
    public @Nullable DyeColor fromTag(Tag tag, HolderLookup.Provider registries) {
        return tag.asByte().map(DyeColor::byId).orElse(null);
    }

    @Override
    public Tag toTag(DyeColor value, HolderLookup.Provider registries) {
        return ByteTag.valueOf((byte) value.getId());
    }

    @Nullable
    @Override
    public DyeColor fromEntity(Entity entity) {
        if (entity instanceof Sheep sheep) {
            return sheep.getColor();
        } else {
            return null;
        }
    }
}
