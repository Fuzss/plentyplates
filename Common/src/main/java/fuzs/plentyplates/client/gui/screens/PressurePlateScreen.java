package fuzs.plentyplates.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.networking.ServerboundSetValuesMessage;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.PressurePlateSetting;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.inventory.InventoryMenu;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class PressurePlateScreen extends Screen implements MenuAccess<PressurePlateMenu> {
    public static final ResourceLocation TEXTURE_LOCATION = PlentyPlates.id("textures/gui/pressure_plate.png");
    private static final ResourceLocation BARRIER_LOCATION = new ResourceLocation("item/barrier");
    private static final int VALUES_PER_PAGE = 7;

    private final PressurePlateMenu menu;
    protected int imageWidth = 176;
    protected int imageHeight = 166;
    protected int leftPos;
    protected int topPos;
    private EditBox editBox;
    private final LabelButton[] clickableLabels = new LabelButton[VALUES_PER_PAGE];
    private int selectedLabel = -1;
    private AbstractWidget confirmButton;
    private AbstractWidget removeButton;
    private final AbstractWidget[] navigationButtons = new AbstractWidget[2];
    private List<String> currentValues = Lists.newArrayList();
    private int currentValuesPage;
    private Collection<String> allowedValues = Collections.emptySet();

    public PressurePlateScreen(PressurePlateMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.editBox != null && this.editBox.visible) {
            this.editBox.tick();
        }
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.addRenderableWidget(new ImageButton(this.leftPos + this.imageWidth - 3 - 21, this.topPos - 18, 15, 15, 203, 0, TEXTURE_LOCATION, button -> {
            this.onClose();
        }));
        this.addWhitelistButtons();
        this.addSettingsButtons();
        this.addTextBoxes();
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String string = this.editBox.getValue();
        this.init(minecraft, width, height);
        this.editBox.setValue(string);
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
        this.editBox = this.addRenderableWidget(new EditBox(this.font, this.leftPos + 10, this.topPos + 50, 130, 20, Component.empty()));
        this.editBox.setResponder(s -> {
            boolean valid = this.allowedValues.contains(s) && !this.currentValues.contains(s);
            this.confirmButton.active = valid;
            this.editBox.setTextColor(valid ? 14737632 : ChatFormatting.RED.getColor());
            String suggestion;
            if (valid) {
                suggestion = "";
            } else {
                suggestion = this.allowedValues.stream()
                        .filter(Predicate.not(this.currentValues::contains))
                        .filter(value -> value.startsWith(s))
                        .map(value -> this.font.plainSubstrByWidth(value, this.editBox.getInnerWidth()))
                        .map(value -> value.substring(s.length()))
                        .findFirst()
                        .orElse("");
            }
            this.editBox.setSuggestion(suggestion);
        });
        this.confirmButton = this.addRenderableWidget(new ImageButton(this.leftPos + 147, this.topPos + 50, 20, 20, 140, 166, TEXTURE_LOCATION, button -> {
            String value = this.editBox.getValue();
            if (this.allowedValues.contains(value)) {
                this.currentValues.add(value);
                this.sendCurrentValues();
                this.rebuildListView(true);
            }
            this.editBox.setValue("");
        }));
        this.confirmButton.active = false;
        this.removeButton = this.addRenderableWidget(new ImageButton(this.leftPos + 147, this.topPos + 106, 20, 20, 160, 166, TEXTURE_LOCATION, button -> {
            if (this.selectedLabel != -1) {
                int index = this.currentValuesPage * VALUES_PER_PAGE + this.selectedLabel;
                if (index >= 0 && index < this.currentValues.size()) {
                    this.currentValues.remove(index);
                    this.sendCurrentValues();
                    this.rebuildListView(false);
                }
            }
        }));
        for (int i = 0; i < this.clickableLabels.length; i++) {
            int labelIndex = i;
            this.clickableLabels[i] = this.addRenderableWidget(new LabelButton(this.leftPos + 10, this.topPos + 78 + i * 11, 130, 10, Component.empty(), button -> {
                if (this.selectedLabel != -1 && this.selectedLabel != labelIndex) {
                    this.clickableLabels[this.selectedLabel].reset();
                }
                if (this.selectedLabel != labelIndex) {
                    this.selectedLabel = labelIndex;
                } else {
                    this.selectedLabel = -1;
                }
                this.removeButton.active = this.selectedLabel != -1;
            }, (button, poseStack, mouseX, mouseY) -> {
                this.renderTooltip(poseStack, button.getMessage(), mouseX, mouseY);
            }));
        }
        this.navigationButtons[0] = this.addRenderableWidget(new ImageButton(this.leftPos + 147, this.topPos + 80, 20, 20, 180, 166, TEXTURE_LOCATION, button -> {
            this.currentValuesPage--;
            this.rebuildListView(false);
        }));
        this.navigationButtons[1] = this.addRenderableWidget(new ImageButton(this.leftPos + 147, this.topPos + 132, 20, 20, 200, 166, TEXTURE_LOCATION, button -> {
            this.currentValuesPage++;
            this.rebuildListView(false);
        }));
        this.rebuildListView(false);
    }

    private void rebuildListView(boolean scrollToBottom) {
        this.selectedLabel = -1;
        int lastValuesPage = (this.currentValues.size() - 1) / VALUES_PER_PAGE;
        if (scrollToBottom || lastValuesPage < this.currentValuesPage) {
            this.currentValuesPage = lastValuesPage;
        }
        this.removeButton.active = false;
        for (int i = 0; i < this.clickableLabels.length; i++) {
            LabelButton clickableLabel = this.clickableLabels[i];
            clickableLabel.reset();
            int index = this.currentValuesPage * VALUES_PER_PAGE + i;
            Component message;
            if (index < this.currentValues.size()) {
                message = Component.literal(this.currentValues.get(index));
            } else {
                message = Component.empty();
            }
            clickableLabel.setMessage(message);
        }
        this.updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        this.navigationButtons[0].active = this.currentValuesPage > 0;
        this.navigationButtons[1].active = this.currentValuesPage < (this.currentValues.size() - 1) / VALUES_PER_PAGE;
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
        if (keyCode == InputConstants.KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }

        return this.editBox.keyPressed(keyCode, scanCode, modifiers) || this.editBox.canConsumeInput() || this.containerKeyPressed(keyCode, scanCode, modifiers);
    }

    public boolean containerKeyPressed(int keyCode, int scanCode, int modifiers) {
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

    public void setInitialValues(Collection<String> allowedValues, List<String> currentValues) {
        this.allowedValues = allowedValues;
        this.currentValues = currentValues;
        this.rebuildListView(false);
        this.editBox.setValue("");
    }

    private void sendCurrentValues() {
        PlentyPlates.NETWORKING.sendToServer(new ServerboundSetValuesMessage(this.menu.containerId, this.currentValues));
    }
}
