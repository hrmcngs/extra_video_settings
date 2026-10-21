package extravideoset.client;

import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import java.util.stream.Stream;

/** Minecraft 1.20.2 wraps each recipe in a RecipeHolder. */
public final class RecipeSearchNames {
    private RecipeSearchNames() {}

    public static Stream<String> names(RecipeCollection recipes) {
        return recipes.getRecipes().stream().flatMap(holder -> MultilingualSearch.names(
                holder.value().getResultItem(recipes.registryAccess())).stream());
    }
}
