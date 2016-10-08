package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author GOXR3PLUS
 *
 */
public class Main extends Application {
	
	public static final int		screenWidth		= (int) Screen.getPrimary().getBounds().getWidth();
	public static final int		screenHeight	= (int) Screen.getPrimary().getBounds().getHeight();
	
	public static MainScene		mainScene		= new MainScene();
	public static CaptureWindow	captureWindow	= new CaptureWindow();
	public static Stage			stage;
	
	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primary) throws Exception {
		
		// stage
		stage = primary;
		stage.setTitle("XR3Capture");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(new Scene(mainScene, Color.TRANSPARENT));
		stage.setOnCloseRequest(close -> close.consume());
		fixWindowPosition();
		stage.setAlwaysOnTop(true);
		stage.show();
	}
	
	/**
	 * This method is fixing the position of the window
	 */
	public static void fixWindowPosition() {
		stage.setX(screenWidth - mainScene.getPrefWidth());
		stage.setY(screenHeight / 2 - mainScene.getPrefHeight() / 2);
	}
	
}
