package fuzs.plentyplates.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class LabelButton extends Button {
    private boolean clicked;
    private String truncatedMessage;

    public LabelButton(int x, int y, int width, int height, Component component, OnPress onPress) {
        super(x, y, width, height, component, onPress, DEFAULT_NARRATION);
    }

    @Override
    public void onPress() {
        super.onPress();
        this.clicked = !this.clicked;
    }

    public void reset() {
        this.clicked = false;
    }

    @Override
    public void setMessage(Component message) {
        super.setMessage(message);
        Font font = Minecraft.getInstance().font;
        String string = message.getString();
        this.truncatedMessage = font.plainSubstrByWidth(string, this.width - 8);
        if (this.truncatedMessage.equals(string)) {
            this.truncatedMessage = "";
        } else {
            this.truncatedMessage = font.plainSubstrByWidth(string, this.width - 8 - font.width("...")) + "...";
        }
        if (!this.truncatedMessage.isEmpty()) {
            this.setTooltip(Tooltip.create(this.getMessage()));
        } else {
            this.setTooltip(null);
        }
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        RenderSystem.enableDepthTest();
        if (this.clicked) {
            fill(poseStack, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF8892C9);
        }
        if (!this.truncatedMessage.isEmpty()) {
            drawString(poseStack, font, this.truncatedMessage, this.getX() + 4, this.getY() + (this.height - 8) / 2, this.isHoveredOrFocused() || this.clicked ? 16777120 : 14737632);
        } else {
            drawString(poseStack, font, this.getMessage(), this.getX() + 4, this.getY() + (this.height - 8) / 2, this.isHoveredOrFocused() || this.clicked ? 16777120 : 14737632);
        }
    }
}
