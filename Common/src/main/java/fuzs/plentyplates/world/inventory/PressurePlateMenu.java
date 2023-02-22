package fuzs.plentyplates.world.inventory;

import fuzs.plentyplates.world.level.block.PressurePlateSetting;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.plentyplates.world.level.block.entity.PressurePlateBlockEntity;
import fuzs.puzzleslib.init.builder.ModMenuSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PressurePlateMenu extends AbstractContainerMenu {
    private final ContainerData containerData;
    private final ContainerLevelAccess access;
    private final SensitivityMaterial material;

    public static ModMenuSupplier<?> create(SensitivityMaterial material) {
        return (containerId1, inventory) -> new PressurePlateMenu(material, containerId1, new SimpleContainerData(PressurePlateSetting.values().length), ContainerLevelAccess.NULL);
    }

    public PressurePlateMenu(SensitivityMaterial material, int containerId, ContainerData containerData, ContainerLevelAccess access) {
        super(material.getMenuType(), containerId);
        this.material = material;
        this.containerData = containerData;
        this.access = access;
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

    @Override
    public void setData(int id, int data) {
        super.setData(id, data);
        // required for whitelist/blacklist buttons
        this.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, this.material.getPressurePlateBlock());
    }
}
