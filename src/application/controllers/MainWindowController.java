package application.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;

import application.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * The Scene of the primary window of the application
 * 
 * @author GOXR3PLUS
 *
 */
public class MainWindowController {
	
	@FXML
	private StackPane			root;
	
	@FXML
	private JFXButton			more;
	
	@FXML
	private JFXButton			minimize;
	
	@FXML
	private JFXButton			exitButton;
	
	@FXML
	private JFXSlider			timeSlider;
	
	@FXML
	private JFXButton			captureButton;
	
	@FXML
	private JFXButton			openImageViewer;
	
	@FXML
	private Region				region;
	
	@FXML
	private ProgressIndicator	progressBar;
	
	// --------------------------------------------
	
	// The thread which is opening imageViewer
	private Thread				imageViewerThread;
	
	// References from other controllers
	SettingsWindowController	settingsWindowController;
	CaptureWindowController		captureWindowController;
	
	/**
	 * Add the needed references from the other controllers
	 * 
	 * @param captureWindowController
	 * @param settingsWindowController
	 * 
	 */
	@SuppressWarnings("hiding")
	public void addControllerReferences(CaptureWindowController captureWindowController ,
			SettingsWindowController settingsWindowController) {
		
		this.captureWindowController = captureWindowController;
		this.settingsWindowController = settingsWindowController;
	}
	
	/**
	 * Will be called as soon as FXML file is loaded
	 */
	@FXML
	public void initialize() {
		
		// more
		more.setOnAction(a -> settingsWindowController.show());
		
		// minimize
		minimize.setOnAction(a -> Main.stage.setIconified(true));
		
		// exitButton
		exitButton.setOnAction(a -> Platform.exit());
		
		// captureButton
		captureButton.setOnAction(a -> captureWindowController.prepareForCapture());
		
		// region
		region.visibleProperty().bind(progressBar.visibleProperty());
		
		// openImageViewer
		openImageViewer.setOnAction(ac -> {
			// isAlive?
			if (imageViewerThread != null && imageViewerThread.isAlive()) {
				ac.consume();
				return;
			}
			
			// Open ImageViewer
			imageViewerThread = new Thread(() -> {
				Platform.runLater(Notifications.create().title("Processing").text("Opening XR3ImageViewer....")
						.hideAfter(Duration.millis(1000))::show);
				
				try {
					
					ProcessBuilder builder = new ProcessBuilder("java", "-jar", "XR3ImageViewer.jar");
					Process process = builder.start();
					process.waitFor();
					
					if (process.exitValue() != 0)
						Platform.runLater(Notifications.create().title("Error")
								.text("Can't open XR3ImageViewer!\nBuilder Directory:" + builder.directory())
								.hideAfter(Duration.millis(2000))::showError);
					
				} catch (IOException | InterruptedException ex) {
					Logger.getLogger(getClass().getName()).log(Level.INFO, null, ex);
				}
			});
			
			imageViewerThread.setDaemon(true);
			imageViewerThread.start();
			
		});
		
		// timeSlider
		timeSlider.setOnScroll(s -> timeSlider.setValue(timeSlider.getValue() + ( s.getDeltaY() > 0 ? 1 : -1 )));
	}
	
	/**
	 * @return The TimeSlider
	 */
	public Slider getTimeSlider() {
		return timeSlider;
	}
	
	/**
	 * @return The Root of the FXML
	 */
	public StackPane getRoot() {
		return root;
	}
}
