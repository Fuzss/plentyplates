package fuzs.plentyplates.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.client.gui.components.LabelButton;
import fuzs.plentyplates.networking.ServerboundSetValuesMessage;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.PressurePlateSetting;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class PressurePlateScreen extends Screen implements MenuAccess<PressurePlateMenu>, ContainerListener {
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
    private final AbstractWidget[] whitelistButtons = new AbstractWidget[2];

    public PressurePlateScreen(PressurePlateMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
        menu.addSlotListener(this);
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
        this.menu.removeSlotListener(this);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String string = this.editBox.getValue();
        this.init(minecraft, width, height);
        this.editBox.setValue(string);
    }

    private void addWhitelistButtons() {
        int settingsAmount = this.menu.getMaterial().getSettings().length - 1;
        int posX = (this.width - settingsAmount * 30 - 20) / 2;
        int posY = this.topPos + 20;
        for (int i = 0; i < this.whitelistButtons.length; i++) {
            int me = i;
            int other = (i + 1) % 2;
            this.whitelistButtons[i] = this.addRenderableWidget(new ImageButton(posX, posY, 20, 20, i * 20, 166, 20, TEXTURE_LOCATION, 256, 256, button -> {
                this.sendButtonClick(0);
                this.whitelistButtons[me].visible = false;
                this.whitelistButtons[other].visible = true;
            }));
            this.whitelistButtons[i].setTooltip(Tooltip.create(PressurePlateSetting.WHITELIST.getComponent(me == 0)));
        }
        this.updateWhitelistButtons();
    }

    private void updateWhitelistButtons() {
        if (this.whitelistButtons[0] != null && this.whitelistButtons[1] != null) {
            this.whitelistButtons[0].visible = this.menu.getSettingsValue(PressurePlateSetting.WHITELIST);
            this.whitelistButtons[1].visible = !this.menu.getSettingsValue(PressurePlateSetting.WHITELIST);
        }
    }

    private void addSettingsButtons() {
        PressurePlateSetting[] settings = this.menu.getMaterial().getSettings();
        int settingsAmount = settings.length - 1;
        int posX = (this.width - settingsAmount * 30 - 20) / 2 + 30;
        int posY = this.topPos + 20;
        int i = 0;
        for (PressurePlateSetting setting : settings) {
            if (i++ == 0) continue;
            this.addRenderableWidget(new ImageButton(posX + (i - 2) * 30, posY, 20, 20, setting.getTextureId() * 20 + 40, 166, TEXTURE_LOCATION, button -> {
                this.sendButtonClick(setting.ordinal());
            }) {

                @Override
                public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                    super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
                    boolean settingsValue = PressurePlateScreen.this.menu.getSettingsValue(setting);
                    if (!settingsValue) {
                        TextureAtlasSprite atlasSprite = PressurePlateScreen.this.minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(BARRIER_LOCATION);
                        guiGraphics.blit(this.getX() + 2, this.getY() + 2, 0, 16, 16, atlasSprite);
                    }
                    if (this.isHoveredOrFocused()) {
                        guiGraphics.renderTooltip(PressurePlateScreen.this.font, setting.getComponent(settingsValue), mouseX, mouseY);
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
            boolean valid = this.isValidInput(s);
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
            if (this.isValidInput(value)) {
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

    private boolean isValidInput(String s) {
        if (s.isBlank()) return false;
        return (this.allowedValues.isEmpty() || this.allowedValues.contains(s)) && !this.currentValues.contains(s);
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
            boolean empty = index >= this.currentValues.size();
            if (empty) {
                message = Component.empty();
            } else {
                message = Component.literal(this.currentValues.get(index));
            }
            clickableLabel.setMessage(message);
            clickableLabel.visible = !empty;
        }
        this.updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        this.navigationButtons[0].active = this.currentValuesPage > 0;
        this.navigationButtons[1].active = this.currentValuesPage < (this.currentValues.size() - 1) / VALUES_PER_PAGE;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        this.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderLabels(guiGraphics, mouseX, mouseY);
    }

    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos + this.imageWidth - 3 - 27, this.topPos - 24, this.imageWidth, 0, 27, 24);
    }

    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.leftPos + (this.imageWidth - this.font.width(this.title)) / 2, this.topPos + 6, 4210752, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }

        if (keyCode == InputConstants.KEY_TAB && this.editBox.canConsumeInput()) {
            int increment = hasShiftDown() ? -1 : 1;
            String s = this.editBox.getValue();
            if (this.isValidInput(s)) {
                List<String> suggestions = this.allowedValues.stream().filter(Predicate.not(this.currentValues::contains)).toList();
                int index = suggestions.indexOf(s);
                if (index != -1) {
                    this.editBox.setValue(suggestions.get(((index + increment) % suggestions.size() + suggestions.size()) % suggestions.size()));
                }
            } else {
                String suggestion = this.allowedValues.stream()
                        .filter(Predicate.not(this.currentValues::contains))
                        .filter(value -> value.startsWith(s))
                        .findFirst()
                        .orElse(s);
                this.editBox.setValue(suggestion);
            }
            return true;
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

    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {

    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
        this.updateWhitelistButtons();
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
