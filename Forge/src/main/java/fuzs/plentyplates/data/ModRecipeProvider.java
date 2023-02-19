package fuzs.plentyplates.data;

import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            ShapedRecipeBuilder.shaped(material.getPressurePlateBlock())
                    .define('#', material.getMaterialBlock())
                    .pattern("##")
                    .unlockedBy(getHasName(material.getMaterialBlock()), has(material.getMaterialBlock()))
                    .save(recipeConsumer);
        }
    }
}
