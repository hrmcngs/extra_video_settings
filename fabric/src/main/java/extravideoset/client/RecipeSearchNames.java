package extravideoset.client;

import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import java.util.stream.Stream;

/** Minecraft 1.20.1 recipe results. */
public final class RecipeSearchNames {
    private RecipeSearchNames() {}

    public static Stream<String> names(RecipeCollection recipes) {
        return recipes.getRecipes().stream().flatMap(recipe -> MultilingualSearch.names(
                recipe.getResultItem(recipes.registryAccess())).stream());
    }
}
