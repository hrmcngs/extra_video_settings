package extravideoset.mixin;

import extravideoset.client.MultilingualSearch;
import extravideoset.client.RecipeSearchNames;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(FullTextSearchTree.class)
public abstract class FullTextSearchTreeMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static Function<Object, Stream<String>> evs$addNames(Function<Object, Stream<String>> original) {
        return value -> {
            Stream<String> aliases = Stream.empty();
            if (value instanceof ItemStack stack) {
                aliases = MultilingualSearch.names(stack).stream();
            } else if (value instanceof RecipeCollection recipes) {
                aliases = RecipeSearchNames.names(recipes);
            }
            return Stream.concat(original.apply(value), aliases);
        };
    }
}
