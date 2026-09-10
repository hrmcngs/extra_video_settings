package extravideoset.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/** Hold-to-zoom without changing the player's saved FOV setting. */
public final class Zoom {
    public static final KeyMapping KEY = new KeyMapping(
            "key.extra_video_settings.zoom", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C, "key.categories.extra_video_settings");
    private static final double FOV_SCALE = 0.25;

    private Zoom() {}

    public static double apply(double fov) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.level != null && client.screen == null
                && client.isWindowActive() && KEY.isDown()) {
            return fov * FOV_SCALE;
        }
        return fov;
    }
}
