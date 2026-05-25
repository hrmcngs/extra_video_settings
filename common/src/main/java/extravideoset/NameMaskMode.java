package extravideoset;

/**
 * Display mode for other players' names. Set via the in-game UI and read by
 * the per-loader nameplate / chat / tab-list hooks.
 *
 * Loader-agnostic enum lives in common/ so the masking logic can be shared.
 */
public enum NameMaskMode {
	OFF("extra_video_settings.name_mask.off"),
	BLACKOUT("extra_video_settings.name_mask.blackout"),
	OBFUSCATED("extra_video_settings.name_mask.obfuscated");

	private final String translationKey;

	NameMaskMode(String translationKey) {
		this.translationKey = translationKey;
	}

	public String getTranslationKey() {
		return translationKey;
	}

	public static NameMaskMode fromString(String s) {
		if (s == null) return OFF;
		try {
			return valueOf(s.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return OFF;
		}
	}
}
