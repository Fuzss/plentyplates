package fuzs.plentyplates.data;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.data.v1.AbstractRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, material.getPressurePlateBlock())
                    .define('#', material.getMaterialBlock())
                    .pattern("##")
                    .unlockedBy(getHasName(material.getMaterialBlock()), has(material.getMaterialBlock()))
                    .save(recipeConsumer);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, material.getPressurePlateBlock())
                    .requires(material.getPressurePlateBlock())
                    .unlockedBy(getHasName(material.getPressurePlateBlock()), has(material.getPressurePlateBlock()))
                    .save(recipeConsumer, PlentyPlates.id("clear_" + material.id().getPath()));
        }
    }
}
