package application;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.controllers.CaptureWindowController;
import application.controllers.MainWindowController;
import application.controllers.SettingsWindowController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
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
	
	Thread							daemon;
	
	/**
	 * 
	 */
	public static Stage				stage;
	/**
	 * 
	 */
	public MainWindowController		mainWindowController;
	/**
	 * The Capture Window of the application
	 */
	public CaptureWindowController	captureWindowController;
	/**
	 * 
	 */
	public SettingsWindowController	settingsWindowController;
	
	/**
	 * Test to Speech using MaryTTS Libraries
	 */
	public static TextToSpeech		textToSpeech	= new TextToSpeech();
	
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
		stage.setTitle("XR3Capture V.7");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setAlwaysOnTop(true);
		
		// MainWindowController
		FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/MainWindowController.fxml"));
		loader1.load();
		mainWindowController = loader1.getController();
		
		// CaptureWindowController
		FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/CaptureWindowController.fxml"));
		loader2.load();
		captureWindowController = loader2.getController();
		
		// SettingsController
		FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/fxml/SettingsWindowController.fxml"));
		loader3.load();
		settingsWindowController = loader3.getController();
		
		// Add References between controllers
		mainWindowController.addControllerReferences(captureWindowController, settingsWindowController);
		captureWindowController.addControllerReferences(mainWindowController, settingsWindowController);
		settingsWindowController.addControllerReferences(mainWindowController, captureWindowController);
		
		// Finally
		stage.setScene(new Scene(loader1.getRoot(), Color.TRANSPARENT));
		stage.show();
		
		startPositionFixThread();
		
		// Check MaryTTS
		// textToSpeech.speak("Hello my name is Mary!")
	}
	
	/**
	 * This method is starting a Thread which is running all the time and is
	 * fixing the position of the application on the screen
	 */
	private void startPositionFixThread() {
		
		// Check frequently for the Primary Screen Bounds
		daemon = new Thread(() -> {
			// Run it until the application has been closed
			while (true) {
				
				// CountDownLatch
				CountDownLatch count = new CountDownLatch(1);
				
				// Get VisualBounds of the Primary Screen
				Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
				Platform.runLater(() -> {
					
					if (mainWindowController.getRoot().getNodeOrientation() == NodeOrientation.LEFT_TO_RIGHT)
						stage.setX(bounds.getMaxX() - stage.getWidth());
					else
						stage.setX(0);
					
					stage.setY(bounds.getMaxY() / 2 - stage.getHeight() / 2);
					count.countDown();
				});
				
				try {
					// Wait until the Platform.runLater has run
					count.await();
					// Sleep some time
					Thread.sleep(500);
				} catch (InterruptedException ex) {
					daemon.interrupt();
					Logger.getLogger(Main.class.getName()).log(Level.WARNING, null, ex);
				}
			}
		});
		
		daemon.setDaemon(true);
		daemon.start();
	}
	
}
