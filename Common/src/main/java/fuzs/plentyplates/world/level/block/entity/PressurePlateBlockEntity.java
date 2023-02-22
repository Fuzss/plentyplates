package fuzs.plentyplates.world.level.block.entity;

import fuzs.plentyplates.init.ModRegistry;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.DirectionalPressurePlateBlock;
import fuzs.plentyplates.world.level.block.PressurePlateSetting;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.plentyplates.world.level.block.entity.data.DataStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PressurePlateBlockEntity extends BlockEntity implements MenuProvider {
    public static final String TAG_SETTINGS = "Settings";
    public static final String TAG_MATERIAL = "Material";
    public static final String TAG_OWNER = "Owner";

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
            return PressurePlateSetting.values().length;
        }
    };

    @Nullable
    private UUID owner;
    private SensitivityMaterial sensitivityMaterial;
    private DataStorage<?> dataStorage;
    private int settings = PressurePlateSetting.DEFAULT_SETTINGS;

    public PressurePlateBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModRegistry.PRESSURE_PLATE_BLOCK_ENTITY_TYPE.get(), blockPos, blockState);
    }

    public PressurePlateBlockEntity(SensitivityMaterial sensitivityMaterial, BlockPos blockPos, BlockState blockState) {
        this(blockPos, blockState);
        this.setSensitivityMaterial(sensitivityMaterial);
    }

    public void setSensitivityMaterial(SensitivityMaterial sensitivityMaterial) {
        this.sensitivityMaterial = sensitivityMaterial;
        this.dataStorage = new DataStorage<>(sensitivityMaterial.dataProvider.get());
    }

    public void setSettingsValue(int id, int value) {
        this.settings = this.settings & ~(1 << id) | (value & 1) << id;
        this.update(PressurePlateSetting.values()[id], value == 1);
        this.setChanged();
    }

    public int getSettingsValue(int id) {
        return this.settings >> id & 1;
    }

    public boolean getSettingsValue(PressurePlateSetting setting) {
        return this.getSettingsValue(setting.ordinal()) == 1;
    }

    public boolean allowedToAccess(Player player) {
        if (!player.getAbilities().mayBuild) return false;
        if (!this.getSettingsValue(PressurePlateSetting.LOCKED) && this.owner != null) {
            return this.owner.equals(player.getUUID());
        }
        return true;
    }

    private void update(PressurePlateSetting setting, boolean value) {
        switch (setting) {
            case ILLUMINATED -> {
                this.level.setBlock(this.worldPosition, this.getBlockState().setValue(DirectionalPressurePlateBlock.LIT, value), 3);
            }
            case SILENT -> this.level.setBlock(this.worldPosition, this.getBlockState().setValue(DirectionalPressurePlateBlock.SILENT, !value), 3);
            case SHROUDED -> this.level.setBlock(this.worldPosition, this.getBlockState().setValue(DirectionalPressurePlateBlock.SHROUDED, !value), 3);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pressure_plate");
    }

    public boolean permits(Entity entity) {
        if (entity instanceof LivingEntity livingEntity && this.getSettingsValue(PressurePlateSetting.BABY) && !livingEntity.isBaby()) return false;
        return this.dataStorage.permits(entity, this.getSettingsValue(PressurePlateSetting.WHITELIST));
    }

    public Collection<String> getAllowedValues() {
        return this.dataStorage.getAllowedValues();
    }

    public void setCurrentValues(List<String> values) {
        this.dataStorage.setCurrentValues(values);
        this.setChanged();
    }

    public List<String> getCurrentValues() {
        return this.dataStorage.getCurrentValues();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(TAG_OWNER)) {
            this.owner = tag.getUUID(TAG_OWNER);
        }
        this.settings = tag.getByte(TAG_SETTINGS);
        this.setSensitivityMaterial(SensitivityMaterial.values()[tag.getByte(TAG_MATERIAL)]);
        this.dataStorage.loadFrom(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.owner != null) {
            tag.putUUID(TAG_OWNER, this.owner);
        }
        tag.putByte(TAG_SETTINGS, (byte) this.settings);
        tag.putByte(TAG_MATERIAL, (byte) this.sensitivityMaterial.ordinal());
        this.dataStorage.saveTo(tag);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new PressurePlateMenu(this.sensitivityMaterial, containerId, this.dataAccess, ContainerLevelAccess.create(this.level, this.worldPosition));
    }
}
