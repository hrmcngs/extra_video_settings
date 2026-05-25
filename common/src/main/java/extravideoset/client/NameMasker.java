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

	/**
	 * Whether the given name should be masked. Considers the per-name mode:
	 *   - local player → {@link NameMaskConfig#getSelfMode()}
	 *   - other players → {@link NameMaskConfig#getMode()}
	 * Returns false when the applicable mode is OFF.
	 */
	public static boolean shouldMaskName(String name) {
		if (name == null || name.isEmpty()) return false;
		return modeFor(name) != NameMaskMode.OFF;
	}

	/**
	 * Replace `name` using the mode that applies to it (self vs other).
	 * Pass-through if that mode is OFF.
	 */
	public static Component maskName(String name) {
		NameMaskMode mode = modeFor(name);
		if (mode == NameMaskMode.OFF) return Component.literal(name);
		return buildMasked(name, mode);
	}

	/** Pick the right mode (self vs others) for the given player name. */
	private static NameMaskMode modeFor(String name) {
		String self = localPlayerName();
		if (self != null && self.equals(name)) {
			return NameMaskConfig.getSelfMode();
		}
		return NameMaskConfig.getMode();
	}

	/**
	 * Replace any occurrence of a connected player's name inside `text`,
	 * using the per-name mode (self vs other).
	 */
	public static Component maskTextContainingPlayerNames(Component text) {
		NameMaskMode othersMode = NameMaskConfig.getMode();
		NameMaskMode selfMode = NameMaskConfig.getSelfMode();
		if (othersMode == NameMaskMode.OFF && selfMode == NameMaskMode.OFF) return text;

		Set<String> names = candidateNames();
		if (names.isEmpty()) return text;

		String raw = text.getString();
		String replaced = raw;
		for (String name : names) {
			if (name == null || name.isEmpty()) continue;
			NameMaskMode mode = modeFor(name);
			if (mode == NameMaskMode.OFF) continue;
			Pattern p = Pattern.compile("\\b" + Pattern.quote(name) + "\\b");
			Component masked = buildMasked(name, mode);
			replaced = p.matcher(replaced).replaceAll(plainOf(masked, mode));
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

	private static String plainOf(Component c, NameMaskMode mode) {
		// Re-render: for BLACKOUT this is the block string; for OBFUSCATED we
		// re-wrap with §k inline because Component is being flattened.
		String s = c.getString();
		if (mode == NameMaskMode.OBFUSCATED) {
			return ChatFormatting.OBFUSCATED + s + ChatFormatting.RESET;
		}
		return s;
	}

	private static String localPlayerName() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return null;
		return mc.player.getGameProfile().getName();
	}

	/**
	 * All names that may need masking — every connected player plus the
	 * local player (so death-screen text and singleplayer chat still get
	 * the self name covered even when conn.getOnlinePlayers() omits it).
	 */
	private static Set<String> candidateNames() {
		Minecraft mc = Minecraft.getInstance();
		ClientPacketListener conn = mc.getConnection();
		String self = localPlayerName();
		java.util.HashSet<String> out = new java.util.HashSet<>();
		if (conn != null) {
			for (PlayerInfo info : conn.getOnlinePlayers()) {
				String n = info.getProfile().getName();
				if (n != null && !n.isEmpty()) out.add(n);
			}
		}
		if (self != null && !self.isEmpty()) out.add(self);
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
		NameMaskMode mode = modeFor(name);
		if (mode == NameMaskMode.OFF) return Component.literal(name);
		return buildMasked(name, mode);
	}
}
