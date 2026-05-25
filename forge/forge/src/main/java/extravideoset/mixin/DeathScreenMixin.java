package extravideoset.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;

import extravideoset.client.NameMasker;

/**
 * Rewrite the death-cause Component passed to DeathScreen so the player's
 * own name (e.g. "Devは殺された") is hidden when the user has enabled the
 * "include self" toggle. Death-screen text is a separate Component from the
 * chat broadcast, so the chat hook doesn't cover it.
 */
@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin {

	@ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private static Component evs$maskCauseOfDeath(Component cause) {
		System.err.println("[EVS-DEBUG] DeathScreenMixin fired. cause="
				+ (cause == null ? "null" : cause.getString())
				+ " othersMode=" + extravideoset.client.NameMaskConfig.getMode()
				+ " selfMode=" + extravideoset.client.NameMaskConfig.getSelfMode());
		if (cause == null) return cause;
		Component masked = NameMasker.maskTextContainingPlayerNames(cause);
		System.err.println("[EVS-DEBUG] maskedText=" + masked.getString());
		return masked;
	}
}
