package com.mygdx.crazyball.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.crazyball.IActivityRequestHandler;
import com.mygdx.crazyball.MyGame;

public class DesktopLauncher implements IActivityRequestHandler {
    private static DesktopLauncher application;
	public static void main (String[] arg) {
        if (application == null) {
            application = new DesktopLauncher();
        }
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 800;
        config.width = 480;
        config.addIcon("../res/drawable-hdpi/ic_launcher.png", Files.FileType.Internal);
		new LwjglApplication(new MyGame(application), config);
	}

    @Override
    public void showAds(boolean show) {
        // TODO Auto-generated method stub

    }
}
