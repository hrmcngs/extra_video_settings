package extravideoset;

import net.minecraftforge.common.ForgeConfigSpec;

public class EVSConfig {
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final ForgeConfigSpec.BooleanValue DEBUG_LOGGING;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		DEBUG_LOGGING = builder
				.comment("Enable debug logging to the console")
				.define("debugLogging", false);
		CLIENT_SPEC = builder.build();
	}

	public static boolean isDebugEnabled() {
		return CLIENT_SPEC.isLoaded() && DEBUG_LOGGING.get();
	}
}
