package extravideoset;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod("extra_video_settings")
public class ExtraVideoSettingsMod {
	public static final Logger LOGGER = LogManager.getLogger(ExtraVideoSettingsMod.class);
	public static final String MODID = "extra_video_settings";

	public ExtraVideoSettingsMod() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, EVSConfig.CLIENT_SPEC);
		LOGGER.info("Extra Video Settings loaded");
	}

	public static void debugLog(String message, Object... args) {
		if (EVSConfig.isDebugEnabled()) {
			LOGGER.info("[DEBUG] " + message, args);
		}
	}
}
