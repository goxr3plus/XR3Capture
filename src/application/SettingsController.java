/**
 * 
 */
package application;

import com.jfoenix.controls.JFXToggleButton;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author GOXR3PLUS
 *
 */
public class SettingsController extends Stage {
	
	@FXML
	private BorderPane		root;
	
	@FXML
	private JFXToggleButton	marryttsToggle;
	
	@FXML
	private JFXToggleButton	akbarTrollToggle;
	
	@FXML
	private void initialize() {
		
		setTitle("Settings");
		setScene(new Scene(root));
		centerOnScreen();
		
	}
	
	public JFXToggleButton getMarryTTSToggle() {
		return marryttsToggle;
	}
	
	public JFXToggleButton getAkbarTrollToggle() {
		return akbarTrollToggle;
	}
}
