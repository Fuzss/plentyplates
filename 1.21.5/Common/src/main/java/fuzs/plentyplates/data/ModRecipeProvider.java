package fuzs.plentyplates.data;

import fuzs.plentyplates.world.level.block.SensitivityMaterial;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.data.recipes.RecipeOutput;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addRecipes(RecipeOutput recipeOutput) {
        for (SensitivityMaterial material : SensitivityMaterial.values()) {
            this.pressurePlate(material.getBlock(), material.getMaterialBlock());
        }
    }
}
