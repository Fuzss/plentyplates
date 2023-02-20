package fuzs.plentyplates.world.level.block.entity;

import fuzs.plentyplates.init.ModRegistry;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PressurePlateBlockEntity extends BlockEntity implements MenuProvider {
    public static final String TAG_SETTINGS = "Settings";

    private final ContainerData dataAccess = new ContainerData() {

        @Override
        public int get(int index) {
            return PressurePlateBlockEntity.this.getSettingsValue(index);
        }

        @Override
        public void set(int index, int value) {
            PressurePlateBlockEntity.this.setSettingsValue(index, value);
        }

        @Override
        public int getCount() {
            return PressurePlateBlockEntity.this.sensitivityMaterial.getSettingsAmount() + 1;
        }
    };

    private SensitivityMaterial sensitivityMaterial;
    private int settings;

    public PressurePlateBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModRegistry.PRESSURE_PLATE_BLOCK_ENTITY_TYPE.get(), blockPos, blockState);
    }

    public PressurePlateBlockEntity(SensitivityMaterial sensitivityMaterial, BlockPos blockPos, BlockState blockState) {
        this(blockPos, blockState);
        this.sensitivityMaterial = sensitivityMaterial;
    }

    public void setSettingsValue(int id, int value) {
        this.settings = this.settings & ~(1 << id) | (value & 1) << id;
    }

    public int getSettingsValue(int id) {
        return this.settings >> id & 1;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pressure_plate");
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.settings = tag.getInt(TAG_SETTINGS);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(TAG_SETTINGS, this.settings);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new PressurePlateMenu(containerId, this.dataAccess, ContainerLevelAccess.create(this.level, this.worldPosition));
    }
}
