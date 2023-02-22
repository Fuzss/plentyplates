package fuzs.plentyplates.world.level.block;

import net.minecraft.network.chat.Component;

public enum PressurePlateSetting {
    WHITELIST(true, "whitelist", -1),
    SHROUDED(true, "shrouded", 3),
    SILENT(true, "silent", 1),
    ILLUMINATED(false, "illuminated", 0),
    LOCKED(true, "locked", 2),
    BABY(false, "baby", 4);

    private static final String TRANSLATION_KEY_PREFIX = "gui.pressure_plate.";
    public static final int DEFAULT_SETTINGS;

    private final int textureId;
    private final Component componentOn;
    private final Component componentOff;
    private final boolean defaultValue;

    PressurePlateSetting(boolean defaultValue, String translationKey, int textureId) {
        this.textureId = textureId;
        this.componentOn = Component.translatable(TRANSLATION_KEY_PREFIX + translationKey + ".on");
        this.componentOff = Component.translatable(TRANSLATION_KEY_PREFIX + translationKey + ".off");
        this.defaultValue = defaultValue;
    }

    public int getTextureId() {
        return this.textureId;
    }

    public Component getComponent(boolean on) {
        return on ? this.componentOn : this.componentOff;
    }

    public static PressurePlateSetting[] defaultValues() {
        return new PressurePlateSetting[]{WHITELIST, SILENT, SHROUDED, ILLUMINATED};
    }

    static {
        int defaultSettings = 0;
        for (PressurePlateSetting setting : values()) {
            if (setting.defaultValue) defaultSettings |= 1 << setting.ordinal();
        }
        DEFAULT_SETTINGS = defaultSettings;
    }
}
