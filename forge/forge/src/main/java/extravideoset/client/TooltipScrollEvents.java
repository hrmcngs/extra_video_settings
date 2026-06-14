package extravideoset.client;

import net.minecraft.client.gui.screens.Screen;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import extravideoset.ExtraVideoSettingsMod;

/**
 * Forge scroll capture for tooltip scrolling.
 *
 * The mixin approach (hooking {@code MouseHandler.onScroll}) wasn't firing for
 * reasons that weren't worth tracking down — Forge's
 * {@link ScreenEvent.MouseScrolled.Pre} fires reliably for every screen scroll
 * and lets us consume the event with {@code setCanceled(true)}.
 *
 * The GuiGraphics render-side mixin still applies the visual offset.
 */
public final class TooltipScrollEvents {

	private TooltipScrollEvents() {}

	public static void init() {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, TooltipScrollEvents::onScroll);
		ExtraVideoSettingsMod.LOGGER.info("[EVS] TooltipScrollEvents registered");
	}

	private static void onScroll(ScreenEvent.MouseScrolled.Pre event) {
		if (!Screen.hasShiftDown()) return;
		double dy = event.getScrollDelta();
		if (dy == 0) return;
		TooltipScrollState.addScroll(dy);
		event.setCanceled(true);
	}
}
