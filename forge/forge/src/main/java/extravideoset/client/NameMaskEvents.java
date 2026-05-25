package extravideoset.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import extravideoset.client.NameMasker;

/**
 * Forge nameplate + chat hooks. Tab list is handled by mixin (see PlayerInfoMixin).
 *
 * Chat: Forge 1.20.1 splits {@link ClientChatReceivedEvent} into
 *   - {@code System}  — death messages, /kill log, /give output, advancement broadcasts
 *   - {@code Player}  — normal player chat (`<player> message`)
 *
 * The eventbus dispatches by exact class; subclass instances do NOT reach
 * listeners registered on the parent. Register one listener per subclass
 * with the explicit {@code Class<T>} overload — reflective inference can be
 * fooled by nested classes (sometimes registers for the parent).
 */
public final class NameMaskEvents {

	private NameMaskEvents() {}

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, NameMaskEvents::onRenderNameTag);
		MinecraftForge.EVENT_BUS.addListener(
				EventPriority.LOWEST,
				/* receiveCanceled = */ false,
				ClientChatReceivedEvent.System.class,
				NameMaskEvents::onSystemChat);
		MinecraftForge.EVENT_BUS.addListener(
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
