package application.controllers;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.controlsfx.control.Notifications;

import application.Main;
import application.SFileChooser;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This is the Window which is used from the user to draw the rectangle
 * representing an area on the screen to be captured
 * 
 * @author GOXR3PLUS
 *
 */
public class CaptureWindowController extends Stage {
	
	@FXML
	private StackPane			stackPane;
	
	@FXML
	private Canvas				mainCanvas;
	
	@FXML
	private MediaView			mediaView;
	
	// Random
	Random						random			= new Random();
	
	// Canvas
	GraphicsContext				gc;
	
	// FileChooser
	SFileChooser				fileSaver		= new SFileChooser();
	
	// Variables
	int							xPressed		= 0;
	int							yPressed		= 0;
	int							xNow			= 0;
	int							yNow			= 0;
	private int					UPPER_LEFT_X	= 0;
	private int					UPPER_LEFT_Y	= 0;
	private int					rectWidth;
	private int					rectHeight;
	
	Color						background		= Color.rgb(0, 0, 0, 0.3);
	
	// Service
	final CaptureService		captureService	= new CaptureService();
	
	BooleanProperty				shiftPressed	= new SimpleBooleanProperty();
	BooleanProperty				upPressed		= new SimpleBooleanProperty();
	BooleanProperty				rightPressed	= new SimpleBooleanProperty();
	BooleanProperty				downPressed		= new SimpleBooleanProperty();
	BooleanProperty				leftPressed		= new SimpleBooleanProperty();
	BooleanBinding				anyPressed		= upPressed.or(downPressed).or(leftPressed).or(rightPressed);
	
	private int					screenWidth		= (int) Screen.getPrimary().getBounds().getWidth();
	private int					screenHeight	= (int) Screen.getPrimary().getBounds().getHeight();
	Thread						countingThread;
	
	// Media
	MediaPlayer					mediaPlayer;
	
	// References from other controllers
	MainWindowController		mainWindowController;
	SettingsWindowController	settingsWindowController;
	
	/**
	 * Constructor
	 */
	public CaptureWindowController() {
		
		setX(0);
		setY(0);
		getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
		initStyle(StageStyle.TRANSPARENT);
		setAlwaysOnTop(true);
		
	}
	
	/**
	 * Add the needed references from the other controllers
	 * 
	 * @param mainWindowController
	 * @param settingsWindowController
	 * 
	 */
	@SuppressWarnings("hiding")
	public void addControllerReferences(MainWindowController mainWindowController , SettingsWindowController settingsWindowController) {
		
		this.mainWindowController = mainWindowController;
		this.settingsWindowController = settingsWindowController;
	}
	
