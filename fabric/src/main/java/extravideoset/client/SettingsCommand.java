package extravideoset.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.io.FileNotFoundException;
import java.util.List;

import extravideoset.ExtraVideoSettingsMod;

public class SettingsCommand {

	public static void init() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			SuggestionProvider<FabricClientCommandSource> suggestKeyProfiles = (ctx, builder) ->
					SharedSuggestionProvider.suggest(SettingsProfileManager.listProfiles("keys"), builder);

			SuggestionProvider<FabricClientCommandSource> suggestVideoProfiles = (ctx, builder) ->
					SharedSuggestionProvider.suggest(SettingsProfileManager.listProfiles("video"), builder);

			dispatcher.register(ClientCommandManager.literal("evs")
					// /evs save keys <name>
					.then(ClientCommandManager.literal("save")
							.then(ClientCommandManager.literal("keys")
									.then(ClientCommandManager.argument("name", StringArgumentType.word())
											.executes(SettingsCommand::saveKeys)))
							.then(ClientCommandManager.literal("video")
									.then(ClientCommandManager.argument("name", StringArgumentType.word())
											.executes(SettingsCommand::saveVideo))))
					// /evs load keys <name>
					.then(ClientCommandManager.literal("load")
							.then(ClientCommandManager.literal("keys")
									.then(ClientCommandManager.argument("name", StringArgumentType.word())
											.suggests(suggestKeyProfiles)
											.executes(SettingsCommand::loadKeys)))
							.then(ClientCommandManager.literal("video")
									.then(ClientCommandManager.argument("name", StringArgumentType.word())
											.suggests(suggestVideoProfiles)
											.executes(SettingsCommand::loadVideo))))
					// /evs list [keys|video]
					.then(ClientCommandManager.literal("list")
							.then(ClientCommandManager.literal("keys")
									.executes(ctx -> listProfiles(ctx, "keys")))
							.then(ClientCommandManager.literal("video")
									.executes(ctx -> listProfiles(ctx, "video"))))
					// /evs delete keys|video <name>
					.then(ClientCommandManager.literal("delete")
							.then(ClientCommandManager.literal("keys")
									.then(ClientCommandManager.argument("name", StringArgumentType.word())
											.suggests(suggestKeyProfiles)
											.executes(ctx -> deleteProfile(ctx, "keys"))))
							.then(ClientCommandManager.literal("video")
									.then(ClientCommandManager.argument("name", StringArgumentType.word())
											.suggests(suggestVideoProfiles)
											.executes(ctx -> deleteProfile(ctx, "video")))))
			);
		});
		ExtraVideoSettingsMod.LOGGER.info("Settings commands registered");
	}

	private static int saveKeys(CommandContext<FabricClientCommandSource> ctx) {
		String name = StringArgumentType.getString(ctx, "name");
		try {
			int count = SettingsProfileManager.saveKeyBindings(name);
			ctx.getSource().sendFeedback(Component.translatable(
					"extra_video_settings.command.save_keys_success", count, name)
					.withStyle(ChatFormatting.GREEN));
			return count;
		} catch (Exception e) {
			ctx.getSource().sendError(Component.translatable(
					"extra_video_settings.command.error", e.getMessage())
					.withStyle(ChatFormatting.RED));
			ExtraVideoSettingsMod.LOGGER.error("Failed to save key bindings", e);
			return 0;
		}
	}

	private static int saveVideo(CommandContext<FabricClientCommandSource> ctx) {
		String name = StringArgumentType.getString(ctx, "name");
		try {
			int count = SettingsProfileManager.saveVideoSettings(name);
			ctx.getSource().sendFeedback(Component.translatable(
					"extra_video_settings.command.save_video_success", count, name)
					.withStyle(ChatFormatting.GREEN));
			return count;
		} catch (Exception e) {
			ctx.getSource().sendError(Component.translatable(
					"extra_video_settings.command.error", e.getMessage())
					.withStyle(ChatFormatting.RED));
			ExtraVideoSettingsMod.LOGGER.error("Failed to save video settings", e);
			return 0;
		}
	}

	private static int loadKeys(CommandContext<FabricClientCommandSource> ctx) {
		String name = StringArgumentType.getString(ctx, "name");
		try {
			int count = SettingsProfileManager.loadKeyBindings(name);
			ctx.getSource().sendFeedback(Component.translatable(
					"extra_video_settings.command.load_keys_success", count, name)
					.withStyle(ChatFormatting.GREEN));
			return count;
		} catch (FileNotFoundException e) {
			ctx.getSource().sendError(Component.translatable(
					"extra_video_settings.command.profile_not_found", name)
					.withStyle(ChatFormatting.RED));
			return 0;
		} catch (Exception e) {
			ctx.getSource().sendError(Component.translatable(
					"extra_video_settings.command.error", e.getMessage())
					.withStyle(ChatFormatting.RED));
			ExtraVideoSettingsMod.LOGGER.error("Failed to load key bindings", e);
			return 0;
		}
	}

	private static int loadVideo(CommandContext<FabricClientCommandSource> ctx) {
		String name = StringArgumentType.getString(ctx, "name");
		try {
			int count = SettingsProfileManager.loadVideoSettings(name);
			ctx.getSource().sendFeedback(Component.translatable(
					"extra_video_settings.command.load_video_success", count, name)
					.withStyle(ChatFormatting.GREEN));
			return count;
		} catch (FileNotFoundException e) {
			ctx.getSource().sendError(Component.translatable(
					"extra_video_settings.command.profile_not_found", name)
					.withStyle(ChatFormatting.RED));
			return 0;
		} catch (Exception e) {
			ctx.getSource().sendError(Component.translatable(
					"extra_video_settings.command.error", e.getMessage())
					.withStyle(ChatFormatting.RED));
			ExtraVideoSettingsMod.LOGGER.error("Failed to load video settings", e);
			return 0;
		}
	}

	private static int listProfiles(CommandContext<FabricClientCommandSource> ctx, String type) {
		List<String> profiles = SettingsProfileManager.listProfiles(type);
		if (profiles.isEmpty()) {
			ctx.getSource().sendFeedback(Component.translatable(
					"extra_video_settings.command.no_profiles", type)
					.withStyle(ChatFormatting.YELLOW));
		} else {
			String joined = String.join(", ", profiles);
			ctx.getSource().sendFeedback(Component.translatable(
					"extra_video_settings.command.list_profiles", type, joined)
					.withStyle(ChatFormatting.AQUA));
		}
		return profiles.size();
	}

	private static int deleteProfile(CommandContext<FabricClientCommandSource> ctx, String type) {
		String name = StringArgumentType.getString(ctx, "name");
		try {
			boolean deleted = SettingsProfileManager.deleteProfile(type, name);
			if (deleted) {
				ctx.getSource().sendFeedback(Component.translatable(
						"extra_video_settings.command.delete_success", type, name)
						.withStyle(ChatFormatting.GREEN));
				return 1;
			} else {
				ctx.getSource().sendError(Component.translatable(
						"extra_video_settings.command.profile_not_found", name)
						.withStyle(ChatFormatting.RED));
				return 0;
			}
		} catch (Exception e) {
			ctx.getSource().sendError(Component.translatable(
					"extra_video_settings.command.error", e.getMessage())
					.withStyle(ChatFormatting.RED));
			return 0;
		}
	}
}
