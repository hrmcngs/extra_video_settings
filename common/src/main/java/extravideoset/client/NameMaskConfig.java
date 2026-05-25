package extravideoset.client;

import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import extravideoset.NameMaskMode;

/**
 * Static holder for the current name-mask modes. Two independent
 * {@link NameMaskMode} values:
 *   {@code othersMode} — how OTHER players' names are masked
 *   {@code selfMode}   — how YOUR OWN name is masked (independent)
 *
 * Persisted to a plain text file in config/extra_video_settings/name_mask.txt
 * (loader-agnostic; no ForgeConfigSpec / no Fabric config lib needed).
 *
 * File format:
 *   others=OBFUSCATED
 *   self=BLACKOUT
 *
 * Backward-compat (read-only):
 *   A bare line "OBFUSCATED" is treated as {@code others=OBFUSCATED}.
 *   {@code self=true}  → selfMode = othersMode (whatever it currently is)
 *   {@code self=false} → selfMode = OFF
 */
public final class NameMaskConfig {

	private static volatile NameMaskMode othersMode = NameMaskMode.OFF;
	private static volatile NameMaskMode selfMode = NameMaskMode.OFF;
	private static volatile boolean loaded = false;

	private NameMaskConfig() {}

	/** Mask mode applied to OTHER players. */
	public static NameMaskMode getMode() {
		if (!loaded) load();
		return othersMode;
	}

	public static void setMode(NameMaskMode mode) {
		if (mode == null) mode = NameMaskMode.OFF;
		if (othersMode == mode) return;
		othersMode = mode;
		save();
	}

	/** Mask mode applied to the LOCAL player. */
	public static NameMaskMode getSelfMode() {
		if (!loaded) load();
		return selfMode;
	}

	public static void setSelfMode(NameMaskMode mode) {
		if (mode == null) mode = NameMaskMode.OFF;
		if (selfMode == mode) return;
		selfMode = mode;
		save();
	}

	/** Legacy boolean API kept for callers that haven't migrated yet. */
	public static boolean getIncludeSelf() {
		return getSelfMode() != NameMaskMode.OFF;
	}

	/** Legacy setter: true → mirror others' mode, false → OFF. */
	public static void setIncludeSelf(boolean v) {
		setSelfMode(v ? getMode() : NameMaskMode.OFF);
	}

	public static synchronized void load() {
		loaded = true;
		Path file = filePath();
		if (file == null || !Files.exists(file)) return;
		try {
			boolean legacySelfTrue = false;
			boolean sawSelfKey = false;
			for (String raw : Files.readAllLines(file, StandardCharsets.UTF_8)) {
				String line = raw.trim();
				if (line.isEmpty() || line.startsWith("#")) continue;
				if (line.startsWith("others=")) {
					othersMode = NameMaskMode.fromString(line.substring(7));
				} else if (line.startsWith("self=")) {
					sawSelfKey = true;
					String v = line.substring(5).trim();
					if (v.equalsIgnoreCase("true")) {
						legacySelfTrue = true;
					} else if (v.equalsIgnoreCase("false")) {
						selfMode = NameMaskMode.OFF;
					} else {
						selfMode = NameMaskMode.fromString(v);
					}
				} else {
					// Bare line — legacy "OBFUSCATED" format. Treat as others.
					othersMode = NameMaskMode.fromString(line);
				}
			}
			if (legacySelfTrue) selfMode = othersMode;
			// If file has no self= key at all, leave selfMode at default OFF.
			if (!sawSelfKey) selfMode = NameMaskMode.OFF;
		} catch (IOException ignored) {
			// keep defaults
		}
	}

	private static synchronized void save() {
		Path file = filePath();
		if (file == null) return;
		try {
			Files.createDirectories(file.getParent());
			String content = "others=" + othersMode.name()
					+ System.lineSeparator()
					+ "self=" + selfMode.name()
					+ System.lineSeparator();
			Files.writeString(file, content, StandardCharsets.UTF_8);
		} catch (IOException ignored) {
			// non-fatal; in-memory state still applies for this session
		}
	}

	private static Path filePath() {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null) return null;
		return mc.gameDirectory.toPath()
				.resolve("config")
				.resolve("extra_video_settings")
				.resolve("name_mask.txt");
	}
}
