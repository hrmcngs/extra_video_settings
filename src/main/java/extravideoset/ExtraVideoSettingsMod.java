package extravideoset;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.fml.common.Mod;

@Mod("extra_video_settings")
public class ExtraVideoSettingsMod {
	public static final Logger LOGGER = LogManager.getLogger(ExtraVideoSettingsMod.class);
	public static final String MODID = "extra_video_settings";

	public ExtraVideoSettingsMod() {
		LOGGER.info("Extra Video Settings loaded");
	}
}
