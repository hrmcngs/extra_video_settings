package extravideoset.client;

import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import extravideoset.ExtraVideoSettingsMod;

public class VanillaVideoOptionsScreen extends Screen {
	private final Screen parent;
	private final Options options;
	private OptionsList list;

	public VanillaVideoOptionsScreen(Screen parent, Options options) {
		super(Component.translatable("extra_video_settings.screen.title"));
		this.parent = parent;
		this.options = options;
	}

	@Override
	protected void init() {
		ExtraVideoSettingsMod.debugLog("VanillaVideoOptionsScreen.init() - width={}, height={}", this.width, this.height);
		this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);

		// Vanilla video/accessibility options that Embeddium removes
		this.list.addSmall(
				this.options.fovEffectScale(),
				this.options.screenEffectScale()
		);
		this.list.addSmall(
				this.options.darknessEffectScale(),
				this.options.damageTiltStrength()
		);
		this.list.addSmall(
				this.options.glintSpeed(),
				this.options.glintStrength()
		);
		this.list.addBig(
				this.options.entityShadows()
		);

		this.addWidget(this.list);
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
				btn -> {
					this.options.save();
					this.minecraft.setScreen(this.parent);
				}
		).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics);
		this.list.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 5, 0xFFFFFF);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	@Override
	public void onClose() {
		ExtraVideoSettingsMod.debugLog("VanillaVideoOptionsScreen closed, saving options");
		this.options.save();
		this.minecraft.setScreen(this.parent);
	}
}
