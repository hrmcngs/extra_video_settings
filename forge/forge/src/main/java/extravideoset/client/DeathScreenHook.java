package extravideoset.client;

import java.lang.reflect.Field;

import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import extravideoset.client.NameMasker;

/**
 * Forge fallback for DeathScreen name masking — runs alongside
 * {@code DeathScreenMixin}. The mixin can silently fail to apply on Forge
 * when no refmap is generated for the build; this event-based hook uses
 * {@code ScreenEvent.Init.Post} + reflection to rewrite the screen's
 * {@code causeOfDeath} field after the screen is constructed. If the mixin
 * already masked the text, this is a no-op (regex won't find any unmasked
 * name).
 */
public final class DeathScreenHook {

	private DeathScreenHook() {}

	private static final Field CAUSE_OF_DEATH;
	static {
		Field f = null;
		try {
			f = DeathScreen.class.getDeclaredField("causeOfDeath");
			f.setAccessible(true);
		} catch (NoSuchFieldException ignored) {
			// Field name changed — give up silently. The mixin path may
			// still apply.
		}
		CAUSE_OF_DEATH = f;
	}

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener(DeathScreenHook::onInit);
	}

	private static void onInit(ScreenEvent.Init.Post event) {
		if (CAUSE_OF_DEATH == null) return;
		if (!(event.getScreen() instanceof DeathScreen ds)) return;
		try {
			Component current = (Component) CAUSE_OF_DEATH.get(ds);
			if (current == null) return;
			Component masked = NameMasker.maskTextContainingPlayerNames(current);
			if (masked != current) {
				CAUSE_OF_DEATH.set(ds, masked);
			}
		} catch (IllegalAccessException ignored) {
			// JDK 17 with no --add-opens may refuse to set a final field.
			// Mixin path is the primary route anyway.
		}
	}
}
