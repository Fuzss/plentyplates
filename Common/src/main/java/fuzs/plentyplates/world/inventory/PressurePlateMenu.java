package fuzs.plentyplates.world.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class PressurePlateMenu extends AbstractContainerMenu {
    private final ContainerData containerData;
    private final ContainerLevelAccess access;

    public PressurePlateMenu(int containerId, ContainerData containerData, ContainerLevelAccess access) {
        super(menuType, containerId);
        this.containerData = containerData;
        this.access = access;
        this.addDataSlots(containerData);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, Blocks.BEACON);
    }
}
