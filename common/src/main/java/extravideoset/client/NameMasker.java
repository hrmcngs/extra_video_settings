package extravideoset.client;

import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import extravideoset.NameMaskMode;

/**
 * Builds masked replacements for player names according to the current
 * {@link NameMaskMode}. Used by the per-loader hooks for nameplate, chat,
 * and tab list.
 */
public final class NameMasker {

	/** Unicode full-block; used for the BLACKOUT mode replacement glyph. */
	private static final char BLOCK = '█';

	private NameMasker() {}

	/** Skip the local player so the user can still see their own name. */
	public static boolean shouldMaskName(String name) {
		if (NameMaskConfig.getMode() == NameMaskMode.OFF) return false;
		if (name == null || name.isEmpty()) return false;
		String self = localPlayerName();
		return self == null || !name.equals(self);
	}

	/** Replace `name` using the current mode. Pass-through if mode is OFF. */
	public static Component maskName(String name) {
		NameMaskMode mode = NameMaskConfig.getMode();
		if (mode == NameMaskMode.OFF) return Component.literal(name);
		return buildMasked(name, mode);
	}

	/** Replace any occurrence of a connected player's name inside `text`. */
	public static Component maskTextContainingPlayerNames(Component text) {
		NameMaskMode mode = NameMaskConfig.getMode();
		if (mode == NameMaskMode.OFF) return text;

		Set<String> names = otherPlayerNames();
		if (names.isEmpty()) return text;

		String raw = text.getString();
		String replaced = raw;
		for (String name : names) {
			if (name == null || name.isEmpty()) continue;
			// Word-boundary-ish replace: avoid matching inside other words.
			Pattern p = Pattern.compile("\\b" + Pattern.quote(name) + "\\b");
			Component masked = buildMasked(name, mode);
			// For chat we keep the surrounding text intact and produce a plain
			// String. This loses style for the name itself; that's acceptable
			// for a privacy filter.
			replaced = p.matcher(replaced).replaceAll(plainOf(masked));
		}
		return replaced.equals(raw) ? text : Component.literal(replaced);
	}

	private static Component buildMasked(String name, NameMaskMode mode) {
		if (mode == NameMaskMode.BLACKOUT) {
			// Same character count as the original so layout doesn't shift.
			StringBuilder b = new StringBuilder(name.length());
			for (int i = 0; i < name.length(); i++) b.append(BLOCK);
			return Component.literal(b.toString());
		}
		// OBFUSCATED: keep the original text, apply §k formatting so the
		// vanilla font renderer glitches it.
		MutableComponent c = Component.literal(name);
		return c.withStyle(ChatFormatting.OBFUSCATED);
	}

	private static String plainOf(Component c) {
		// Re-render: for BLACKOUT this is the block string; for OBFUSCATED we
		// re-wrap with §k inline because Component is being flattened.
		String s = c.getString();
		if (NameMaskConfig.getMode() == NameMaskMode.OBFUSCATED) {
			return ChatFormatting.OBFUSCATED + s + ChatFormatting.RESET;
		}
		return s;
	}

	private static String localPlayerName() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return null;
		return mc.player.getGameProfile().getName();
	}

	private static Set<String> otherPlayerNames() {
		Minecraft mc = Minecraft.getInstance();
		ClientPacketListener conn = mc.getConnection();
		if (conn == null) return Set.of();
		String self = localPlayerName();
		java.util.HashSet<String> out = new java.util.HashSet<>();
		for (PlayerInfo info : conn.getOnlinePlayers()) {
			String n = info.getProfile().getName();
			if (n == null || n.isEmpty()) continue;
			if (self != null && self.equals(n)) continue;
			out.add(n);
		}
		return out;
	}

	/** Helper for tab-list mixin: build masked Component from a UUID's PlayerInfo. */
	public static Component maskFor(UUID uuid, String fallbackName) {
		Minecraft mc = Minecraft.getInstance();
		ClientPacketListener conn = mc.getConnection();
		String name = fallbackName;
		if (conn != null) {
			PlayerInfo info = conn.getPlayerInfo(uuid);
			if (info != null) name = info.getProfile().getName();
		}
		if (name == null) name = "";
		if (!shouldMaskName(name)) return Component.literal(name);
		return buildMasked(name, NameMaskConfig.getMode());
	}
}
