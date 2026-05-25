package extravideoset.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import extravideoset.client.NameMasker;

/**
 * Tab-list hook: when the tab-list renderer asks each PlayerInfo for its
 * display name, return the masked variant if the user enabled name masking.
 *
 * Vanilla returns the server-provided tab display name (nullable, falls back
 * to the profile name). We override at HEAD so the masked text wins.
 */
@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin {

	@Inject(method = "getTabListDisplayName", at = @At("HEAD"), cancellable = true)
	private void evs$maskTabListName(CallbackInfoReturnable<Component> cir) {
		PlayerInfo self = (PlayerInfo) (Object) this;
		GameProfile profile = self.getProfile();
		String name = profile != null ? profile.getName() : null;
		if (!NameMasker.shouldMaskName(name)) return;
		cir.setReturnValue(NameMasker.maskName(name));
	}
}
