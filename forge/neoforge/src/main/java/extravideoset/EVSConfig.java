package extravideoset;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EVSConfig {
	public static final ModConfigSpec CLIENT_SPEC;
	public static final ModConfigSpec.BooleanValue DEBUG_LOGGING;

	static {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		DEBUG_LOGGING = builder
				.comment("Enable debug logging to the console")
				.define("debugLogging", false);
		CLIENT_SPEC = builder.build();
	}

	public static boolean isDebugEnabled() {
		return CLIENT_SPEC.isLoaded() && DEBUG_LOGGING.get();
	}
}
