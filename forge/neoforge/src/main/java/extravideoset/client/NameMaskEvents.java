package extravideoset.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.NeoForge;

import extravideoset.ExtraVideoSettingsMod;
import extravideoset.client.NameMasker;

/**
 * NeoForge nameplate + chat hooks. Tab list is handled by mixin (see PlayerInfoMixin).
 *
 * Chat handling note: NeoForge 1.20.2 makes {@link ClientChatReceivedEvent} abstract
 * with concrete subclasses ({@code System}, {@code Player}). We use the
 * annotation-based registration so the bus walks the class hierarchy and dispatches
 * both subclasses to our single parent listener.
 */
public final class NameMaskEvents {

	private NameMaskEvents() {}

	public static void init() {
		NeoForge.EVENT_BUS.register(NameMaskEvents.class);
		ExtraVideoSettingsMod.LOGGER.info("[EVS] NameMaskEvents registered");
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRenderNameTag(RenderNameTagEvent event) {
		if (!(event.getEntity() instanceof Player p)) return;
		String name = p.getGameProfile().getName();
		if (!NameMasker.shouldMaskName(name)) return;
		event.setContent(NameMasker.maskName(name));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onChat(ClientChatReceivedEvent event) {
		Component msg = event.getMessage();
		Component masked = NameMasker.maskTextContainingPlayerNames(msg);
		ExtraVideoSettingsMod.LOGGER.info("[EVS] chat({}) changed={} in='{}' out='{}'",
				event.getClass().getSimpleName(), masked != msg,
				msg.getString(), masked.getString());
		if (masked != msg) event.setMessage(masked);
	}
}
