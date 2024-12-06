package fuzs.plentyplates.data;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addRecipes(RecipeOutput recipeOutput) {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, material.getPressurePlateBlock())
                    .define('#', material.getMaterialBlock())
                    .pattern("##")
                    .unlockedBy(getHasName(material.getMaterialBlock()), has(material.getMaterialBlock()))
                    .save(recipeOutput);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, material.getPressurePlateBlock())
                    .requires(material.getPressurePlateBlock())
                    .unlockedBy(getHasName(material.getPressurePlateBlock()), has(material.getPressurePlateBlock()))
                    .save(recipeOutput, PlentyPlates.id("clear_" + material.id().getPath()));
        }
    }
}
