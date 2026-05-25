package extravideoset.client;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import net.minecraft.network.chat.Component;

import extravideoset.client.NameMasker;

/**
 * Fabric chat hook. Nameplate is handled by EntityRendererNameTagMixin,
 * tab list by PlayerInfoMixin (both in extravideoset.mixin).
 */
public final class NameMaskEvents {

	private NameMaskEvents() {}

	public static void init() {
		ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> {
			Component masked = NameMasker.maskTextContainingPlayerNames(message);
			return masked != null ? masked : message;
		});
	}
}
