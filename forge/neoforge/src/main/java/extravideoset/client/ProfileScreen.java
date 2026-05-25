package extravideoset.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.io.FileNotFoundException;
import java.util.List;

import extravideoset.ExtraVideoSettingsMod;

public class ProfileScreen extends Screen {

	private final Screen parent;
	private EditBox nameField;
	private Component statusMessage = Component.literal("");

	public ProfileScreen(Screen parent) {
		super(Component.translatable("extra_video_settings.profiles.title"));
		this.parent = parent;
	}

	// ========== OptionsScreen button hook ==========

	public static void registerScreenEvent() {
		NeoForge.EVENT_BUS.addListener(ProfileScreen::onOptionsScreenInit);
		NeoForge.EVENT_BUS.addListener(ProfileScreen::onMouseScrolled);
	}

	/** Bounded mouse-wheel scrolling on OptionsScreen — never scrolls past
	 *  the topmost or bottommost widget. */
	private static void onMouseScrolled(ScreenEvent.MouseScrolled.Post event) {
		if (!(event.getScreen() instanceof OptionsScreen)) return;
		double delta = event.getScrollDeltaY();
		if (delta == 0) return;
		int desired = (int) (-delta * 12);

		var screen = event.getScreen();
		int minY = Integer.MAX_VALUE;
		int maxBottom = Integer.MIN_VALUE;
		for (var l : screen.children()) {
			if (l instanceof net.minecraft.client.gui.components.AbstractWidget aw) {
				minY = Math.min(minY, aw.getY());
				maxBottom = Math.max(maxBottom, aw.getY() + aw.getHeight());
			}
		}
		if (minY == Integer.MAX_VALUE) return;

		int top = 4, bottom = screen.height - 4;
		int minShift = Math.min(0, bottom - maxBottom);
		int maxShift = Math.max(0, top - minY);
		int shift = Math.max(minShift, Math.min(maxShift, desired));
		if (shift == 0) return;

		for (var l : screen.children()) {
			if (l instanceof net.minecraft.client.gui.components.AbstractWidget aw) {
				aw.setY(aw.getY() + shift);
			}
		}
	}

	private static void onOptionsScreenInit(ScreenEvent.Init.Post event) {
		if (!(event.getScreen() instanceof OptionsScreen)) return;

		Screen screen = event.getScreen();
		int cx = screen.width / 2;

		String doneText = CommonComponents.GUI_DONE.getString();
		Button doneButton = null;
		for (var listener : event.getListenersList()) {
			if (listener instanceof Button btn && btn.getMessage().getString().equals(doneText)) {
				doneButton = btn;
				break;
			}
		}

		if (doneButton == null) {
			event.addListener(Button.builder(
					Component.translatable("extra_video_settings.profiles"),
					b -> Minecraft.getInstance().setScreen(new ProfileScreen(screen)))
					.bounds(cx - 100, screen.height - 48, 200, 20)
					.build());
			return;
		}

		int gridBottom = 0;
		for (var listener : event.getListenersList()) {
			if (listener instanceof net.minecraft.client.gui.components.AbstractWidget aw
					&& aw != doneButton
					&& aw.getY() < doneButton.getY()) {
				gridBottom = Math.max(gridBottom, aw.getY() + aw.getHeight());
			}
		}

		// Always place Profile directly below the grid, push Done down
		// to follow. If Done is off-screen on a tiny window, the user can
		// scroll (see onMouseScrolled) or close the screen with ESC.
		final Button originalDone = doneButton;
		int profileY = gridBottom + 4;
		int newDoneY = profileY + 24;

		event.removeListener(originalDone);
		event.addListener(Button.builder(CommonComponents.GUI_DONE, b -> originalDone.onPress())
				.bounds(cx - 100, newDoneY, 200, 20)
				.build());
		event.addListener(Button.builder(
				Component.translatable("extra_video_settings.profiles"),
				b -> Minecraft.getInstance().setScreen(new ProfileScreen(screen)))
				.bounds(cx - 100, profileY, 200, 20)
				.build());
	}

	// ========== Screen implementation ==========

