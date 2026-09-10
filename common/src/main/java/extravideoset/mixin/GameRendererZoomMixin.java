package extravideoset.mixin;

import extravideoset.client.Zoom;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererZoomMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void evs$zoom(Camera camera, float partialTick, boolean useFovSetting,
            CallbackInfoReturnable<Double> cir) {
        // Leave the hand/item projection alone.
        if (useFovSetting) {
            cir.setReturnValue(Zoom.apply(cir.getReturnValueD()));
        }
    }
}
