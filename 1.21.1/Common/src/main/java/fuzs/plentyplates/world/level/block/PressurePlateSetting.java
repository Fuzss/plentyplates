package fuzs.plentyplates.world.level.block;

import net.minecraft.network.chat.Component;

public enum PressurePlateSetting {
    WHITELIST("whitelist", true, -1),
    SHROUDED("shrouded", true, 3),
    SILENT("silent", true, 1),
    ILLUMINATED("illuminated", false, 0),
    LOCKED("locked", true, 2),
    BABY("baby", false, 4);

    public static final int DEFAULT_SETTINGS;

    private final int textureId;
    private final Component componentOn;
    private final Component componentOff;
    private final boolean defaultValue;

    PressurePlateSetting(String name, boolean defaultValue, int textureId) {
        this.textureId = textureId;
        this.componentOn = Component.translatable("gui.pressure_plate." + name + ".on");
        this.componentOff = Component.translatable("gui.pressure_plate." + name + ".off");
        this.defaultValue = defaultValue;
    }

    public int getTextureId() {
        return this.textureId;
    }

    public Component getComponent(boolean turnedOn) {
        return turnedOn ? this.componentOn : this.componentOff;
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
