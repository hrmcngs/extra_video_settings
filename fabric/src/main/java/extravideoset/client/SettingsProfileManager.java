package extravideoset.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import extravideoset.ExtraVideoSettingsMod;

public class SettingsProfileManager {

	private static Path getProfilesDir() {
		return Minecraft.getInstance().gameDirectory.toPath()
				.resolve("config").resolve("extra_video_settings").resolve("profiles");
	}

	// ========== Key Bindings ==========

	public static int saveKeyBindings(String name) throws IOException {
		Path dir = getProfilesDir();
		Files.createDirectories(dir);
		Path file = dir.resolve("keys_" + name + ".txt");

		KeyMapping[] keyMappings = Minecraft.getInstance().options.keyMappings;

		// Sort by name for readability (use a copy to avoid modifying the original)
		List<KeyMapping> sorted = new ArrayList<>(Arrays.asList(keyMappings));
		sorted.sort(Comparator.comparing(KeyMapping::getName));

		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file, StandardCharsets.UTF_8))) {
			writer.println("# Key Bindings Profile: " + name);
			writer.println("# Saved: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			writer.println("# Total: " + sorted.size() + " keys");
			writer.println();

			for (KeyMapping km : sorted) {
				writer.println(km.getName() + "=" + km.getKey().getName());
			}
		}

		ExtraVideoSettingsMod.debugLog("Saved {} key bindings to profile '{}'", sorted.size(), name);
		return sorted.size();
	}

	public static int loadKeyBindings(String name) throws IOException {
		Path file = getProfilesDir().resolve("keys_" + name + ".txt");
		if (!Files.exists(file)) {
			throw new FileNotFoundException(name);
		}

		// Parse profile file
		Map<String, String> bindings = new LinkedHashMap<>();
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) continue;
				int eq = line.indexOf('=');
				if (eq > 0) {
					bindings.put(line.substring(0, eq), line.substring(eq + 1));
				}
			}
		}

		// Apply key bindings
		int applied = 0;
		KeyMapping[] keyMappings = Minecraft.getInstance().options.keyMappings;
		for (KeyMapping km : keyMappings) {
			String value = bindings.get(km.getName());
			if (value != null) {
				try {
					km.setKey(InputConstants.getKey(value));
					applied++;
				} catch (Exception e) {
					ExtraVideoSettingsMod.LOGGER.warn("Failed to set key '{}' to '{}': {}", km.getName(), value, e.getMessage());
				}
			}
		}

		KeyMapping.resetMapping();
		Minecraft.getInstance().options.save();

		ExtraVideoSettingsMod.debugLog("Loaded {} key bindings from profile '{}'", applied, name);
		return applied;
	}

	// ========== Video/Options Settings ==========

	public static int saveVideoSettings(String name) throws IOException {
		Path dir = getProfilesDir();
		Files.createDirectories(dir);
		Path file = dir.resolve("video_" + name + ".txt");

		// Read options.txt, save everything except key bindings
		Path optionsFile = Minecraft.getInstance().gameDirectory.toPath().resolve("options.txt");

		// Ensure options.txt is up to date
		Minecraft.getInstance().options.save();

		List<String> settings = new ArrayList<>();
		if (Files.exists(optionsFile)) {
			try (BufferedReader reader = Files.newBufferedReader(optionsFile, StandardCharsets.UTF_8)) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (!line.startsWith("key_")) {
						settings.add(line);
					}
				}
			}
		}

		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file, StandardCharsets.UTF_8))) {
			writer.println("# Video/Options Settings Profile: " + name);
			writer.println("# Saved: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			writer.println("# Settings: " + settings.size() + " entries (from options.txt, excluding key bindings)");
			writer.println();

			for (String s : settings) {
				writer.println(s);
			}
		}

		ExtraVideoSettingsMod.debugLog("Saved {} video settings to profile '{}'", settings.size(), name);
		return settings.size();
	}

	public static int loadVideoSettings(String name) throws IOException {
		Path file = getProfilesDir().resolve("video_" + name + ".txt");
		if (!Files.exists(file)) {
			throw new FileNotFoundException(name);
		}

		// Read profile settings
		Map<String, String> profileSettings = new LinkedHashMap<>();
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) continue;
				int colon = line.indexOf(':');
				if (colon > 0) {
					profileSettings.put(line.substring(0, colon), line.substring(colon + 1));
				}
			}
		}

		// Read current options.txt to preserve key bindings
		Path optionsFile = Minecraft.getInstance().gameDirectory.toPath().resolve("options.txt");
		List<String> keyLines = new ArrayList<>();

		if (Files.exists(optionsFile)) {
			try (BufferedReader reader = Files.newBufferedReader(optionsFile, StandardCharsets.UTF_8)) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.startsWith("key_")) {
						keyLines.add(line);
					}
				}
			}
		}

		// Write merged options.txt: profile settings + current key bindings
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(optionsFile, StandardCharsets.UTF_8))) {
			for (Map.Entry<String, String> entry : profileSettings.entrySet()) {
				writer.println(entry.getKey() + ":" + entry.getValue());
			}
			for (String keyLine : keyLines) {
				writer.println(keyLine);
			}
		}

		// Reload options from file
		Minecraft.getInstance().options.load();

		ExtraVideoSettingsMod.debugLog("Loaded {} video settings from profile '{}'", profileSettings.size(), name);
		return profileSettings.size();
	}

	// ========== Profile Management ==========

	public static List<String> listProfiles(String type) {
		Path dir = getProfilesDir();
		if (!Files.exists(dir)) return Collections.emptyList();

		String prefix = type + "_";
		List<String> profiles = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, prefix + "*.txt")) {
			for (Path p : stream) {
				String fileName = p.getFileName().toString();
				profiles.add(fileName.substring(prefix.length(), fileName.length() - 4));
			}
		} catch (IOException e) {
			ExtraVideoSettingsMod.LOGGER.warn("Failed to list profiles: {}", e.getMessage());
		}

		Collections.sort(profiles);
		return profiles;
	}

	public static boolean deleteProfile(String type, String name) throws IOException {
		Path file = getProfilesDir().resolve(type + "_" + name + ".txt");
		return Files.deleteIfExists(file);
	}
}
