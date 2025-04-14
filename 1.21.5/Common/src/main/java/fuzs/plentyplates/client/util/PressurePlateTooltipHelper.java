package fuzs.plentyplates.client.util;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.Locale;
import java.util.function.Consumer;

public class PressurePlateTooltipHelper {

    public static void appendHoverText(DirectionalPressurePlateBlock block, ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, Consumer<Component> tooltipLineConsumer) {
        tooltipLineConsumer.accept(Component.translatable(TooltipComponent.ACTIVATED_BY.getTranslationKey(),
                Component.translatable(block.getSensitivityMaterial().getEntityDescriptionKey())
                        .withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GREEN));
        Options options = Minecraft.getInstance().options;
        Component shiftComponent = Component.keybind(options.keyShift.getName()).withStyle(ChatFormatting.LIGHT_PURPLE);
        Component useComponent = Component.keybind(options.keyUse.getName()).withStyle(ChatFormatting.LIGHT_PURPLE);
        Component component = Component.translatable(TooltipComponent.DESCRIPTION.getTranslationKey(),
                shiftComponent,
                useComponent).withStyle(ChatFormatting.GRAY);
        tooltipLineConsumer.accept(component);
    }

    public enum TooltipComponent implements StringRepresentable {
        DESCRIPTION,
        ACTIVATED_BY;

        public String getTranslationKey() {
            return Util.makeDescriptionId(Registries.elementsDirPath(Registries.BLOCK),
                    PlentyPlates.id("pressure_plate")) + ".tooltip." + this.getSerializedName();
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
