package org.tilegames.hexicube.spacegame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopStarter {
	public static LwjglApplicationConfiguration config;
	
	public static void main(String[] args) {
		config = new LwjglApplicationConfiguration();
		config.title = "title here";
		config.width = 800;
		config.height = 600;
		config.useGL20 = false;
		
		new LwjglApplication(new Game(), config);
	}
}