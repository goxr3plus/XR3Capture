package application;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.controlsfx.control.Notifications;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This is the Window which is used from the user to draw the rectangle
 * representing an area on the screen to be captured
 * 
 * @author GOXR3PLUS
 *
 */
public class CaptureWindow extends Stage {

	// BorderPane
	BorderPane borderPane = new BorderPane();

	// Canvas
	Canvas canvas = new Canvas();
	GraphicsContext gc = canvas.getGraphicsContext2D();
	FileChooser fileSaver = new FileChooser();

	// Variables
	private int xPressed = 0;
	private int yPressed = 0;
	private int xNow = 0;
	private int yNow = 0;
	private int rectWidth;
	private int rectHeight;
	private Color foreground = Color.rgb(255, 167, 0);
	private Color background = Color.rgb(0, 0, 0, 0.3);

	// Service
	private final CaptureService captureService = new CaptureService();

	/**
	 * Constructor
	 * 
	 * @param rectWidth
	 * @param rectHeight
	 */
	public CaptureWindow() {

		setX(0);
		setY(0);
		setWidth(Main.screenWidth);
		setHeight(Main.screenHeight);
		getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
		initStyle(StageStyle.TRANSPARENT);
		setAlwaysOnTop(true);

		// BorderPane
		borderPane.setStyle("-fx-background-color:rgb(0,0,0,0.01);");

		// Canvas
		canvas.setWidth(Main.screenWidth);
		canvas.setHeight(Main.screenHeight);
		canvas.setOnMousePressed(m -> {
			xPressed = (int) m.getScreenX();
			yPressed = (int) m.getScreenY();
		});
		canvas.setOnMouseDragged(m -> {
			xNow = (int) m.getScreenX();
			yNow = (int) m.getScreenY();
			repaintCanvas();
		});
		borderPane.setCenter(canvas);

		// FileSaver
		fileSaver.getExtensionFilters().add(new ExtensionFilter("png", ".png"));
		Arrays.stream(ImageIO.getReaderFormatNames()).filter(s -> s.matches("[a-z]*")).forEach(format -> {
			String description;
			// switch (format) {
			// case "png":
			// description = "Better alternative than GIF or JPG for high colour
			// lossless images, supports translucency";
			// break;
			// case "jpg":
			// description = "Great for photographic images";
			// break;
			// case "gif":
			// description = "Supports animation, and transparent pixels";
			// break;
			// default:
			description = "." + format;
			// }
			if (!format.equals("png"))
				fileSaver.getExtensionFilters().add(new ExtensionFilter(format, description));
			// System.out.println(format);
		});
		fileSaver.setTitle("Save Image");

		// Scene
		setScene(new Scene(borderPane, Color.TRANSPARENT));
		getScene().setCursor(Cursor.CROSSHAIR);
		getScene().setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE) {
				Main.stage.show();
				close();
			} else if (key.getCode() == KeyCode.ENTER)
				createImage();

		});

		// graphics context 2D
		gc.setLineDashes(6);
		gc.setFont(Font.font("null", FontWeight.BOLD, 14));
	}

	/**
	 * Creates and saves the image
	 */
	public void createImage() {
		gc.clearRect(0, 0, getWidth(), getHeight());

		// Wait for frame Render
		new AnimationTimer() {
			private int frameCount = 0;

			@Override
			public void handle(long timestamp) {
				frameCount++;
				if (frameCount >= 5) {
					stop();

					BufferedImage image;
					int[] rect = calculateRect();
					try {
						image = new Robot().createScreenCapture(new Rectangle(rect[0], rect[1], rect[2], rect[3]));
					} catch (AWTException e) {
						e.printStackTrace();
						return;
					}

					// Start the Service
					captureService.startService(image);
				}
			}
		}.start();

	}

	/** Repaints the canvas **/
	protected void repaintCanvas() {

		gc.clearRect(0, 0, getWidth(), getHeight());
		gc.setStroke(foreground);
		gc.setFill(background);
		gc.setLineWidth(3);

		if (xNow > xPressed && yNow > yPressed) { // Right and Down

			calculateWidthAndHeight(xNow - xPressed, yNow - yPressed);
			gc.strokeRect(xPressed, yPressed, rectWidth, rectHeight);
			gc.fillRect(xPressed, yPressed, rectWidth, rectHeight);

		} else if (xNow < xPressed && yNow < yPressed) { // Left and Up

			calculateWidthAndHeight(xPressed - xNow, yPressed - yNow);
			gc.strokeRect(xNow, yNow, rectWidth, rectHeight);
			gc.fillRect(xNow, yNow, rectWidth, rectHeight);

		} else if (xNow > xPressed && yNow < yPressed) { // Right and Up

			calculateWidthAndHeight(xNow - xPressed, yPressed - yNow);
			gc.strokeRect(xPressed, yNow, rectWidth, rectHeight);
			gc.fillRect(xPressed, yNow, rectWidth, rectHeight);

		} else if (xNow < xPressed && yNow > yPressed) { // Left and Down

			calculateWidthAndHeight(xPressed - xNow, yNow - yPressed);
			gc.strokeRect(xNow, yPressed, rectWidth, rectHeight);
			gc.fillRect(xNow, yPressed, rectWidth, rectHeight);
		}

		// Show the Size
		gc.setLineWidth(2);
		gc.setStroke(Color.ORANGE);
		gc.strokeRect(0, 0, 81, 27);
		gc.setFill(Color.WHITE);
		gc.fillRect(2, 2, 77, 23);
		gc.setFill(Color.BLACK);
		gc.fillText(rectWidth + "," + rectHeight, 8, 18);
		gc.setLineWidth(1);
	}

	private void calculateWidthAndHeight(int w, int h) {
		rectWidth = w;
		rectHeight = h;
	}

	/**
	 * Selects whole Screen
	 */
	private void selectWholeScreen() {
		xPressed = 0;
		yPressed = 0;
		xNow = (int) getWidth();
		yNow = (int) getHeight();
		repaintCanvas();
	}

	/**
	 * Prepares the Window for the User
	 */
	public void prepareForCapture() {
		show();
		repaintCanvas();
		if (Main.mainScene.wholeScreen.isSelected())
			selectWholeScreen();

		Main.stage.close();

	}

	/**
	 * Return an array witch contains the (UPPER_LEFT) Point2D of the rectangle
	 * and the width and height of the rectangle
	 * 
	 * @return
	 */
	public int[] calculateRect() {

		if (xNow > xPressed) { // Right
			if (yNow > yPressed) // and DOWN
				return new int[] { xPressed, yPressed, rectWidth, rectHeight };
			else if (yNow < yPressed) // and UP
				return new int[] { xPressed, yNow, rectWidth, rectHeight };
		} else if (xNow < xPressed) { // LEFT
			if (yNow > yPressed) // and DOWN
				return new int[] { xNow, yPressed, rectWidth, rectHeight };
			else if (yNow < yPressed) // and UP
				return new int[] { xNow, yNow, rectWidth, rectHeight };
		}

		return new int[] { xPressed, yPressed, xNow, yNow };
	}

	/**
	 * The work of the Service is to capture the Image based on the rectangle
	 * that user drawn of the Screen
	 * 
	 * @author GOXR3PLUS
	 *
	 */
	public class CaptureService extends Service<Boolean> {
		String filePath;
		BufferedImage image;

		/**
		 * Constructor
		 */
		public CaptureService() {

			setOnSucceeded(s -> done());

			setOnCancelled(c -> done());

			setOnFailed(f -> done());

		}

		/**
		 * Starts the Service
		 */
		public void startService(BufferedImage image) {
			if (!isRunning()) {

				this.image = image;

				// Show the SaveDialog
				File file = fileSaver.showSaveDialog(CaptureWindow.this);
				if (file != null) {
					filePath = file.getAbsolutePath();
					Main.mainScene.progressBar.setVisible(true);
					reset();
					start();
				} else
					repaintCanvas();
			}
		}

		/**
		 * Service has been done
		 */
		private void done() {
			System.out.println("Is JavaFX Thread:" + Platform.isFxApplicationThread() + ",Value:" + getValue());

			Main.stage.show();
			close();

			if (getValue()) // successful?
				Notifications.create().title("Successfull Capturing").text("Image is being saved at:\n" + filePath)
						.showInformation();
			else
				Notifications.create().title("Error").text("Failed to capture the Screen!").showError();

			// Check if the file has been created
			new Thread(() -> {
				File file = new File(filePath);
				while (!file.exists()){
					System.out.println("File has been created:"+file.exists());
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				System.out.println("File has been created:"+file.exists());
				Platform.runLater(() -> Main.mainScene.progressBar.setVisible(false));
			}).start();

		}

		@Override
		protected Task<Boolean> createTask() {
			return new Task<Boolean>() {
				@Override
				protected Boolean call() throws Exception {
					try {
						ImageIO.write(image, fileSaver.getSelectedExtensionFilter().getDescription(),
								new File(filePath));
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}

					return true;
				}

			};
		}

	}

}
