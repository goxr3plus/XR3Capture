package application;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

import org.controlsfx.control.Notifications;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
	
	// Random
	Random					random			= new Random();
	
	// BorderPane
	BorderPane				borderPane		= new BorderPane();
	
	// Canvas
	Canvas					canvas			= new Canvas();
	GraphicsContext			gc				= canvas.getGraphicsContext2D();
	FileChooser				fileSaver		= new FileChooser();
	
	// Variables
	int						xPressed		= 0;
	int						yPressed		= 0;
	int						xNow			= 0;
	int						yNow			= 0;
	private int				UPPER_LEFT_X	= 0;
	private int				UPPER_LEFT_Y	= 0;
	private int				rectWidth;
	private int				rectHeight;
	
	private Color			background		= Color.rgb(0, 0, 0, 0.3);
	
	// Service
	final CaptureService	captureService	= new CaptureService();
	
	BooleanProperty			shiftPressed	= new SimpleBooleanProperty();
	BooleanProperty			upPressed		= new SimpleBooleanProperty();
	BooleanProperty			rightPressed	= new SimpleBooleanProperty();
	BooleanProperty			downPressed		= new SimpleBooleanProperty();
	BooleanProperty			leftPressed		= new SimpleBooleanProperty();
	BooleanBinding			anyPressed		= upPressed.or(downPressed).or(leftPressed).or(rightPressed);
	
	/**
	 * Constructor
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
		canvas.setCursor(Cursor.CROSSHAIR);
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
		
		// Event Listeners
		
		getScene().setOnMousePressed(m -> {
			xPressed = (int) m.getScreenX();
			yPressed = (int) m.getScreenY();
		});
		
		getScene().setOnMouseDragged(m -> {
			xNow = (int) m.getScreenX();
			yNow = (int) m.getScreenY();
			repaintCanvas();
		});
		
		addKeyHandlers();
		addKeyAnimationTimer();
		
		// graphics context 2D
		gc.setLineDashes(6);
		gc.setFont(Font.font("null", FontWeight.BOLD, 14));
	}
	
	/**
	 * The Animation Timer for the Keys Pressed
	 */
	private void addKeyAnimationTimer() {
		
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long timestamp) {
				
				// With special key pressed
				// (we want [LEFT] and [DOWN] side of the rectangle to be
				// movable)
				
				// No Special Key is Pressed
				// (we want [RIGHT] and [UP] side of the rectangle to be
				// movable)
				
				// ------------------------------
				if (rightPressed.get()) {
					if (shiftPressed.get()) { // Special Key?
						if (xNow > xPressed) { // Mouse gone Right?
							xPressed += 1;
						} else {
							xNow += 1;
						}
					} else {
						if (xNow > xPressed) { // Mouse gone Right?
							xNow += 1;
						} else {
							xPressed += 1;
						}
					}
				}
				
				if (leftPressed.get()) {
					if (shiftPressed.get()) { // Special Key?
						if (xNow > xPressed) { // Mouse gone Right?
							xPressed -= 1;
						} else {
							xNow -= 1;
						}
					} else {
						if (xNow > xPressed) { // Mouse gone Right?
							xNow -= 1;
						} else {
							xPressed -= 1;
						}
					}
				}
				
				if (upPressed.get()) {
					if (shiftPressed.get()) { // Special Key?
						if (yNow > yPressed) { // Mouse gone UP?
							yNow -= 1;
						} else {
							yPressed -= 1;
						}
					} else {
						if (yNow > yPressed) { // Mouse gone UP?
							yPressed -= 1;
						} else {
							yNow -= 1;
						}
					}
				}
				
				if (downPressed.get()) {
					if (shiftPressed.get()) { // Special Key?
						if (yNow > yPressed) { // Mouse gone UP?
							yNow += 1;
						} else {
							yPressed += 1;
						}
					} else {
						if (yNow > yPressed) { // Mouse gone UP?
							yPressed += 1;
						} else {
							yNow += 1;
						}
					}
				}
				
				repaintCanvas();
			}
		};
		
		anyPressed.addListener((obs , wasPressed , isNowPressed) ->
		
		{
			if (isNowPressed.booleanValue()) {
				timer.start();
			} else {
				timer.stop();
			}
		});
	}
	
	/**
	 * Adds the KeyHandlers to the Scene
	 */
	private void addKeyHandlers() {
		
		// -------------Read the below to understand the Code-------------------
		
		// the default prototype of the below code is
		// 1->when the user is pressing RIGHT ARROW -> The rectangle is
		// increasing from the right side
		// 2->when the user is pressing LEFT ARROW -> The rectangle is
		// decreasing from the right side
		// 3->when the user is pressing UP ARROW -> The rectangle is increasing
		// from the up side
		// 4->when the user is pressing DOWN ARROW -> The rectangle is
		// decreasing from the up side
		
		// mention that when the shit key is being down then the rectangle is
		// increasing or decreasing from the opposite sides
		
		// keyPressed
		getScene().setOnKeyPressed(key -> {
			if (key.isShiftDown())
				shiftPressed.set(true);
			
			if (key.getCode() == KeyCode.RIGHT)
				rightPressed.set(true);
			
			if (key.getCode() == KeyCode.LEFT)
				leftPressed.set(true);
			
			if (key.getCode() == KeyCode.UP)
				upPressed.set(true);
			
			if (key.getCode() == KeyCode.DOWN)
				downPressed.set(true);
		});
		
		// keyReleased
		getScene().setOnKeyReleased(key -> {
			
			if (key.getCode() == KeyCode.SHIFT)
				shiftPressed.set(false);
			
			if (key.getCode() == KeyCode.RIGHT) {
				if (key.isControlDown()) {
					xNow = (int) getWidth();
					repaintCanvas();
				}
				rightPressed.set(false);
			}
			
			if (key.getCode() == KeyCode.LEFT) {
				if (key.isControlDown()) {
					xPressed = 0;
					repaintCanvas();
				}
				leftPressed.set(false);
			}
			
			if (key.getCode() == KeyCode.UP) {
				if (key.isControlDown()) {
					yPressed = 0;
					repaintCanvas();
				}
				upPressed.set(false);
			}
			
			if (key.getCode() == KeyCode.DOWN) {
				if (key.isControlDown()) {
					yNow = (int) getHeight();
					repaintCanvas();
				}
				downPressed.set(false);
			}
			
			if (key.getCode() == KeyCode.A && key.isControlDown())
				selectWholeScreen();
			
			if (key.getCode() == KeyCode.ESCAPE) {
				deActivateAllKeys();
				Main.stage.show();
				close();
			} else if (key.getCode() == KeyCode.ENTER) {
				deActivateAllKeys();
				createImage();
			}
			
		});
	}
	
	/**
	 * Deactivates the keys contained into this method
	 */
	private void deActivateAllKeys() {
		shiftPressed.set(false);
		upPressed.set(false);
		rightPressed.set(false);
		downPressed.set(false);
		leftPressed.set(false);
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
					int[] rect = getRectangleBounds();
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
		
		// draw the actual rectangle
		gc.setStroke(Color.AQUA);
		gc.setFill(background);
		gc.setLineWidth(2);
		
		// smart calculation of where the mouse has been dragged
		rectWidth = ( xNow > xPressed ) ? xNow - xPressed // RIGHT
				: xPressed - xNow // LEFT
		;
		rectHeight = ( yNow > yPressed ) ? yNow - yPressed // DOWN
				: yPressed - yNow // UP
		;
		
		UPPER_LEFT_X = // -------->UPPER_LEFT_X
				( xNow > xPressed ) ? xPressed // RIGHT
						: xNow// LEFT
		;
		UPPER_LEFT_Y = // -------->UPPER_LEFT_Y
				( yNow > yPressed ) ? yPressed // DOWN
						: yNow // UP
		;
		
		gc.strokeRect(UPPER_LEFT_X, UPPER_LEFT_Y, rectWidth, rectHeight);
		gc.fillRect(UPPER_LEFT_X, UPPER_LEFT_Y, rectWidth, rectHeight);
		
		// Show the Size
		double middle = UPPER_LEFT_X + rectWidth / 2.00;
		gc.setLineWidth(2);
		gc.setStroke(Color.AQUA);
		gc.strokeRect(middle - 78, UPPER_LEFT_Y < 25 ? UPPER_LEFT_Y + 1 : UPPER_LEFT_Y - 24.00, 79, 25);
		gc.setFill(Color.rgb(0, 0, 00, 0.9));
		gc.fillRect(middle - 77, UPPER_LEFT_Y < 25 ? UPPER_LEFT_Y + 1 : UPPER_LEFT_Y - 23.00, 77, 23);
		gc.setFill(Color.WHITE);
		gc.fillText(rectWidth + "," + rectHeight, middle - 77 + 9,
				UPPER_LEFT_Y < 25 ? UPPER_LEFT_Y + 17.00 : UPPER_LEFT_Y - 6.00);
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
		Main.stage.close();
	}
	
	/**
	 * Return an array witch contains the (UPPER_LEFT) Point2D of the rectangle
	 * and the width and height of the rectangle
	 * 
	 * @return An array witch contains the (UPPER_LEFT) Point2D of the
	 *         rectangle
	 *         and the width and height of the rectangle
	 */
	public int[] getRectangleBounds() {
		
		return new int[]{ UPPER_LEFT_X , UPPER_LEFT_Y , rectWidth , rectHeight };
		
	}
	
	/**
	 * The work of the Service is to capture the Image based on the rectangle
	 * that user drawn of the Screen
	 * 
	 * @author GOXR3PLUS
	 *
	 */
	public class CaptureService extends Service<Boolean> {
		String			filePath;
		BufferedImage	image;
		
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
		 * 
		 * @param img
		 */
		public void startService(BufferedImage img) {
			if (!isRunning()) {
				
				this.image = img;
				
				// Show the SaveDialog
				fileSaver.setInitialFileName("ScreenShot" + random.nextInt(50000));
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
			// System.out.println("Is JavaFX Thread:" +
			// Platform.isFxApplicationThread() + ",Value:" + getValue())
			
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
				while (!file.exists()) {
					// System.out.println("File has been created:" +
					// file.exists())
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				// System.out.println("File has been created:" + file.exists())
				Platform.runLater(() -> Main.mainScene.progressBar.setVisible(false));
			}).start();
			
		}
		
		@Override
		protected Task<Boolean> createTask() {
			return new Task<Boolean>() {
				@Override
				protected Boolean call() throws Exception {
					
					boolean write = false;
					
					// Try to write the file to the disc
					try {
						write = ImageIO.write(image, fileSaver.getSelectedExtensionFilter().getDescription(),
								new File(filePath));
					} catch (IOException e) {
						e.printStackTrace();
						return write;
					}
					
					return write;
				}
				
			};
		}
		
	}
	
}
