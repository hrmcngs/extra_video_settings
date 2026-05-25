package extravideoset.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.NeoForge;

import extravideoset.client.NameMasker;

/**
 * NeoForge nameplate + chat hooks. Tab list is handled by mixin (see PlayerInfoMixin).
 *
 * Chat: NeoForge 1.20.2 splits {@link ClientChatReceivedEvent} into
 *   - {@code System}  — death messages, /kill log, /give output, advancement broadcasts
 *   - {@code Player}  — normal player chat (`<player> message`)
 *
 * The eventbus dispatches by exact class; subclass instances do NOT reach
 * listeners registered on the parent. We register one listener per subclass
 * AND use the explicit {@code Class<T>} addListener overload because
 * NeoForge's reflective type inference can be fooled by nested classes
 * (sometimes it ends up registering for the parent).
 */
public final class NameMaskEvents {

	private NameMaskEvents() {}

	public static void init() {
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, NameMaskEvents::onRenderNameTag);
		NeoForge.EVENT_BUS.addListener(
				EventPriority.LOWEST,
				/* receiveCanceled = */ false,
				ClientChatReceivedEvent.System.class,
				NameMaskEvents::onSystemChat);
		NeoForge.EVENT_BUS.addListener(
				EventPriority.LOWEST,
				/* receiveCanceled = */ false,
				ClientChatReceivedEvent.Player.class,
				NameMaskEvents::onPlayerChat);
	}

	private static void onRenderNameTag(RenderNameTagEvent event) {
		if (!(event.getEntity() instanceof Player p)) return;
		String name = p.getGameProfile().getName();
		if (!NameMasker.shouldMaskName(name)) return;
		event.setContent(NameMasker.maskName(name));
	}

	private static void onSystemChat(ClientChatReceivedEvent.System event) {
		applyMask(event);
	}

	private static void onPlayerChat(ClientChatReceivedEvent.Player event) {
		applyMask(event);
	}

	private static void applyMask(ClientChatReceivedEvent event) {
		Component msg = event.getMessage();
		Component masked = NameMasker.maskTextContainingPlayerNames(msg);
		if (masked != msg) event.setMessage(masked);
	}
}
