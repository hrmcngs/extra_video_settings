package extravideoset.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.NeoForge;

import extravideoset.client.NameMasker;

/** NeoForge nameplate + chat hooks. Tab list is handled by mixin (see PlayerInfoMixin). */
public final class NameMaskEvents {

	private NameMaskEvents() {}

	public static void init() {
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, NameMaskEvents::onRenderNameTag);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, NameMaskEvents::onChatReceived);
	}

	private static void onRenderNameTag(RenderNameTagEvent event) {
		if (!(event.getEntity() instanceof Player p)) return;
		String name = p.getGameProfile().getName();
		if (!NameMasker.shouldMaskName(name)) return;
		event.setContent(NameMasker.maskName(name));
	}

	private static void onChatReceived(ClientChatReceivedEvent event) {
		Component msg = event.getMessage();
		Component masked = NameMasker.maskTextContainingPlayerNames(msg);
		if (masked != msg) event.setMessage(masked);
	}
}
