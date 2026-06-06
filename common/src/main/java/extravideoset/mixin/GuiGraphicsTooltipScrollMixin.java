package extravideoset.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;

import extravideoset.client.TooltipScrollState;

/**
 * Vertically translate every tooltip render by TooltipScrollState.getOffset().
 *
 * In MC 1.20.1 tooltip rendering lives on GuiGraphics (Screen only forwards).
 * We hook the private {@code renderTooltipInternal} so every entry point
 * (item, component-list, formatted-char-sequence) gets the same offset.
 *
 * Item-tooltip entry point also resets the offset when the hovered ItemStack
 * changes, so a new tooltip always starts unscrolled.
 */
@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsTooltipScrollMixin {

	@Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
			at = @At("HEAD"))
	private void evs$tooltipResetForItem(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
		TooltipScrollState.onTooltipFor(stack);
	}

	@Inject(method = "renderTooltipInternal", at = @At("HEAD"))
	private void evs$tooltipScrollPush(Font font, List<ClientTooltipComponent> components,
			int x, int y, ClientTooltipPositioner positioner, CallbackInfo ci) {
		GuiGraphics self = (GuiGraphics) (Object) this;
		self.pose().pushPose();
		self.pose().translate(0f, (float) TooltipScrollState.getOffset(), 0f);
	}

	@Inject(method = "renderTooltipInternal", at = @At("RETURN"))
	private void evs$tooltipScrollPop(Font font, List<ClientTooltipComponent> components,
			int x, int y, ClientTooltipPositioner positioner, CallbackInfo ci) {
		GuiGraphics self = (GuiGraphics) (Object) this;
		self.pose().popPose();
	}
}
