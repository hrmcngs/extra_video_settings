package extravideoset.mixin;

import extravideoset.client.MultilingualSearch;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Optional JEI 15 integration, without making JEI a required dependency. */
@Pseudo
@Mixin(targets = "mezz.jei.gui.ingredients.DisplayNameUtil", remap = false)
public abstract class JeiSearchMixin {
    @Inject(method = "getLowercaseDisplayNameForSearch", at = @At("RETURN"), cancellable = true, require = 0)
    private static void evs$addNames(Object ingredient, @Coerce Object helper,
            CallbackInfoReturnable<String> cir) {
        if (ingredient instanceof ItemStack stack) {
            var names = MultilingualSearch.names(stack);
            if (!names.isEmpty()) {
                cir.setReturnValue(cir.getReturnValue() + "\n" + String.join("\n", names));
            }
        }
    }
}
