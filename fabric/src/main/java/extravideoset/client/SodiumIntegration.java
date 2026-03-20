package extravideoset.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;

import extravideoset.ExtraVideoSettingsMod;

public class SodiumIntegration {

	private static final MinecraftOptionsStorage VANILLA_STORAGE = new MinecraftOptionsStorage();

	public static OptionPage createPage() {
		List<OptionGroup> groups = new ArrayList<>();

		groups.add(OptionGroup.createBuilder()
				.add(createPercentOption(
						"options.fovEffectScale",
						"extra_video_settings.tooltip.fov_effect_scale",
						opts -> (int) Math.round(opts.fovEffectScale().get() * 100),
						(opts, val) -> opts.fovEffectScale().set(val / 100.0)))
				.add(createPercentOption(
						"options.screenEffectScale",
						"extra_video_settings.tooltip.screen_effect_scale",
						opts -> (int) Math.round(opts.screenEffectScale().get() * 100),
						(opts, val) -> opts.screenEffectScale().set(val / 100.0)))
				.add(createPercentOption(
						"options.darknessEffectScale",
						"extra_video_settings.tooltip.darkness_effect_scale",
						opts -> (int) Math.round(opts.darknessEffectScale().get() * 100),
						(opts, val) -> opts.darknessEffectScale().set(val / 100.0)))
				.add(createPercentOption(
						"options.damageTiltStrength",
						"extra_video_settings.tooltip.damage_tilt_strength",
						opts -> (int) Math.round(opts.damageTiltStrength().get() * 100),
						(opts, val) -> opts.damageTiltStrength().set(val / 100.0)))
				.add(createPercentOption(
						"options.glintSpeed",
						"extra_video_settings.tooltip.glint_speed",
						opts -> (int) Math.round(opts.glintSpeed().get() * 100),
						(opts, val) -> opts.glintSpeed().set(val / 100.0)))
				.add(createPercentOption(
						"options.glintStrength",
						"extra_video_settings.tooltip.glint_strength",
						opts -> (int) Math.round(opts.glintStrength().get() * 100),
						(opts, val) -> opts.glintStrength().set(val / 100.0)))
				.add(OptionImpl.createBuilder(boolean.class, VANILLA_STORAGE)
						.setName(Component.translatable("options.entityShadows"))
						.setTooltip(Component.translatable("extra_video_settings.tooltip.entity_shadows"))
						.setBinding(
								(opts, val) -> opts.entityShadows().set(val),
								(opts) -> opts.entityShadows().get())
						.setControl(TickBoxControl::new)
						.setImpact(OptionImpact.LOW)
						.build())
				.build());

		ExtraVideoSettingsMod.LOGGER.info("Created Sodium extra settings page");
		return new OptionPage(
				Component.translatable("extra_video_settings.sodium_page"),
				ImmutableList.copyOf(groups));
	}

	private static OptionImpl<Options, Integer> createPercentOption(
			String nameKey, String tooltipKey,
			Function<Options, Integer> getter,
			BiConsumer<Options, Integer> setter) {
		return OptionImpl.createBuilder(int.class, VANILLA_STORAGE)
				.setName(Component.translatable(nameKey))
				.setTooltip(Component.translatable(tooltipKey))
				.setBinding(setter, getter)
				.setControl(opt -> new SliderControl(opt, 0, 100, 1, ControlValueFormatter.percentage()))
				.setImpact(OptionImpact.LOW)
				.build();
	}
}
