
package main.java.com.goxr3plus.xr3capture.application;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The Class Main.
 *
 * @author GOXR3PLUS
 */
public class Main extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		new CaptureWindow().stage.show();
	}
}