	/**
	 * Will be called as soon as FXML file is loaded
	 */
	@FXML
	public void initialize() {
		
		// System.out.println("CaptureWindow initialized")
		
		// Scene
		Scene scene = new Scene(stackPane, screenWidth, screenHeight, Color.TRANSPARENT);
		scene.setCursor(Cursor.NONE);
		setScene(scene);
		addKeyHandlers();
		
		// Canvas
		mainCanvas.setWidth(screenWidth);
		mainCanvas.setHeight(screenHeight);
		mainCanvas.setOnMousePressed(m -> {
			xPressed = (int) m.getScreenX();
			yPressed = (int) m.getScreenY();
		});
		
		mainCanvas.setOnMouseDragged(m -> {
			xNow = (int) m.getScreenX();
			yNow = (int) m.getScreenY();
			repaintCanvas();
		});
		
		// graphics context 2D
		gc = mainCanvas.getGraphicsContext2D();
		gc.setLineDashes(6);
		gc.setFont(Font.font("null", FontWeight.BOLD, 14));
		
		// // Media
		// Media media = null;
		// try {
		// media = new
		// Media(getClass().getResource("/video/video.mp4").toURI().toString());
		// } catch (URISyntaxException e) {
		// e.printStackTrace();
		// }
		// mediaPlayer = new MediaPlayer(media);
		//
		// // MediaView
		// mediaView.setMediaPlayer(mediaPlayer);
		// mediaView.setFitWidth(screenWidth);
		// mediaView.setFitHeight(screenHeight);
		// mediaView.setPreserveRatio(false);
		// mediaView.setVisible(false);
		//
		// mediaPlayer.setOnError(() -> System.out.println("Media error:" +
		// mediaPlayer.getError().toString()));
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
			
			if (key.getCode() == KeyCode.ESCAPE || key.getCode() == KeyCode.BACK_SPACE) {
				
				// Stop Counting Thread
				if (countingThread != null)
					countingThread.interrupt();
				
				// Stop MaryTTS
				Main.textToSpeech.stopSpeaking();
				
				// Deactivate all keys
				deActivateAllKeys();
				
				// show the appropriate windows
				Main.stage.show();
				close();
			} else if (key.getCode() == KeyCode.ENTER || key.getCode() == KeyCode.SPACE) {
				// Stop MaryTTS
				Main.textToSpeech.stopSpeaking();
				
				// Deactivate all keys
				deActivateAllKeys();
				
				// Capture Selected Area
				createImage();
			}
			
		});
		
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
	 * Deactivates the keys contained into this method
	 */
	private void deActivateAllKeys() {
		shiftPressed.set(false);
		upPressed.set(false);
		rightPressed.set(false);
		downPressed.set(false);
		leftPressed.set(false);
	}
	
	int i = 0;
	
	/**
	 * Creates and saves the image
	 */
	public void createImage() {
		// return if it is alive
		if ( ( countingThread != null && countingThread.isAlive() ) || captureService.isRunning())
			return;
		
		countingThread = new Thread(() -> {
			boolean interrupted;
			
			// CountDown
			if (!mainWindowController.getTimeSlider().isDisabled()) {
				for (i = (int) mainWindowController.getTimeSlider().getValue(); i > 0; i--) {
					
					// Lock until it has been refreshed from JavaFX
					// Application Thread
					CountDownLatch count = new CountDownLatch(1);
					
					// Repaint the Canvas
					Platform.runLater(() -> {
						gc.clearRect(0, 0, getWidth(), getHeight());
						gc.setFill(background);
						gc.fillRect(0, 0, getWidth(), getHeight());
						gc.setFill(Color.BLACK);
						gc.fillOval(getWidth() / 2 - 90, getHeight() / 2 - 165, 250, 250);
						gc.setFill(Color.WHITE);
						gc.setFont(Font.font("", FontWeight.BOLD, 120));
						gc.fillText(Integer.toString(i), getWidth() / 2, getHeight() / 2);
						
						// Unlock the Parent Thread
						count.countDown();
					});
					
					try {
						// Wait JavaFX Application Thread
						count.await();
						
						// MaryTTS
						if (settingsWindowController.getMarryTTSToggle().isSelected())
							Main.textToSpeech.speak(i);
						
						// Sleep 1 seconds after that
						Thread.sleep(980);
					} catch (InterruptedException ex) {
						interrupted = true;
						countingThread.interrupt();
						Logger.getLogger(getClass().getName()).log(Level.INFO, null, ex);
						break;
					}
				}
			}
			
			// !interrupted?
			if (!Thread.interrupted()) {
				// MaryTTS
				if (settingsWindowController.getMarryTTSToggle().isSelected())
					Main.textToSpeech.speak("Select where the image will be saved.");
				
				Platform.runLater(() -> {
					// Clear the canvas
					gc.clearRect(0, 0, getWidth(), getHeight());
					
					// Wait for frame Render
					new AnimationTimer() {
						private int frameCount = 0;
						
						@Override
						public void handle(long timestamp) {
							frameCount++;
							if (frameCount >= 5) {
								stop();
								
								// Capture the Image
								BufferedImage image;
								int[] rect = getRectangleBounds();
								try {
									image = new Robot()
											.createScreenCapture(new Rectangle(rect[0], rect[1], rect[2], rect[3]));
								} catch (AWTException ex) {
									Logger.getLogger(getClass().getName()).log(Level.INFO, null, ex);
									return;
								}
								
								// System.out.println("Starting Service")
								
								// Start the Service
								captureService.startService(image);
								
							}
						}
					}.start();
				});
			} // !interrupted?
		});
		
		countingThread.setDaemon(true);
		countingThread.start();
		
	}
	
	/** Repaints the canvas **/
	protected void repaintCanvas() {
		
		gc.clearRect(0, 0, getWidth(), getHeight());
		gc.setFont(Font.font("", FontWeight.BOLD, 14));
		
		// draw the actual rectangle
		gc.setStroke(Color.AQUA);
		gc.setFill(background);
		gc.setLineWidth(1);
		
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
		
		gc.strokeRect(UPPER_LEFT_X - 1.00, UPPER_LEFT_Y - 1.00, rectWidth + 2.00, rectHeight + 2.00);
		gc.fillRect(UPPER_LEFT_X, UPPER_LEFT_Y, rectWidth, rectHeight);
		
		// draw the circles
		
		// Show the Size
		double middle = UPPER_LEFT_X + rectWidth / 2.00;
		gc.setLineWidth(1);
		gc.setStroke(Color.AQUA);
		gc.strokeRect(middle - 78, UPPER_LEFT_Y < 25 ? UPPER_LEFT_Y + 2 : UPPER_LEFT_Y - 26.00, 79, 25);
		gc.setFill(Color.rgb(0, 0, 00, 0.9));
		gc.fillRect(middle - 77, UPPER_LEFT_Y < 25 ? UPPER_LEFT_Y + 2 : UPPER_LEFT_Y - 25.00, 77, 23);
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
		settingsWindowController.close();
		if (settingsWindowController.getMarryTTSToggle().isSelected())
			Main.textToSpeech.speak("Select an area of the screen dragging your mouse and then press Enter or Space");
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
				fileSaver.get().setInitialFileName("ScreenShot" + random.nextInt(50000));
				File file = fileSaver.get().showSaveDialog(CaptureWindowController.this);
				if (file != null) {
					filePath = file.getAbsolutePath();
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
			
			Main.stage.show();
			close();
			
			if (getValue()) // successful?
				Notifications.create().title("Successfull Capturing").text("Image is being saved at:\n" + filePath)
						.showInformation();
			else
				Notifications.create().title("Error").text("Failed to capture the Screen!").showError();
		}
		
		@Override
		protected Task<Boolean> createTask() {
			return new Task<Boolean>() {
				@Override
				protected Boolean call() throws Exception {
					
					boolean write = false;
					
					// Try to write the file to the disc
					try {
						write = ImageIO.write(image, fileSaver.get().getSelectedExtensionFilter().getDescription(),
								new File(filePath));
					} catch (IOException ex) {
						Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
						return write;
					}
					
					return write;
				}
				
			};
		}
		
	}
	
}
