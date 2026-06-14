package extravideoset.client;

import net.minecraft.client.gui.screens.Screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

import extravideoset.ExtraVideoSettingsMod;

/**
 * Fabric scroll capture for tooltip scrolling.
 *
 * Fabric API exposes scroll-handling per-screen via {@link ScreenMouseEvents}:
 * register an {@code allowMouseScroll} listener on every screen as it inits,
 * return false to swallow the scroll (so vanilla / Sodium / etc. don't react).
 */
public final class TooltipScrollEvents {

	private TooltipScrollEvents() {}

	public static void init() {
		ScreenEvents.AFTER_INIT.register((client, screen, w, h) ->
				ScreenMouseEvents.allowMouseScroll(screen).register(TooltipScrollEvents::onScroll));
		ExtraVideoSettingsMod.LOGGER.info("[EVS] TooltipScrollEvents registered");
	}

	private static boolean onScroll(Screen screen, double mouseX, double mouseY,
			double horizontalAmount, double verticalAmount) {
		if (!Screen.hasShiftDown()) return true;
		double amount = (Math.abs(horizontalAmount) > Math.abs(verticalAmount))
				? horizontalAmount : verticalAmount;
		if (amount == 0) return true;
		TooltipScrollState.addScroll(amount);
		return false;
	}
}
