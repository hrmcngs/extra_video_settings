package extravideoset.client;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.binding.compat.VanillaBooleanOptionBinding;
import me.jellysquid.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;

import org.embeddedt.embeddium.api.OptionPageConstructionEvent;
import org.embeddedt.embeddium.client.gui.options.OptionIdentifier;

import extravideoset.ExtraVideoSettingsMod;

public class EmbeddiumIntegration {

	private static final MinecraftOptionsStorage VANILLA_STORAGE = new MinecraftOptionsStorage();

	private static final ControlValueFormatter PERCENTAGE_OR_OFF = value ->
			value == 0 ? Component.translatable("options.off") : Component.literal(value + "%");

	public static void init() {
		OptionPageConstructionEvent.BUS.addListener(EmbeddiumIntegration::onPageConstruction);
		ExtraVideoSettingsMod.LOGGER.info("Embeddium integration initialized");
	}

	private static void onPageConstruction(OptionPageConstructionEvent event) {
		OptionIdentifier<Void> pageId = event.getId();
		ExtraVideoSettingsMod.debugLog("Page construction: modId={}, path={}, name={}",
				pageId.getModId(), pageId.getPath(), event.getName().getString());

		if ("general".equals(pageId.getPath())) {
			addVanillaVideoSettings(event);
		}
	}

	private static void addVanillaVideoSettings(OptionPageConstructionEvent event) {
		Options options = Minecraft.getInstance().options;

		OptionGroup group = OptionGroup.createBuilder()
				.setId(OptionIdentifier.create("extra_video_settings", "vanilla_video"))
				.add(createPercentOption("fov_effect_scale",
						"options.fovEffectScale",
						"extra_video_settings.tooltip.fov_effect_scale",
						opts -> (int) Math.round(opts.fovEffectScale().get() * 100),
						(opts, val) -> opts.fovEffectScale().set(val / 100.0)))
				.add(createPercentOption("screen_effect_scale",
						"options.screenEffectScale",
						"extra_video_settings.tooltip.screen_effect_scale",
						opts -> (int) Math.round(opts.screenEffectScale().get() * 100),
						(opts, val) -> opts.screenEffectScale().set(val / 100.0)))
				.add(createPercentOption("darkness_effect_scale",
						"options.darknessEffectScale",
						"extra_video_settings.tooltip.darkness_effect_scale",
						opts -> (int) Math.round(opts.darknessEffectScale().get() * 100),
						(opts, val) -> opts.darknessEffectScale().set(val / 100.0)))
				.add(createPercentOption("damage_tilt_strength",
						"options.damageTiltStrength",
						"extra_video_settings.tooltip.damage_tilt_strength",
						opts -> (int) Math.round(opts.damageTiltStrength().get() * 100),
						(opts, val) -> opts.damageTiltStrength().set(val / 100.0)))
				.add(createPercentOption("glint_speed",
						"options.glintSpeed",
						"extra_video_settings.tooltip.glint_speed",
						opts -> (int) Math.round(opts.glintSpeed().get() * 100),
						(opts, val) -> opts.glintSpeed().set(val / 100.0)))
				.add(createPercentOption("glint_strength",
						"options.glintStrength",
						"extra_video_settings.tooltip.glint_strength",
						opts -> (int) Math.round(opts.glintStrength().get() * 100),
						(opts, val) -> opts.glintStrength().set(val / 100.0)))
				.add(OptionImpl.createBuilder(Boolean.class, VANILLA_STORAGE)
						.setId(OptionIdentifier.create("extra_video_settings", "entity_shadows", Boolean.class))
						.setName(Component.translatable("options.entityShadows"))
						.setTooltip(Component.translatable("extra_video_settings.tooltip.entity_shadows"))
						.setBinding(new VanillaBooleanOptionBinding(options.entityShadows()))
						.setControl(TickBoxControl::new)
						.setImpact(OptionImpact.LOW)
						.build())
				.build();

		event.addGroup(group);
		ExtraVideoSettingsMod.debugLog("Added vanilla video settings group to General page");
	}

	private static OptionImpl<Options, Integer> createPercentOption(
			String id, String nameKey, String tooltipKey,
			Function<Options, Integer> getter,
			BiConsumer<Options, Integer> setter) {
		return OptionImpl.createBuilder(Integer.class, VANILLA_STORAGE)
				.setId(OptionIdentifier.create("extra_video_settings", id, Integer.class))
				.setName(Component.translatable(nameKey))
				.setTooltip(Component.translatable(tooltipKey))
				.setBinding(setter, getter)
				.setControl(opt -> new SliderControl(opt, 0, 100, 1, PERCENTAGE_OR_OFF))
				.setImpact(OptionImpact.LOW)
				.build();
	}
}
