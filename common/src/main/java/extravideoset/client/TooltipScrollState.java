package extravideoset.client;

import java.util.Objects;

import net.minecraft.world.item.ItemStack;

/**
 * Vertical scroll offset applied to tooltips by ScreenTooltipScrollMixin.
 *
 * Shift+mouse-wheel updates {@link #offset}; the mixin pushes a matching
 * {@code translate(0, offset, 0)} onto the GuiGraphics pose for each
 * tooltip render. Offset resets when the hovered ItemStack changes so a
 * fresh tooltip always starts unscrolled.
 */
public final class TooltipScrollState {

	// Double accumulator so Mac trackpad's small fractional scroll deltas
	// (~0.05 per event) add up over many events instead of being truncated
	// to zero by an int cast at every addScroll call.
	private static double offset = 0;
	private static Object lastKey = null;

	private TooltipScrollState() {}

	public static int getOffset() { return (int) offset; }

	/** Reset the offset whenever the tooltip's target item changes. */
	public static void onTooltipFor(Object key) {
		Object stable = stableKeyOf(key);
		if (!Objects.equals(lastKey, stable)) {
			offset = 0;
			lastKey = stable;
		}
	}

	/** Cheap-but-stable key for an ItemStack — item + count + tag hash.
	 *  ItemStack itself uses identity equality, so the same logical stack
	 *  is a different instance each frame; we'd reset every tick. */
	private static Object stableKeyOf(Object key) {
		if (key instanceof ItemStack stack) {
			int tagHash = stack.getTag() == null ? 0 : stack.getTag().hashCode();
			return stack.getItem() + "@" + stack.getCount() + "@" + tagHash;
		}
		return key;
	}

	/** Shift+wheel up (positive scrollY) reveals upper content,
	 *  which means translating the rendered tooltip downward.
	 *  Pixels-per-unit gain. Mouse wheels report ±1 per click → 10px per step;
	 *  trackpads report ~0.05 per event and accumulate via the double field. */
	public static void addScroll(double scrollY) {
		offset += scrollY * 10.0;
		extravideoset.ExtraVideoSettingsMod.LOGGER.info("[EVS] tooltip scroll: in={} offset={}", scrollY, offset);
	}
}
