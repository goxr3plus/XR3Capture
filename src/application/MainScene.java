package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * The Scene of the primary window of the application
 * 
 * @author GOXR3PLUS
 *
 */
public class MainScene extends StackPane implements Initializable {

	@FXML
	JFXCheckBox wholeScreen;

	@FXML
	private JFXButton help;

	@FXML
	private JFXButton fixPosition;

	@FXML
	private JFXButton exitButton;

	@FXML
	private JFXButton captureButton;

	@FXML
	private JFXButton openImageViewer;

	@FXML
	private Region region;

	@FXML
	ProgressIndicator progressBar;

	// Notification
	Notifications notification = Notifications.create().title("Information").text(
			"After you have pressed capture button:\nTo capture the screen press [ENTER]\nTo cancel the capture press [ESCAPE]");

	/**
	 * Constructor
	 */
	public MainScene() {

		// FXMLLOADER
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// captureButton
		captureButton.setOnAction(a -> Main.captureWindow.prepareForCapture());

		// exitButton
		exitButton.setOnAction(a -> Platform.exit());

		// fixPosition
		fixPosition.setOnAction(a -> Main.fixWindowPosition());

		// help
		help.setOnAction(a -> notification.hideAfter(Duration.millis(3000)).show());

		// region
		region.visibleProperty().bind(progressBar.visibleProperty());

		// openImageViewer
		openImageViewer.setOnAction(ac -> {
			Notifications.create().title("Processing").text("Opening XR3ImageViewer....").hideAfter(Duration.millis(2000)).show();
			try {
				Runtime.getRuntime().exec("java -jar XR3ImageViewer.jar");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}
}
