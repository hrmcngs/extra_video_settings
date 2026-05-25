package extravideoset;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import extravideoset.client.DeathScreenHook;
import extravideoset.client.NameMaskConfig;
import extravideoset.client.NameMaskEvents;
import extravideoset.client.ProfileScreen;
import extravideoset.client.SettingsCommand;
import extravideoset.client.VanillaVideoScreenHook;

@Mod("extra_video_settings")
public class ExtraVideoSettingsMod {
	public static final Logger LOGGER = LogManager.getLogger(ExtraVideoSettingsMod.class);
	public static final String MODID = "extra_video_settings";

	public ExtraVideoSettingsMod() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, EVSConfig.CLIENT_SPEC);

		// NeoForge 1.20.2: Embeddium integration is omitted because no matching
		// 1.20.2 Embeddium binary is wired up yet (EmbeddiumIntegration.java
		// is excluded from this sourceSet — see build.gradle).

		SettingsCommand.init();
		ProfileScreen.registerScreenEvent();
		NameMaskEvents.init();
		NameMaskConfig.load();
		VanillaVideoScreenHook.register();
		DeathScreenHook.register();

		LOGGER.info("Extra Video Settings loaded (NeoForge)");
	}

	public static void debugLog(String message, Object... args) {
		if (EVSConfig.isDebugEnabled()) {
			LOGGER.info("[DEBUG] " + message, args);
		}
	}
}
