package fuzs.plentyplates.world.level.block;

import com.google.common.base.Preconditions;
import fuzs.plentyplates.PlentyPlates;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.stream.Stream;

public enum SensitivityMaterial {
    OBSIDIAN("obsidian", Blocks.OBSIDIAN, Player.class, new ResourceLocation("block/obsidian")),
    DEEPSLATE("deepslate", Blocks.DEEPSLATE, Mob.class, new ResourceLocation("block/deepslate")),
    CALCITE("calcite", Blocks.CALCITE, Animal.class, new ResourceLocation("block/calcite")),
    TUFF("tuff", Blocks.TUFF, Villager.class, new ResourceLocation("block/tuff")),
    SMOOTH_BASALT("smooth_basalt", Blocks.SMOOTH_BASALT, Sheep.class, new ResourceLocation("block/smooth_basalt"));

    private final ResourceLocation id;
    private final Block materialBlock;
    private final Class<? extends Entity> clazz;
    private final ResourceLocation texture;
    private Block pressurePlateBlock;

    SensitivityMaterial(String name, Block materialBlock, Class<? extends Entity> clazz, ResourceLocation texture) {
        this.id = PlentyPlates.id(name + "_pressure_plate");
        this.materialBlock = materialBlock;
        this.clazz = clazz;
        this.texture = texture;
    }

    public Block getPressurePlateBlock() {
        if (this.pressurePlateBlock == null) {
            Preconditions.checkArgument(Registry.BLOCK.containsKey(this.id()));
            this.pressurePlateBlock = Registry.BLOCK.get(this.id());
        }
        return this.pressurePlateBlock;
    }

    public ResourceLocation id() {
        return this.id;
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

    private static ResourceLocation expandToFile(ResourceLocation texture) {
        return new ResourceLocation(texture.getNamespace(), "textures/" + texture.getPath() + ".png");
    }

    public Block getMaterialBlock() {
        return this.materialBlock;
    }

    public Class<? extends Entity> getClazz() {
        return this.clazz;
    }

    public static Block[] allBlocks() {
        return Stream.of(values()).map(SensitivityMaterial::getPressurePlateBlock).toArray(Block[]::new);
    }
}
