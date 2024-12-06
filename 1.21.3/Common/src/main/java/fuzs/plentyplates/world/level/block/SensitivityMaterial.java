package fuzs.plentyplates.world.level.block;

import com.google.common.base.Preconditions;
import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.inventory.PressurePlateMenu;
import fuzs.plentyplates.world.level.block.entity.data.ColorDataProvider;
import fuzs.plentyplates.world.level.block.entity.data.DataProvider;
import fuzs.plentyplates.world.level.block.entity.data.PlayerDataProvider;
import fuzs.plentyplates.world.level.block.entity.data.RegistryDataProvider;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.Supplier;
import java.util.stream.Stream;

public enum SensitivityMaterial implements StringRepresentable {
    OBSIDIAN("obsidian", Blocks.OBSIDIAN, Player.class, ResourceLocationHelper.withDefaultNamespace("block/obsidian"),
            PlayerDataProvider::new
    ),
    COBBLESTONE("cobblestone", Blocks.COBBLESTONE, Entity.class,
            ResourceLocationHelper.withDefaultNamespace("block/cobblestone"),
            () -> RegistryDataProvider.entityType(false)
    ),
    MOSSY_COBBLESTONE("mossy_cobblestone", Blocks.MOSSY_COBBLESTONE, ItemEntity.class,
            ResourceLocationHelper.withDefaultNamespace("block/mossy_cobblestone"), RegistryDataProvider::item
    ),
    STONE_BRICKS("stone_bricks", Blocks.STONE_BRICKS, Mob.class,
            ResourceLocationHelper.withDefaultNamespace("block/stone_bricks"),
            () -> RegistryDataProvider.entityType(true), PressurePlateSetting.BABY
    ),
    MOSSY_STONE_BRICKS("mossy_stone_bricks", Blocks.MOSSY_STONE_BRICKS, Villager.class,
            ResourceLocationHelper.withDefaultNamespace("block/mossy_stone_bricks"),
            RegistryDataProvider::villagerProfession, PressurePlateSetting.BABY
    ),
    CHISELED_STONE_BRICKS("chiseled_stone_bricks", Blocks.CHISELED_STONE_BRICKS, Sheep.class,
            ResourceLocationHelper.withDefaultNamespace("block/chiseled_stone_bricks"), ColorDataProvider::new
    );

    public static final StringRepresentable.EnumCodec<SensitivityMaterial> CODEC = StringRepresentable.fromEnum(
            SensitivityMaterial::values);

    private final String name;
    private final Block materialBlock;
    private final Class<? extends Entity> clazz;
    private final ResourceLocation texture;
    private final PressurePlateSetting[] settings;
    public final Supplier<DataProvider<?>> dataProvider;
    private Block pressurePlateBlock;
    private MenuType<PressurePlateMenu> menuType;

    SensitivityMaterial(String name, Block materialBlock, Class<? extends Entity> clazz, ResourceLocation texture, Supplier<DataProvider<?>> dataProvider, PressurePlateSetting... settings) {
        this.name = name;
        this.materialBlock = materialBlock;
        this.clazz = clazz;
        this.texture = texture;
        this.dataProvider = dataProvider;
        this.settings = ArrayUtils.addAll(PressurePlateSetting.defaultValues(), settings);
    }

    public Block getPressurePlateBlock() {
        if (this.pressurePlateBlock == null) {
            Preconditions.checkArgument(BuiltInRegistries.BLOCK.containsKey(this.id()));
            this.pressurePlateBlock = BuiltInRegistries.BLOCK.getValue(this.id());
        }
        return this.pressurePlateBlock;
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
        return PlentyPlates.id(this.name + "_pressure_plate");
    }

    public String translationKey() {
        return Util.makeDescriptionId("block", this.id());
    }

    public String descriptionKey() {
        return this.translationKey() + ".entities";
    }

    public ResourceLocation getModelTexture() {
        return this.texture;
    }

    public ResourceLocation getTextureFile() {
        return expandToFile(this.getModelTexture());
    }

    public ResourceLocation getTranslucentModelTexture() {
        StringBuilder builder = new StringBuilder(this.texture.getPath());
        int index = builder.lastIndexOf("/");
        // handles -1 too due to ++index which we need anyway
        return PlentyPlates.id(builder.insert(++index, "translucent_").toString());
    }

    public ResourceLocation getTranslucentTextureFile() {
        return expandToFile(this.getTranslucentModelTexture());
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static ResourceLocation expandToFile(ResourceLocation texture) {
        return ResourceLocationHelper.fromNamespaceAndPath(texture.getNamespace(),
                "textures/" + texture.getPath() + ".png"
        );
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
