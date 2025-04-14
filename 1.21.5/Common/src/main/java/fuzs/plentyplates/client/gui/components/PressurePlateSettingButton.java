package fuzs.plentyplates.client.gui.components;

import fuzs.plentyplates.client.gui.screens.PressurePlateScreen;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.PressurePlateSetting;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.Consumers;

public class PressurePlateSettingButton extends SpritelessImageButton {
    private static final ResourceLocation BARRIER_LOCATION = ResourceLocationHelper.withDefaultNamespace("item/barrier");

    private final PressurePlateSetting setting;
    private final PressurePlateMenu menu;

    public PressurePlateSettingButton(int posX, int posY, PressurePlateSetting setting, PressurePlateMenu menu) {
        super(posX,
                posY,
                20,
                20,
                setting.getTextureId() * 20 + 40,
                166,
                PressurePlateScreen.TEXTURE_LOCATION,
                Consumers.nop()::accept);
        this.setting = setting;
        this.menu = menu;
    }

    @Override
    public void onPress() {
        this.sendButtonClick(this.setting.ordinal());
        this.refreshTooltip();
    }

    private void sendButtonClick(int id) {
        if (this.menu.clickMenuButton(Minecraft.getInstance().player, id)) {
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    public void refreshTooltip() {
        boolean settingsValue = this.menu.getSettingsValue(setting);
        this.setTooltip(Tooltip.create(setting.getComponent(settingsValue)));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        boolean settingsValue = this.menu.getSettingsValue(setting);
        if (!settingsValue) {
            TextureAtlasSprite textureAtlasSprite = Minecraft.getInstance()
                    .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                    .apply(BARRIER_LOCATION);
            guiGraphics.blitSprite(RenderType::guiTextured,
                    textureAtlasSprite,
                    this.getX() + 2,
                    this.getY() + 2,
                    16,
                    16);
        }
    }
}
