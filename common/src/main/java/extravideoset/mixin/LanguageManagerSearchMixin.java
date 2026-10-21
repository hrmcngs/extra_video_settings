package extravideoset.mixin;

import extravideoset.client.MultilingualSearch;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageManager.class)
public abstract class LanguageManagerSearchMixin {
    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    private void evs$reloadSearchLanguages(ResourceManager resources, CallbackInfo ci) {
        MultilingualSearch.reload(resources);
    }
}
