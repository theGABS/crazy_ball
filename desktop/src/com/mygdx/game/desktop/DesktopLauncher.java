package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.IActivityRequestHandler;
import com.mygdx.game.MyGame;

public class DesktopLauncher implements IActivityRequestHandler {
    private static DesktopLauncher application;
	public static void main (String[] arg) {
        if (application == null) {
            application = new DesktopLauncher();
        }
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new MyGame(application), config);
	}

    @Override
    public void showAds(boolean show) {
        // TODO Auto-generated method stub

    }
}
