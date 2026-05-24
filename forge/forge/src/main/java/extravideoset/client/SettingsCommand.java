package extravideoset.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.minecraftforge.client.event.RegisterClientCommandsEvent;

import java.io.FileNotFoundException;
import java.util.List;

import extravideoset.ExtraVideoSettingsMod;

public class SettingsCommand {

	public static void init() {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.addListener(SettingsCommand::onRegisterCommands);
		ExtraVideoSettingsMod.LOGGER.info("Settings commands registered");
	}

	private static void onRegisterCommands(RegisterClientCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

		SuggestionProvider<CommandSourceStack> suggestKeyProfiles = (ctx, builder) ->
				SharedSuggestionProvider.suggest(SettingsProfileManager.listProfiles("keys"), builder);

		SuggestionProvider<CommandSourceStack> suggestVideoProfiles = (ctx, builder) ->
				SharedSuggestionProvider.suggest(SettingsProfileManager.listProfiles("video"), builder);

		dispatcher.register(Commands.literal("evs")
				// /evs save keys <name>
				.then(Commands.literal("save")
						.then(Commands.literal("keys")
								.then(Commands.argument("name", StringArgumentType.word())
										.executes(SettingsCommand::saveKeys)))
						.then(Commands.literal("video")
								.then(Commands.argument("name", StringArgumentType.word())
										.executes(SettingsCommand::saveVideo))))
				// /evs load keys <name>
				.then(Commands.literal("load")
						.then(Commands.literal("keys")
								.then(Commands.argument("name", StringArgumentType.word())
										.suggests(suggestKeyProfiles)
										.executes(SettingsCommand::loadKeys)))
						.then(Commands.literal("video")
								.then(Commands.argument("name", StringArgumentType.word())
										.suggests(suggestVideoProfiles)
										.executes(SettingsCommand::loadVideo))))
				// /evs list [keys|video]
				.then(Commands.literal("list")
						.then(Commands.literal("keys")
								.executes(ctx -> listProfiles(ctx, "keys")))
						.then(Commands.literal("video")
								.executes(ctx -> listProfiles(ctx, "video"))))
				// /evs delete keys|video <name>
				.then(Commands.literal("delete")
						.then(Commands.literal("keys")
								.then(Commands.argument("name", StringArgumentType.word())
										.suggests(suggestKeyProfiles)
										.executes(ctx -> deleteProfile(ctx, "keys"))))
						.then(Commands.literal("video")
								.then(Commands.argument("name", StringArgumentType.word())
										.suggests(suggestVideoProfiles)
										.executes(ctx -> deleteProfile(ctx, "video")))))
		);
	}

	private static int saveKeys(CommandContext<CommandSourceStack> ctx) {
		String name = StringArgumentType.getString(ctx, "name");
		try {
			int count = SettingsProfileManager.saveKeyBindings(name);
			ctx.getSource().sendSuccess(() -> Component.translatable(
					"extra_video_settings.command.save_keys_success", count, name)
					.withStyle(ChatFormatting.GREEN), false);
			return count;
		} catch (Exception e) {
			ctx.getSource().sendFailure(Component.translatable(
					"extra_video_settings.command.error", e.getMessage())
					.withStyle(ChatFormatting.RED));
			ExtraVideoSettingsMod.LOGGER.error("Failed to save key bindings", e);
			return 0;
		}
	}

	private static int saveVideo(CommandContext<CommandSourceStack> ctx) {
		String name = StringArgumentType.getString(ctx, "name");
		try {
			int count = SettingsProfileManager.saveVideoSettings(name);
			ctx.getSource().sendSuccess(() -> Component.translatable(
					"extra_video_settings.command.save_video_success", count, name)
					.withStyle(ChatFormatting.GREEN), false);
			return count;
		} catch (Exception e) {
			ctx.getSource().sendFailure(Component.translatable(
					"extra_video_settings.command.error", e.getMessage())
					.withStyle(ChatFormatting.RED));
			ExtraVideoSettingsMod.LOGGER.error("Failed to save video settings", e);
			return 0;
		}
	}

	private static int loadKeys(CommandContext<CommandSourceStack> ctx) {
		String name = StringArgumentType.getString(ctx, "name");
		try {
			int count = SettingsProfileManager.loadKeyBindings(name);
			ctx.getSource().sendSuccess(() -> Component.translatable(
					"extra_video_settings.command.load_keys_success", count, name)
					.withStyle(ChatFormatting.GREEN), false);
			return count;
		} catch (FileNotFoundException e) {
			ctx.getSource().sendFailure(Component.translatable(
					"extra_video_settings.command.profile_not_found", name)
					.withStyle(ChatFormatting.RED));
			return 0;
		} catch (Exception e) {
			ctx.getSource().sendFailure(Component.translatable(
					"extra_video_settings.command.error", e.getMessage())
					.withStyle(ChatFormatting.RED));
			ExtraVideoSettingsMod.LOGGER.error("Failed to load key bindings", e);
			return 0;
		}
	}

	private static int loadVideo(CommandContext<CommandSourceStack> ctx) {
		String name = StringArgumentType.getString(ctx, "name");
		try {
			int count = SettingsProfileManager.loadVideoSettings(name);
			ctx.getSource().sendSuccess(() -> Component.translatable(
					"extra_video_settings.command.load_video_success", count, name)
					.withStyle(ChatFormatting.GREEN), false);
			return count;
		} catch (FileNotFoundException e) {
			ctx.getSource().sendFailure(Component.translatable(
					"extra_video_settings.command.profile_not_found", name)
					.withStyle(ChatFormatting.RED));
			return 0;
		} catch (Exception e) {
			ctx.getSource().sendFailure(Component.translatable(
					"extra_video_settings.command.error", e.getMessage())
					.withStyle(ChatFormatting.RED));
			ExtraVideoSettingsMod.LOGGER.error("Failed to load video settings", e);
			return 0;
		}
	}

	private static int listProfiles(CommandContext<CommandSourceStack> ctx, String type) {
		List<String> profiles = SettingsProfileManager.listProfiles(type);
		if (profiles.isEmpty()) {
			ctx.getSource().sendSuccess(() -> Component.translatable(
					"extra_video_settings.command.no_profiles", type)
					.withStyle(ChatFormatting.YELLOW), false);
		} else {
			String joined = String.join(", ", profiles);
			ctx.getSource().sendSuccess(() -> Component.translatable(
					"extra_video_settings.command.list_profiles", type, joined)
					.withStyle(ChatFormatting.AQUA), false);
		}
		return profiles.size();
	}

	private static int deleteProfile(CommandContext<CommandSourceStack> ctx, String type) {
		String name = StringArgumentType.getString(ctx, "name");
		try {
			boolean deleted = SettingsProfileManager.deleteProfile(type, name);
			if (deleted) {
				ctx.getSource().sendSuccess(() -> Component.translatable(
						"extra_video_settings.command.delete_success", type, name)
						.withStyle(ChatFormatting.GREEN), false);
				return 1;
			} else {
				ctx.getSource().sendFailure(Component.translatable(
						"extra_video_settings.command.profile_not_found", name)
						.withStyle(ChatFormatting.RED));
				return 0;
			}
		} catch (Exception e) {
			ctx.getSource().sendFailure(Component.translatable(
					"extra_video_settings.command.error", e.getMessage())
					.withStyle(ChatFormatting.RED));
			return 0;
		}
	}
}
