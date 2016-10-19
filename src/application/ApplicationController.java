package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
public class ApplicationController extends StackPane implements Initializable {
	
	@FXML
	private JFXButton			more;
	
	@FXML
	private JFXButton			exitButton;
	
	@FXML
	private Slider				timeSlider;
	
	@FXML
	private JFXToggleButton		waitTimeButton;
	
	@FXML
	private JFXButton			captureButton;
	
	@FXML
	private JFXButton			openImageViewer;
	
	@FXML
	private Region				region;
	
	@FXML
	private ProgressIndicator	progressBar;
	
	private Thread				openImageViewerThread;
	
	/**
	 * Constructor
	 */
	public ApplicationController() {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ApplicationController.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
		}
		
	}
	
	@Override
	public void initialize(URL location , ResourceBundle resources) {
		
		// more
		more.setOnAction(a -> Main.settingsController.show());
		
		// exitButton
		exitButton.setOnAction(a -> Platform.exit());
		
		// captureButton
		captureButton.setOnAction(a -> Main.captureWindowController.prepareForCapture());
		
		// region
		region.visibleProperty().bind(progressBar.visibleProperty());
		
		// openImageViewer
		openImageViewer.setOnAction(ac -> {
			// isAlive?
			if (openImageViewerThread != null && openImageViewerThread.isAlive()) {
				ac.consume();
				return;
			}
			
			// Open ImageViewer
			openImageViewerThread = new Thread(() -> {
				Platform.runLater(Notifications.create().title("Processing").text("Opening XR3ImageViewer....")
						.hideAfter(Duration.millis(1000))::show);
				
				try {
					
					Process process = Runtime.getRuntime().exec("java -jar XR3ImageViewer.jar");
					process.waitFor();
					
					if (process.exitValue() != 0)
						Platform.runLater(Notifications.create().title("Error").text("Can't open XR3ImageViewer!")
								.hideAfter(Duration.millis(2000))::showError);
					
				} catch (IOException | InterruptedException ex) {
					Logger.getLogger(getClass().getName()).log(Level.INFO, null, ex);
				}
			});
			
			openImageViewerThread.setDaemon(true);
			openImageViewerThread.start();
			
		});
		
		// timeSlider
		timeSlider.disableProperty().bind(waitTimeButton.selectedProperty().not());
		timeSlider.valueProperty().addListener((observable , oldValue , newValue) -> waitTimeButton
				.setText("Wait Time:( " + (int) timeSlider.getValue() + " sec)"));
		
		timeSlider.setOnScroll(s -> timeSlider.setValue(timeSlider.getValue() + ( s.getDeltaY() > 0 ? 1 : -1 )));
	}
	
	/**
	 * @return The TimeSlider
	 */
	public Slider getTimeSlider() {
		return timeSlider;
	}
}
