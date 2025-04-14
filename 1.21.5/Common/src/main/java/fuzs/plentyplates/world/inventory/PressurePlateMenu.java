package fuzs.plentyplates.world.inventory;

import fuzs.plentyplates.init.ModRegistry;
import fuzs.plentyplates.world.level.block.PressurePlateSetting;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.plentyplates.world.level.block.entity.PressurePlateBlockEntity;
import fuzs.puzzleslib.api.network.v4.codec.ExtraStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PressurePlateMenu extends AbstractContainerMenu {
    private final ContainerData containerData;
    private final ContainerLevelAccess access;
    private final SensitivityMaterial material;
    private final Collection<String> allowedValues;
    private final List<String> currentValues;

    public PressurePlateMenu(int containerId, Inventory inventory, Data data) {
        this(data.sensitivityMaterial(),
                containerId,
                new SimpleContainerData(PressurePlateSetting.values().length),
                ContainerLevelAccess.NULL,
                data.allowedValues(),
                data.currentValues());
    }

    public PressurePlateMenu(SensitivityMaterial material, int containerId, ContainerData containerData, ContainerLevelAccess access) {
        this(material, containerId, containerData, access, Collections.emptySet(), Collections.emptyList());
    }

    private PressurePlateMenu(SensitivityMaterial material, int containerId, ContainerData containerData, ContainerLevelAccess access, Collection<String> allowedValues, List<String> currentValues) {
        super(ModRegistry.PRESSURE_PLATE_MENU_TYPE.value(), containerId);
        this.material = material;
        this.containerData = containerData;
        this.access = access;
        this.allowedValues = allowedValues;
        this.currentValues = currentValues;
        this.addDataSlots(containerData);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        this.containerData.set(id, (this.containerData.get(id) + 1) % 2);
        return true;
    }

    public boolean getSettingsValue(PressurePlateSetting setting) {
        return this.containerData.get(setting.ordinal()) == 1;
    }

    public void setCurrentValues(List<String> values) {
        this.access.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof PressurePlateBlockEntity blockEntity) {
                blockEntity.setCurrentValues(values);
            }
        });
    }

    public SensitivityMaterial getMaterial() {
        return this.material;
    }

    public Collection<String> getAllowedValues() {
        return this.allowedValues;
    }

    public List<String> getCurrentValues() {
        return this.currentValues;
    }

    @Override
    public void setData(int id, int data) {
        super.setData(id, data);
        // required for whitelist / blacklist buttons
        this.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, this.material.getBlock());
    }

    public record Data(SensitivityMaterial sensitivityMaterial,
                       Collection<String> allowedValues,
                       List<String> currentValues) {
        public static final StreamCodec<ByteBuf, Data> STREAM_CODEC = StreamCodec.composite(ExtraStreamCodecs.fromEnum(
                        SensitivityMaterial.class),
                Data::sensitivityMaterial,
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(ArrayList::new)),
                Data::allowedValues,
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
                Data::currentValues,
                Data::new);
    }
}
