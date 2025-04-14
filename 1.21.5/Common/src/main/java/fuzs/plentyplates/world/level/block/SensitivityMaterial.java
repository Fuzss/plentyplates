package fuzs.plentyplates.world.level.block;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.init.ModRegistry;
import fuzs.plentyplates.world.level.block.entity.data.ColorDataProvider;
import fuzs.plentyplates.world.level.block.entity.data.DataProvider;
import fuzs.plentyplates.world.level.block.entity.data.PlayerDataProvider;
import fuzs.plentyplates.world.level.block.entity.data.RegistryDataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum SensitivityMaterial implements StringRepresentable {
    OBSIDIAN(Blocks.OBSIDIAN, Player.class, PlayerDataProvider::new) {
        @Override
        public Block getBlock() {
            return ModRegistry.OBSIDIAN_PRESSURE_PLATE_BLOCK.value();
        }
    },
    COBBLESTONE(Blocks.COBBLESTONE, Entity.class, () -> RegistryDataProvider.entityType(false)) {
        @Override
        public Block getBlock() {
            return ModRegistry.COBBLESTONE_PRESSURE_PLATE_BLOCK.value();
        }
    },
    MOSSY_COBBLESTONE(Blocks.MOSSY_COBBLESTONE, ItemEntity.class, RegistryDataProvider::item) {
        @Override
        public Block getBlock() {
            return ModRegistry.MOSSY_COBBLESTONE_PRESSURE_PLATE_BLOCK.value();
        }
    },
    STONE_BRICKS("stone_brick",
            Blocks.STONE_BRICKS,
            Mob.class,
            () -> RegistryDataProvider.entityType(true),
            PressurePlateSetting.BABY) {
        @Override
        public Block getBlock() {
            return ModRegistry.STONE_BRICK_PRESSURE_PLATE_BLOCK.value();
        }
    },
    MOSSY_STONE_BRICKS("mossy_stone_brick",
            Blocks.MOSSY_STONE_BRICKS,
            Villager.class,
            RegistryDataProvider::villagerProfession,
            PressurePlateSetting.BABY) {
        @Override
        public Block getBlock() {
            return ModRegistry.MOSSY_STONE_BRICK_PRESSURE_PLATE_BLOCK.value();
        }
    },
    CHISELED_STONE_BRICKS("chiseled_stone_brick", Blocks.CHISELED_STONE_BRICKS, Sheep.class, ColorDataProvider::new) {
        @Override
        public Block getBlock() {
            return ModRegistry.CHISELED_STONE_BRICK_PRESSURE_PLATE_BLOCK.value();
        }
    };

    public static final StringRepresentable.StringRepresentableCodec<SensitivityMaterial> CODEC = StringRepresentable.fromEnum(
            SensitivityMaterial::values);

    private final String name;
    private final Block block;
    private final Class<? extends Entity> entityClass;
    private final ResourceLocation textureLocation;
    private final PressurePlateSetting[] settings;
    public final Supplier<DataProvider<?>> dataProvider;

    SensitivityMaterial(Block block, Class<? extends Entity> entityClass, Supplier<DataProvider<?>> dataProvider, PressurePlateSetting... settings) {
        this(null, block, entityClass, dataProvider, settings);
    }

    SensitivityMaterial(@Nullable String name, Block block, Class<? extends Entity> entityClass, Supplier<DataProvider<?>> dataProvider, PressurePlateSetting... settings) {
        this(name,
                block,
                entityClass,
                block.builtInRegistryHolder().key().location().withPrefix("block/"),
                dataProvider,
                settings);
    }

    SensitivityMaterial(@Nullable String name, Block block, Class<? extends Entity> entityClass, ResourceLocation textureLocation, Supplier<DataProvider<?>> dataProvider, PressurePlateSetting... settings) {
        this.name = name != null ? name : this.name().toLowerCase(Locale.ROOT);
        this.block = block;
        this.entityClass = entityClass;
        this.textureLocation = textureLocation;
        this.dataProvider = dataProvider;
        this.settings = ArrayUtils.addAll(PressurePlateSetting.defaultValues(), settings);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public abstract Block getBlock();

    public String getEntityDescriptionKey() {
        return this.getBlock().getDescriptionId() + ".tooltip.entities";
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
        return this.block;
    }

    public Class<? extends Entity> getEntityClass() {
        return this.entityClass;
    }

    public PressurePlateSetting[] getSettings() {
        return this.settings;
    }

    public static Block[] allBlocks() {
        return Stream.of(values()).map(SensitivityMaterial::getBlock).toArray(Block[]::new);
    }
}
