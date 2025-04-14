package fuzs.plentyplates.world.level.block;

import com.google.common.base.Preconditions;
import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.entity.data.ColorDataProvider;
import fuzs.plentyplates.world.level.block.entity.data.DataProvider;
import fuzs.plentyplates.world.level.block.entity.data.PlayerDataProvider;
import fuzs.plentyplates.world.level.block.entity.data.RegistryDataProvider;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum SensitivityMaterial implements StringRepresentable {
    OBSIDIAN(Blocks.OBSIDIAN, Player.class, PlayerDataProvider::new),
    COBBLESTONE(Blocks.COBBLESTONE, Entity.class, () -> RegistryDataProvider.entityType(false)),
    MOSSY_COBBLESTONE(Blocks.MOSSY_COBBLESTONE, ItemEntity.class, RegistryDataProvider::item),
    STONE_BRICKS(Blocks.STONE_BRICKS,
            Mob.class,
            () -> RegistryDataProvider.entityType(true),
            PressurePlateSetting.BABY),
    MOSSY_STONE_BRICKS(Blocks.MOSSY_STONE_BRICKS,
            Villager.class,
            RegistryDataProvider::villagerProfession,
            PressurePlateSetting.BABY),
    CHISELED_STONE_BRICKS(Blocks.CHISELED_STONE_BRICKS, Sheep.class, ColorDataProvider::new);

    public static final StringRepresentable.StringRepresentableCodec<SensitivityMaterial> CODEC = StringRepresentable.fromEnum(
            SensitivityMaterial::values);

    private final Block materialBlock;
    private final Class<? extends Entity> clazz;
    private final ResourceLocation textureLocation;
    private final PressurePlateSetting[] settings;
    public final Supplier<DataProvider<?>> dataProvider;
    private Block pressurePlateBlock;
    private MenuType<PressurePlateMenu> menuType;

    SensitivityMaterial(Block materialBlock, Class<? extends Entity> clazz, Supplier<DataProvider<?>> dataProvider, PressurePlateSetting... settings) {
        this(materialBlock,
                clazz,
                materialBlock.builtInRegistryHolder().key().location().withPrefix("block/"),
                dataProvider,
                settings);
    }

    SensitivityMaterial(Block materialBlock, Class<? extends Entity> clazz, ResourceLocation textureLocation, Supplier<DataProvider<?>> dataProvider, PressurePlateSetting... settings) {
        this.materialBlock = materialBlock;
        this.clazz = clazz;
        this.textureLocation = textureLocation;
        this.dataProvider = dataProvider;
        this.settings = ArrayUtils.addAll(PressurePlateSetting.defaultValues(), settings);
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public Block getPressurePlateBlock() {
        if (this.pressurePlateBlock == null) {
            Preconditions.checkArgument(BuiltInRegistries.BLOCK.containsKey(this.id()));
            return this.pressurePlateBlock = BuiltInRegistries.BLOCK.getValue(this.id());
        } else {
            return this.pressurePlateBlock;
        }
    }

    @SuppressWarnings("unchecked")
    public MenuType<PressurePlateMenu> getMenuType() {
        if (this.menuType == null) {
            Preconditions.checkArgument(BuiltInRegistries.MENU.containsKey(this.id()));
            this.menuType = (MenuType<PressurePlateMenu>) BuiltInRegistries.MENU.getValue(this.id());
        }
        return this.menuType;
    }

    public ResourceLocation id() {
        return PlentyPlates.id(this.getSerializedName() + "_pressure_plate");
    }

    public String getTranslationKey() {
        return Util.makeDescriptionId("block", this.id());
    }

    public String getDescriptionKey() {
        return this.getTranslationKey() + ".entities";
    }

    public ResourceLocation getTextureLocation() {
        return this.textureLocation;
    }

    public ResourceLocation getFullTextureLocation() {
        return this.getTextureLocation().withPath((String string) -> "textures/" + string + ".png");
    }

    public ResourceLocation getTranslucentTextureLocation() {
        return PlentyPlates.id(this.getTextureLocation().getPath()).withSuffix("_translucent");
    }

    public ResourceLocation getFullTranslucentTextureLocation() {
        return this.getTranslucentTextureLocation().withPath((String string) -> "textures/" + string + ".png");
    }

    public Block getMaterialBlock() {
        return this.materialBlock;
    }

    public Class<? extends Entity> getClazz() {
        return this.clazz;
    }

    public PressurePlateSetting[] getSettings() {
        return this.settings;
    }

    public static Block[] allBlocks() {
        return Stream.of(values()).map(SensitivityMaterial::getPressurePlateBlock).toArray(Block[]::new);
    }
}
