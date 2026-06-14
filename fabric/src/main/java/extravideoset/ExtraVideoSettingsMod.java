package extravideoset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;

import extravideoset.client.NameMaskConfig;
import extravideoset.client.NameMaskEvents;
import extravideoset.client.ProfileScreen;
import extravideoset.client.SettingsCommand;
import extravideoset.client.TooltipScrollEvents;

public class ExtraVideoSettingsMod implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ExtraVideoSettings");
	public static final String MODID = "extra_video_settings";

	@Override
	public void onInitializeClient() {
		SettingsCommand.init();
		ProfileScreen.registerScreenEvent();
		NameMaskEvents.init();
		NameMaskConfig.load();
		TooltipScrollEvents.init();
		LOGGER.info("Extra Video Settings loaded");
	}

	public static void debugLog(String message, Object... args) {
		LOGGER.debug(message, args);
	}
}
