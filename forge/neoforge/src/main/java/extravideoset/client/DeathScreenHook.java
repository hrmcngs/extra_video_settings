package extravideoset.client;

import java.lang.reflect.Field;

import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;

import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import extravideoset.client.NameMasker;

/**
 * NeoForge fallback for DeathScreen name masking — runs alongside
 * {@code DeathScreenMixin}. Uses {@code ScreenEvent.Init.Post} + reflection
 * to rewrite {@code causeOfDeath} after the screen is constructed. No-op
 * if the mixin already masked the text.
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
		}
		CAUSE_OF_DEATH = f;
	}

	public static void register() {
		NeoForge.EVENT_BUS.addListener(DeathScreenHook::onInit);
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
		}
	}
}
