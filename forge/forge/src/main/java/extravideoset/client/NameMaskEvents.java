package extravideoset.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import extravideoset.ExtraVideoSettingsMod;
import extravideoset.client.NameMasker;

/**
 * Forge nameplate + chat hooks. Tab list is handled by mixin (see PlayerInfoMixin).
 *
 * Chat handling note: in 1.20.1 {@link ClientChatReceivedEvent} is abstract with two
 * concrete subclasses ({@code System}, {@code Player}). We use the annotation-based
 * registration so Forge walks the class hierarchy and dispatches both subclasses
 * to our single parent listener.
 */
public final class NameMaskEvents {

	private NameMaskEvents() {}

	public static void init() {
		MinecraftForge.EVENT_BUS.register(NameMaskEvents.class);
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
