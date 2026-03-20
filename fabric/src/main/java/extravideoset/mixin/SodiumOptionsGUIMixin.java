package extravideoset.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.Screen;

import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;

import extravideoset.client.SodiumIntegration;

@Mixin(value = SodiumOptionsGUI.class, remap = false)
public abstract class SodiumOptionsGUIMixin {

	@Shadow
	private List<OptionPage> pages;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void evs$addExtraPage(Screen prevScreen, CallbackInfo ci) {
		this.pages.add(SodiumIntegration.createPage());
	}
}
