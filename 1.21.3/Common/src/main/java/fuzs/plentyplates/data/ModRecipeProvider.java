package fuzs.plentyplates.data;

import fuzs.plentyplates.PlentyPlates;
import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addRecipes(RecipeOutput recipeOutput) {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            ShapedRecipeBuilder.shaped(this.items(), RecipeCategory.REDSTONE, material.getPressurePlateBlock())
                    .define('#', material.getMaterialBlock())
                    .pattern("##")
                    .unlockedBy(getHasName(material.getMaterialBlock()), this.has(material.getMaterialBlock()))
                    .save(recipeOutput);
            ShapelessRecipeBuilder.shapeless(this.items(), RecipeCategory.REDSTONE, material.getPressurePlateBlock())
                    .requires(material.getPressurePlateBlock())
                    .unlockedBy(getHasName(material.getPressurePlateBlock()),
                            this.has(material.getPressurePlateBlock()))
                    .save(recipeOutput,
                            ResourceKey.create(Registries.RECIPE, PlentyPlates.id("clear_" + material.id().getPath())));
        }
    }
}
