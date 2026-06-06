package extravideoset.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;

import extravideoset.client.TooltipScrollState;

/**
 * Shift + mouse wheel intercepted at the GLFW callback site
 * ({@link MouseHandler#onScroll}). Updating the scroll offset and cancelling
 * the rest of {@code onScroll} prevents the open screen from also reacting
 * (e.g. Sodium video-settings option scrolling).
 *
 * Targeting {@link MouseHandler} rather than {@code Screen.mouseScrolled}
 * because the latter is inherited from {@code ContainerEventHandler} and
 * doesn't physically exist on {@code Screen.class} — Mixin can't resolve a
 * descriptor for an inherited default method.
 *
 * GuiGraphicsTooltipScrollMixin applies the offset during tooltip render.
 */
@Mixin(MouseHandler.class)
public abstract class ScreenTooltipScrollMixin {

	@Inject(method = "onScroll(JDD)V", at = @At("HEAD"), cancellable = true)
	private void evs$tooltipScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen == null) return;
		if (!Screen.hasShiftDown()) return;
		// Match vanilla's macOS horizontal→vertical fallback so trackpads work.
		double amount = (Minecraft.ON_OSX && yOffset == 0) ? xOffset : yOffset;
		if (amount == 0) return;
		TooltipScrollState.addScroll(amount);
		ci.cancel();
	}
}
