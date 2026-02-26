package extravideoset.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import extravideoset.ExtraVideoSettingsMod;

@Mod.EventBusSubscriber(modid = "extra_video_settings", value = Dist.CLIENT)
public class ClientEvents {

	@SubscribeEvent
	public static void onScreenInit(ScreenEvent.Init.Post event) {
		Screen screen = event.getScreen();

		String className = screen.getClass().getName();
		ExtraVideoSettingsMod.debugLog("Screen init: {}", className);

		// Add button to Embeddium/Sodium options screen
		if (className.contains("SodiumOptionsGUI")) {
			ExtraVideoSettingsMod.debugLog("Detected Embeddium/Sodium screen, adding Vanilla Options button");
			event.addListener(Button.builder(
					Component.translatable("extra_video_settings.button"),
					btn -> {
						Minecraft mc = Minecraft.getInstance();
						mc.setScreen(new VanillaVideoOptionsScreen(screen, mc.options));
					}
			).bounds(screen.width / 2 + 106, screen.height - 30, 100, 20).build());
		}

		// Add button to main Options screen as well
		if (screen instanceof OptionsScreen) {
			ExtraVideoSettingsMod.debugLog("Detected vanilla Options screen, adding Vanilla Options button");
			event.addListener(Button.builder(
					Component.translatable("extra_video_settings.button"),
					btn -> {
						Minecraft mc = Minecraft.getInstance();
						mc.setScreen(new VanillaVideoOptionsScreen(screen, mc.options));
					}
			).bounds(screen.width / 2 + 5, screen.height / 6 + 144 - 6, 150, 20).build());
		}
	}
}
