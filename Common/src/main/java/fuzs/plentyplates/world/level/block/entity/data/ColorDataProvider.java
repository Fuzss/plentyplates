package fuzs.plentyplates.world.level.block.entity.data;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ColorDataProvider implements DataProvider<DyeColor> {

    @Override
    public DyeColor fromString(String value) {
        return DyeColor.byName(value, DyeColor.WHITE);
    }

    @Override
    public String toString(DyeColor value) {
        return value.getSerializedName();
    }

    @Override
    public List<? extends DyeColor> getAllValues() {
        return Arrays.asList(DyeColor.values());
    }

    @Override
    public DyeColor fromTag(Tag tag) {
        return DyeColor.byId(((IntTag) tag).getAsInt());
    }

    @Override
    public Tag toTag(DyeColor value) {
        return IntTag.valueOf(value.getId());
    }

    @Nullable
    @Override
    public DyeColor fromEntity(Entity entity) {
        if (entity instanceof Sheep sheep) {
            return sheep.getColor();
        }
        return null;
    }
}
