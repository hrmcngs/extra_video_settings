package extravideoset.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import extravideoset.client.NameMasker;

/**
 * Fabric nameplate hook. Forge/NeoForge have RenderNameTagEvent for this
 * purpose; Fabric has no equivalent event, so we rewrite the `component`
 * parameter via ModifyVariable before vanilla draws it.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererNameTagMixin {

	@ModifyVariable(method = "renderNameTag", at = @At("HEAD"), argsOnly = true)
	private Component evs$maskNameTag(Component original,
	                                  Entity entity,
	                                  Component componentArg,
	                                  PoseStack pose,
	                                  MultiBufferSource buffer,
	                                  int light) {
		if (!(entity instanceof Player)) return original;
		String name = original.getString();
		if (!NameMasker.shouldMaskName(name)) return original;
		return NameMasker.maskName(name);
	}
}
