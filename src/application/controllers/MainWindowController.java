package application.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;

import application.Main;
import database.DataBase;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * The Scene of the primary window of the application.
 *
 * @author GOXR3PLUS
 */
public class MainWindowController {

    /** The root. */
    @FXML
    private StackPane root;

    /** The more. */
    @FXML
    private JFXButton more;

    /** The minimize. */
    @FXML
    private JFXButton minimize;

    /** The exit button. */
    @FXML
    private JFXButton exitButton;

    /** The time slider. */
    @FXML
    private JFXSlider timeSlider;

    /** The capture button. */
    @FXML
    private JFXButton captureButton;

    /** The open image viewer. */
    @FXML
    private JFXButton openImageViewer;

    /** The region. */
    @FXML
    private Region region;

    /** The progress bar. */
    @FXML
    private ProgressIndicator progressBar;

    // --------------------------------------------

    /** The image viewer thread. */
    // The thread which is opening imageViewer
    private Thread imageViewerThread;

    /** The settings window controller. */
    // References from other controllers
    SettingsWindowController settingsWindowController;

    /** The capture window controller. */
    CaptureWindowController captureWindowController;

    /**
     * Add the needed references from the other controllers.
     *
     * @param captureWindowController
     *            the capture window controller
     * @param settingsWindowController
     *            the settings window controller
     */
    @SuppressWarnings("hiding")
    public void addControllerReferences(CaptureWindowController captureWindowController,
	    SettingsWindowController settingsWindowController) {

	this.captureWindowController = captureWindowController;
	this.settingsWindowController = settingsWindowController;
    }

    /**
     * Will be called as soon as FXML file is loaded.
     */
    @FXML
    public void initialize() {

	// more
	more.setOnAction(a -> settingsWindowController.show());

	// minimize
	minimize.setOnAction(a -> Main.stage.setIconified(true));

	// exitButton
	exitButton.setOnAction(a -> {
	    DataBase.saveDataBaseSettings(settingsWindowController);
	    Platform.exit();
	});

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
	    String path = DataBase.getBasePathForClass(DataBase.class);

	    imageViewerThread = new Thread(() -> {
		Platform.runLater(Notifications.create().title("Processing")
			.text("Opening XR3ImageViewer....\n Current path is: " + path)
			.hideAfter(Duration.millis(2000))::show);

		try {

		    ProcessBuilder builder = new ProcessBuilder("java", "-jar", path + "XR3ImageViewer.jar");
		    Process process = builder.start();
		    process.waitFor();

		    if (process.exitValue() != 0)
			Platform.runLater(Notifications
				.create().title("Error").text("Can't open XR3ImageViewer!\nBuilder Directory:" + path
					+ "\nTrying to start:" + path + "XR3ImageViewer.jar")
				.hideAfter(Duration.millis(2000))::showError);

		} catch (IOException | InterruptedException ex) {
		    Logger.getLogger(getClass().getName()).log(Level.INFO, null, ex);
		}
	    });

	    imageViewerThread.setDaemon(true);
	    imageViewerThread.start();

	});

	// timeSlider
	timeSlider.setOnScroll(s -> timeSlider.setValue(timeSlider.getValue() + (s.getDeltaY() > 0 ? 1 : -1)));
    }

    /**
     * Gets the time slider.
     *
     * @return The TimeSlider
     */
    public Slider getTimeSlider() {
	return timeSlider;
    }

    /**
     * Gets the root.
     *
     * @return The Root of the FXML
     */
    public StackPane getRoot() {
	return root;
    }
}
