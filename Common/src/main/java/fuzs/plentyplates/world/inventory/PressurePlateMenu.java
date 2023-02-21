package fuzs.plentyplates.world.inventory;

import fuzs.plentyplates.world.level.block.PressurePlateSetting;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.init.builder.ModMenuSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class PressurePlateMenu extends AbstractContainerMenu {
    private final ContainerData containerData;
    private final ContainerLevelAccess access;
    private final SensitivityMaterial material;

    public static ModMenuSupplier<?> create(SensitivityMaterial material) {
        return (containerId1, inventory) -> new PressurePlateMenu(material, containerId1, new SimpleContainerData(PressurePlateSetting.values().length + 1), ContainerLevelAccess.NULL);
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

    public boolean getWhitelistSetting() {
        return this.containerData.get(0) == 1;
    }

    public boolean getSettingsValue(PressurePlateSetting setting) {
        return this.containerData.get(setting.getId() + 1) == 1;
    }

    public SensitivityMaterial getMaterial() {
        return this.material;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
//        return stillValid(this.access, player, Blocks.BEACON);
    }
}
