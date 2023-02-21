package fuzs.plentyplates.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.PressurePlateSetting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;

public class PressurePlateScreen extends Screen implements MenuAccess<PressurePlateMenu> {
    public static final ResourceLocation TEXTURE_LOCATION = PlentyPlates.id("textures/gui/pressure_plate.png");
    private static final ResourceLocation BARRIER_LOCATION = new ResourceLocation("item/barrier");

    private final EditBox[] editBoxes = new EditBox[4];
    private final PressurePlateMenu menu;
    protected int imageWidth = 176;
    protected int imageHeight = 166;
    protected int leftPos;
    protected int topPos;

    public PressurePlateScreen(PressurePlateMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
    }

    @Override
    public void tick() {
        super.tick();
        for (EditBox box : this.editBoxes) {
            if (box != null && box.visible) box.tick();
        }
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.addRenderableWidget(new ImageButton(this.leftPos + this.imageWidth - 3 - 21, this.topPos - 18, 15, 15, 203, 0, TEXTURE_LOCATION, button -> {
            this.onClose();
        }));
        this.addWhitelistButtons();
        this.addSettingsButtons();
        this.addTextBoxes();
    }

    private void addWhitelistButtons() {
        AbstractWidget[] buttons = new AbstractWidget[2];
        int settingsAmount = this.menu.getMaterial().getSettingsAmount();
        int posX = (this.width - settingsAmount * 30 - 20) / 2;
        int posY = this.topPos + 20;
        for (int i = 0; i < buttons.length; i++) {
            int me = i;
            int other = (i + 1) % 2;
            buttons[i] = this.addRenderableWidget(new ImageButton(posX, posY, 20, 20, i * 20, 166, TEXTURE_LOCATION, button -> {
                this.sendButtonClick(0);
                buttons[me].visible = false;
                buttons[other].visible = true;
            }));
        }
        buttons[0].visible = this.menu.getWhitelistSetting();
        buttons[1].visible = !this.menu.getWhitelistSetting();
    }

    private void addSettingsButtons() {
        PressurePlateSetting[] settings = this.menu.getMaterial().getSettings();
        int settingsAmount = this.menu.getMaterial().getSettingsAmount();
        int posX = (this.width - settingsAmount * 30 - 20) / 2 + 30;
        int posY = this.topPos + 20;
        int i = 0;
        for (PressurePlateSetting setting : settings) {
            int id = setting.getId();
            this.addRenderableWidget(new ImageButton(posX + i++ * 30, posY, 20, 20, id * 20 + 40, 166, TEXTURE_LOCATION, button -> {
                this.sendButtonClick(id + 1);
            }) {
                private boolean rendering;

                @Override
                public void renderToolTip(PoseStack poseStack, int relativeMouseX, int relativeMouseY) {
                    if (!this.rendering) {
                        super.renderToolTip(poseStack, relativeMouseX, relativeMouseY);
                    }
                }

                @Override
                public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                    this.rendering = true;
                    super.renderButton(poseStack, mouseX, mouseY, partialTick);
                    if (!PressurePlateScreen.this.menu.getSettingsValue(setting)) {
                        TextureAtlasSprite textureatlassprite = PressurePlateScreen.this.minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(BARRIER_LOCATION);
                        RenderSystem.setShaderTexture(0, textureatlassprite.atlas().location());
                        blit(poseStack, this.x + 2, this.y + 2, this.getBlitOffset(), 16, 16, textureatlassprite);
                    }
                    this.rendering = false;
                    if (this.isHovered) {
                        this.renderToolTip(poseStack, mouseX, mouseY);
                    }
                }
            });
        }
    }

    private void sendButtonClick(int id) {
        if (this.menu.clickMenuButton(this.minecraft.player, id)) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    private void addTextBoxes() {
        for (int i = 0; i < this.editBoxes.length; i++) {
            this.editBoxes[i] = this.addRenderableWidget(new EditBox(this.font, this.leftPos + 20, this.topPos + 50 + i * 28, 150, 20, Component.empty()));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        this.renderBg(poseStack, partialTick, mouseX, mouseY);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderLabels(poseStack, mouseX, mouseY);
    }

    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.blit(poseStack, this.leftPos + this.imageWidth - 3 - 27, this.topPos - 24, this.imageWidth, 0, 27, 24);
    }

    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.title, this.leftPos + (this.imageWidth - this.font.width(this.title)) / 2, this.topPos + 6, 4210752);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }

        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public PressurePlateMenu getMenu() {
        return this.menu;
    }
}