	@Override
	protected void init() {
		int cx = this.width / 2;

		// Profile name text field
		this.nameField = new EditBox(this.font, cx - 100, 40, 200, 20,
				Component.translatable("extra_video_settings.profiles.name"));
		this.nameField.setMaxLength(32);
		this.nameField.setHint(Component.translatable("extra_video_settings.profiles.name_hint"));
		this.addRenderableWidget(this.nameField);

		// Save buttons
		int y = 70;
		this.addRenderableWidget(Button.builder(
				Component.translatable("extra_video_settings.profiles.save_keys"),
				btn -> doAction("save", "keys"))
				.bounds(cx - 204, y, 200, 20).build());
		this.addRenderableWidget(Button.builder(
				Component.translatable("extra_video_settings.profiles.save_video"),
				btn -> doAction("save", "video"))
				.bounds(cx + 4, y, 200, 20).build());

		// Load buttons
		y += 24;
		this.addRenderableWidget(Button.builder(
				Component.translatable("extra_video_settings.profiles.load_keys"),
				btn -> doAction("load", "keys"))
				.bounds(cx - 204, y, 200, 20).build());
		this.addRenderableWidget(Button.builder(
				Component.translatable("extra_video_settings.profiles.load_video"),
				btn -> doAction("load", "video"))
				.bounds(cx + 4, y, 200, 20).build());

		// Delete buttons
		y += 24;
		this.addRenderableWidget(Button.builder(
				Component.translatable("extra_video_settings.profiles.delete_keys"),
				btn -> doAction("delete", "keys"))
				.bounds(cx - 204, y, 200, 20).build());
		this.addRenderableWidget(Button.builder(
				Component.translatable("extra_video_settings.profiles.delete_video"),
				btn -> doAction("delete", "video"))
				.bounds(cx + 4, y, 200, 20).build());

		// Done button
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
				btn -> this.minecraft.setScreen(this.parent))
				.bounds(cx - 100, this.height - 27, 200, 20).build());
	}

	private void doAction(String action, String type) {
		String name = this.nameField.getValue().trim();
		if (name.isEmpty()) {
			this.statusMessage = Component.translatable("extra_video_settings.profiles.name_empty")
					.withStyle(ChatFormatting.RED);
			return;
		}

		try {
			switch (action) {
				case "save": {
					int count = "keys".equals(type)
							? SettingsProfileManager.saveKeyBindings(name)
							: SettingsProfileManager.saveVideoSettings(name);
					String key = "keys".equals(type)
							? "extra_video_settings.command.save_keys_success"
							: "extra_video_settings.command.save_video_success";
					this.statusMessage = Component.translatable(key, count, name)
							.withStyle(ChatFormatting.GREEN);
					break;
				}
				case "load": {
					int count = "keys".equals(type)
							? SettingsProfileManager.loadKeyBindings(name)
							: SettingsProfileManager.loadVideoSettings(name);
					String key = "keys".equals(type)
							? "extra_video_settings.command.load_keys_success"
							: "extra_video_settings.command.load_video_success";
					this.statusMessage = Component.translatable(key, count, name)
							.withStyle(ChatFormatting.GREEN);
					break;
				}
				case "delete": {
					boolean deleted = SettingsProfileManager.deleteProfile(type, name);
					if (deleted) {
						this.statusMessage = Component.translatable(
								"extra_video_settings.command.delete_success", type, name)
								.withStyle(ChatFormatting.GREEN);
					} else {
						this.statusMessage = Component.translatable(
								"extra_video_settings.command.profile_not_found", name)
								.withStyle(ChatFormatting.RED);
					}
					break;
				}
			}
		} catch (FileNotFoundException e) {
			this.statusMessage = Component.translatable(
					"extra_video_settings.command.profile_not_found", name)
					.withStyle(ChatFormatting.RED);
		} catch (Exception e) {
			this.statusMessage = Component.translatable(
					"extra_video_settings.command.error", e.getMessage())
					.withStyle(ChatFormatting.RED);
			ExtraVideoSettingsMod.LOGGER.error("Profile action failed", e);
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		// MC 1.20.2+ added (mouseX, mouseY, partialTick) to renderBackground
		this.renderBackground(graphics, mouseX, mouseY, partialTick);

		// Title
		graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);

		// Profile name label
		graphics.drawString(this.font,
				Component.translatable("extra_video_settings.profiles.name"),
				this.width / 2 - 100, 30, 0xA0A0A0);

		// Status message
		if (!this.statusMessage.getString().isEmpty()) {
			graphics.drawCenteredString(this.font, this.statusMessage, this.width / 2, 145, 0xFFFFFF);
		}

		// Profile lists
		int y = 165;
		List<String> keyProfiles = SettingsProfileManager.listProfiles("keys");
		List<String> videoProfiles = SettingsProfileManager.listProfiles("video");

		graphics.drawString(this.font,
				Component.translatable("extra_video_settings.profiles.keys_label"),
				this.width / 2 - 200, y, 0x55FF55);
		y += 12;
		if (keyProfiles.isEmpty()) {
			graphics.drawString(this.font,
					Component.translatable("extra_video_settings.profiles.none"),
					this.width / 2 - 190, y, 0x808080);
		} else {
			graphics.drawString(this.font, String.join(", ", keyProfiles),
					this.width / 2 - 190, y, 0x55FF55);
		}

		y += 16;
		graphics.drawString(this.font,
				Component.translatable("extra_video_settings.profiles.video_label"),
				this.width / 2 - 200, y, 0x55FFFF);
		y += 12;
		if (videoProfiles.isEmpty()) {
			graphics.drawString(this.font,
					Component.translatable("extra_video_settings.profiles.none"),
					this.width / 2 - 190, y, 0x808080);
		} else {
			graphics.drawString(this.font, String.join(", ", videoProfiles),
					this.width / 2 - 190, y, 0x55FFFF);
		}

		super.render(graphics, mouseX, mouseY, partialTick);
	}
}
