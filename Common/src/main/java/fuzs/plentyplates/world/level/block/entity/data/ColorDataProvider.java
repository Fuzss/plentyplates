package fuzs.plentyplates.world.level.block.entity.data;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ColorDataProvider implements DataProvider<DyeColor> {

    @Nullable
    @Override
    public DyeColor fromString(String value) {
        return DyeColor.byName(value, null);
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
        return DyeColor.byId(((ByteTag) tag).getAsByte());
    }

    @Override
    public Tag toTag(DyeColor value) {
        return ByteTag.valueOf((byte) value.getId());
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
