package fuzs.plentyplates.world.level.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.stream.Stream;

public enum PressurePlateSetting {
    SHROUDED(3, "shrouded", DirectionalPressurePlateBlock.SHROUDED),
    SILENT(1, "silent", DirectionalPressurePlateBlock.SILENT),
    ILLUMINATED(0, "illuminated", DirectionalPressurePlateBlock.LIT),
    LOCKED(2, "locked"),
    BABY(4, "baby");

    private static final String TRANSLATION_KEY_PREFIX = "gui.pressure_plate.";

    private final int id;
    private final int mask;
    private final Component component;
    @Nullable
    private final Property<?> property;

    PressurePlateSetting(int id, String name) {
        this(id, name, null);
    }

    PressurePlateSetting(int id, String translationKey, @Nullable Property<?> property) {
        this.id = id;
        this.mask = 1 << id;
        this.component = Component.translatable(TRANSLATION_KEY_PREFIX + translationKey);
        this.property = property;
    }

    public int getId() {
        return this.id;
    }

    public int getMask() {
        return this.mask;
    }

    public Component getComponent() {
        return this.component;
    }

    public Property<?> getProperty() {
        return this.property;
    }

    public static PressurePlateSetting[] sortedValues() {
        return Stream.of(values()).sorted(Comparator.comparingInt(PressurePlateSetting::getId)).toArray(PressurePlateSetting[]::new);
    }
}
