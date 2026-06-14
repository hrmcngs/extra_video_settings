package extravideoset.client;

import net.minecraft.client.gui.screens.Screen;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import extravideoset.ExtraVideoSettingsMod;

/**
 * NeoForge scroll capture for tooltip scrolling.
 * See the Forge equivalent for context; same idea, NeoForge event bus.
 */
public final class TooltipScrollEvents {

	private TooltipScrollEvents() {}

	public static void init() {
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, TooltipScrollEvents::onScroll);
		ExtraVideoSettingsMod.LOGGER.info("[EVS] TooltipScrollEvents registered");
	}

	private static void onScroll(ScreenEvent.MouseScrolled.Pre event) {
		if (!Screen.hasShiftDown()) return;
		double dy = event.getScrollDeltaY();
		if (dy == 0) return;
		TooltipScrollState.addScroll(dy);
		event.setCanceled(true);
	}
}
