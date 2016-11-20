/**
 * 
 */
package application.controllers;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author GOXR3PLUS
 *
 */
public class SettingsWindowController extends Stage {
	
	@FXML
	private BorderPane		root;
	
	@FXML
	private JFXToggleButton	marryttsToggle;
	
	@FXML
	private JFXToggleButton	orientation;
	
	@FXML
	private JFXSlider		precisionSlider;
	
	// --------------------
	MainWindowController	mainWindowController;
	CaptureWindowController	captureWindowController;
	
	/**
	 * Will be called as soon as FXML file is loaded
	 */
	@FXML
	public void initialize() {
		
		setTitle("Settings");
		getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
		setScene(new Scene(root));
		centerOnScreen();
		
		// orientation
		orientation.selectedProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue) { // selected
				mainWindowController.getRoot().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
				orientation.setText("Current : LEFT  -> TO  -> RIGHT");
			} else {
				mainWindowController.getRoot().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
				orientation.setText("Current : RIGHT  -> TO  -> LEFT");
			}
		});
		
	}
	
	/**
	 * Add the needed references from the other controllers
	 * 
	 * @param mainWindowController
	 * @param captureWindowController
	 * 
	 */
	@SuppressWarnings("hiding")
	public void addControllerReferences(MainWindowController mainWindowController ,
			CaptureWindowController captureWindowController) {
		
		this.mainWindowController = mainWindowController;
		this.captureWindowController = captureWindowController;
	}
	
	/**
	 * @return The toggle which is for enabling/disabling text to speech
	 *         recognition
	 */
	public JFXToggleButton getMarryTTSToggle() {
		return marryttsToggle;
	}
	
	/**
	 * @return The precisionSlider
	 * 
	 */
	public JFXSlider getPrecisionSlider() {
		return precisionSlider;
	}
	
}
