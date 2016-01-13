package com.brokenshotgun.lifeinspace.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.brokenshotgun.lifeinspace.LifeInSpaceGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		RunTexturePacker();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 600;
        config.resizable = false;
		config.title = "Mars Base Simulator";

		new LwjglApplication(new LifeInSpaceGame(), config);
	}

	private static void RunTexturePacker() {
		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 512;
		settings.maxHeight = 512;
		TexturePacker.process(settings, "../../images/sprites", "../assets", "sprites");
	}
}
