package extravideoset.client;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import extravideoset.NameMaskMode;

/**
 * When Embeddium is NOT installed, append two name-mask options to the
 * vanilla VideoSettingsScreen's OptionsList — one for OTHER players' names,
 * one for the LOCAL player's own name. Both are independent cycle controls
 * (OFF / Blackout / Obfuscated) rendered as a single addSmall row.
 */
public final class VanillaVideoScreenHook {

	private VanillaVideoScreenHook() {}

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener(VanillaVideoScreenHook::onInit);
	}

	private static void onInit(ScreenEvent.Init.Post event) {
		Screen screen = event.getScreen();
		if (!(screen instanceof VideoSettingsScreen)) return;

		OptionsList list = null;
		for (var listener : event.getListenersList()) {
			if (listener instanceof OptionsList ol) {
				list = ol;
				break;
			}
		}
		if (list == null) return;

		list.addSmall(buildOthersOption(), buildSelfOption());
	}

	private static OptionInstance<NameMaskMode> buildOthersOption() {
		return new OptionInstance<>(
				"extra_video_settings.name_mask.others",
				OptionInstance.cachedConstantTooltip(
						Component.translatable("extra_video_settings.tooltip.name_mask.others")),
				(caption, value) -> Component.translatable(value.getTranslationKey()),
				new OptionInstance.Enum<>(
						List.of(NameMaskMode.values()),
						Codec.STRING.xmap(NameMaskMode::fromString, Enum::name)),
				NameMaskConfig.getMode(),
				value -> NameMaskConfig.setMode(value));
	}

	private static OptionInstance<NameMaskMode> buildSelfOption() {
		return new OptionInstance<>(
				"extra_video_settings.name_mask.self",
				OptionInstance.cachedConstantTooltip(
						Component.translatable("extra_video_settings.tooltip.name_mask.self")),
				(caption, value) -> Component.translatable(value.getTranslationKey()),
				new OptionInstance.Enum<>(
						List.of(NameMaskMode.values()),
						Codec.STRING.xmap(NameMaskMode::fromString, Enum::name)),
				NameMaskConfig.getSelfMode(),
				value -> NameMaskConfig.setSelfMode(value));
	}
}
