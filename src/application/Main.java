package application;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import marytts.TextToSpeech;

/**
 * @author GOXR3PLUS
 *
 */
public class Main extends Application {
	
	/**
	 * 
	 */
	public static Stage						stage;
	/**
	 * 
	 */
	public static ApplicationController		applicationController;
	/**
	 * The Capture Window of the application
	 */
	public static CaptureWindowController	captureWindowController;
	/**
	 * 
	 */
	public static SettingsController		settingsController;
	
	/**
	 * 
	 */
	public static TextToSpeech				textToSpeech	= new TextToSpeech();
	
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
		stage.setTitle("XR3Capture V.6");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setAlwaysOnTop(true);
		
		// CaptureWindowController
		FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/CaptureWindowController.fxml"));
		loader1.load();
		captureWindowController = loader1.getController();
		
		// SettingsController
		FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/SettingsController.fxml"));
		loader2.load();
		settingsController = loader2.getController();
		
		// ApplicationController
		applicationController = new ApplicationController();
		
		// Finally
		stage.setScene(new Scene(applicationController, 294, 210, Color.TRANSPARENT));
		stage.show();
		
		startPositionFixThread();
		
		// Check MaryTTS
		//textToSpeech.speak("Hello my name is Mary!")
	}
	
	/**
	 * This method is starting a Thread which is running all the time and is
	 * fixing the position of the application on the screen
	 */
	private static void startPositionFixThread() {
		
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
					Logger.getLogger(Main.class.getName()).log(Level.WARNING, null, ex);
				}
			}
		});
		
		daemon.setDaemon(true);
		daemon.start();
	}
	
}
