package extravideoset.client;

import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import extravideoset.NameMaskMode;

/**
 * Static holder for the current name-mask mode. Persists to a plain text file
 * in config/extra_video_settings/name_mask.txt — loader-agnostic (no
 * ForgeConfigSpec / no Fabric config lib needed).
 */
public final class NameMaskConfig {

	private static volatile NameMaskMode current = NameMaskMode.OFF;
	private static volatile boolean loaded = false;

	private NameMaskConfig() {}

	public static NameMaskMode getMode() {
		if (!loaded) load();
		return current;
	}

	public static void setMode(NameMaskMode mode) {
		if (mode == null) mode = NameMaskMode.OFF;
		if (current == mode) return;
		current = mode;
		save();
	}

	public static synchronized void load() {
		loaded = true;
		Path file = filePath();
		if (file == null || !Files.exists(file)) return;
		try {
			String s = Files.readString(file, StandardCharsets.UTF_8).trim();
			current = NameMaskMode.fromString(s);
		} catch (IOException ignored) {
			// keep default
		}
	}

	private static synchronized void save() {
		Path file = filePath();
		if (file == null) return;
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, current.name(), StandardCharsets.UTF_8);
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
