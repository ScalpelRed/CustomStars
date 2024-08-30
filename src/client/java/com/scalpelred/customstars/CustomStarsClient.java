package com.scalpelred.customstars;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CustomStarsClient implements ClientModInitializer {

	private static File starModelsFolder;
	public static final CustomStarsClientConfig CONFIG = new CustomStarsClientConfig(CustomStars.LOGGER);

	private static KeyBinding reloadConfigKeyBinding;

	@Override
	public void onInitializeClient() {

		CONFIG.load();
		CONFIG.save();

		reloadConfigKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.customstars.reload", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M,
				"category.customstars"));

		starModelsFolder = Paths.get(System.getProperty("user.dir"), "starmodels").toFile();
		if (!starModelsFolder.exists()) {
			try {
				Files.createDirectories(starModelsFolder.toPath());
			}
			catch (IOException e) {
				CustomStars.LOGGER.error("Can't create starmodels directory: {}", e.getMessage());
			}
		}

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			CONFIG.load();
			while (reloadConfigKeyBinding.wasPressed()) {
				client.worldRenderer.reload();
			}
		});
	}

	public static File getStarModelsFolder() {
		return starModelsFolder;
	}
}