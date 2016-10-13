package application;

import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
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
		stage.setAlwaysOnTop(true);
		
		// Scene
		stage.setScene(new Scene(mainScene, 272, 144, Color.TRANSPARENT));
		stage.show();
		
		// Check frequently for the Primary Screen Bounds
		Thread daemon = new Thread(() -> {
			// Run it until the application has been closed
			while (true) {
				
				// CountDownLatch
				CountDownLatch count = new CountDownLatch(1);
				
				// Get VisualBounds of the Primary Screen
				Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
				Platform.runLater(() -> {
					stage.setX(bounds.getMaxX() - stage.getWidth());
					stage.setY(bounds.getMaxY() / 2 - stage.getHeight() / 2);
					count.countDown();
				});
				
				try {
					// Wait until the Platform.runLater has run
					count.await();
					// Sleep some time
					Thread.sleep(500);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		});
		
		daemon.setDaemon(true);
		daemon.start();
		
	}
	
}
