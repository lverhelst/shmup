package verberg.com.shmup.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import Editor.LevelEditor;
import verberg.com.shmup.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 620;
		config.height = 480;
		//new LwjglApplication(new Game(), config);
		new LwjglApplication(new LevelEditor(), config);
	}
}
