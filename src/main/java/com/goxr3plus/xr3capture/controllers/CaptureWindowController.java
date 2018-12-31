/*
 * 
 */
package main.java.com.goxr3plus.xr3capture.controllers;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3capture.models.CaptureWindowModel;
import main.java.com.goxr3plus.xr3capture.utils.ActionTool;
import main.java.com.goxr3plus.xr3capture.utils.InfoTool;
import main.java.com.goxr3plus.xr3capture.utils.NotificationType;
import main.java.com.goxr3plus.xr3capture.utils.SFileChooser;

/**
 * This is the Window which is used from the user to draw the rectangle representing an area on the screen to be captured.
 *
 * @author GOXR3PLUS
 */
public class CaptureWindowController extends StackPane {
	
	private Stage stage;
	
	private final Stage captureWindowStage;
	
	/** The stack pane. */
	@FXML
	private StackPane stackPane;
	
	/** The main canvas. */
	@FXML
	private Canvas mainCanvas;
	
	// -----------------------------
	
	/**
	 * The Model of the CaptureWindow
	 */
	CaptureWindowModel data = new CaptureWindowModel();
	
	/** The file saver. */
	SFileChooser fileSaver = new SFileChooser();
	
	/** The capture service. */
	final CaptureService captureService = new CaptureService();
	
	/** The graphics context of the canvas */
	GraphicsContext gc;
	
	/**
	 * When a key is being pressed into the capture window then this Animation Timer is doing it's magic.
	 */
	AnimationTimer yPressedAnimation = new AnimationTimer() {
		
		private long nextSecond = 0L;
		// private static final long ONE_SECOND_NANOS = 1_000_000_000L
		private long precisionLevel;
		
		@Override
		public void start() {
			nextSecond = 0L;
			precisionLevel = (long) ( settingsWindowController.getPrecisionSlider().getValue() * 1_000_000L );
			super.start();
		}
		
		@Override
		public void handle(final long nanos) {
			
			System.out.println("TimeStamp: " + nanos + " Current: " + nextSecond);
			System.out.println("Milliseconds Delay: " + precisionLevel / 1_000_000);
			
			if (nanos >= nextSecond) {
				nextSecond = nanos + precisionLevel;
				
				// With special key pressed
				// (we want [LEFT] and [DOWN] side of the rectangle to be
				// movable)
				
				// No Special Key is Pressed
				// (we want [RIGHT] and [UP] side of the rectangle to be
				// movable)
				
				// ------------------------------
				if (data.getRightPressed().get()) {
					if (data.getShiftPressed().get()) { // Special Key?
						if (data.getMouseXNow() > data.getMouseXPressed()) { // Mouse gone Right?
							data.setMouseXPressed(data.getMouseXPressed() + 1);
						} else {
							data.setMouseXNow(data.getMouseXNow() + 1);
						}
					} else {
						if (data.getMouseXNow() > data.getMouseXPressed()) { // Mouse gone Right?
							data.setMouseXNow(data.getMouseXNow() + 1);
						} else {
							data.setMouseXPressed(data.getMouseXPressed() + 1);
						}
					}
				}
				
				if (data.getLeftPressed().get()) {
					if (data.getShiftPressed().get()) { // Special Key?
						if (data.getMouseXNow() > data.getMouseXPressed()) { // Mouse gone Right?
							data.setMouseXPressed(data.getMouseXPressed() - 1);
						} else {
							data.setMouseXNow(data.getMouseXNow() - 1);
						}
					} else {
						if (data.getMouseXNow() > data.getMouseXPressed()) { // Mouse gone Right?
							data.setMouseXNow(data.getMouseXNow() - 1);
						} else {
							data.setMouseXPressed(data.getMouseXPressed() - 1);
						}
					}
				}
				
				if (data.getUpPressed().get()) {
					if (data.getShiftPressed().get()) { // Special Key?
						if (data.getMouseYNow() > data.getMouseYPressed()) { // Mouse gone UP?
							data.setMouseYNow(data.getMouseYNow() - 1);
						} else {
							data.setMouseYPressed(data.getMouseYPressed() - 1);
						}
					} else {
						if (data.getMouseYNow() > data.getMouseYPressed()) { // Mouse gone UP?
							data.setMouseYPressed(data.getMouseYPressed() - 1);
						} else {
							data.setMouseYNow(data.getMouseYNow() - 1);
						}
					}
				}
				
				if (data.getDownPressed().get()) {
					if (data.getShiftPressed().get()) { // Special Key?
						if (data.getMouseYNow() > data.getMouseYPressed()) { // Mouse gone UP?
							data.setMouseYNow(data.getMouseYNow() + 1);
						} else {
							data.setMouseYPressed(data.getMouseYPressed() + 1);
						}
					} else {
						if (data.getMouseYNow() > data.getMouseYPressed()) { // Mouse gone UP?
							data.setMouseYPressed(data.getMouseYPressed() + 1);
						} else {
							data.setMouseYNow(data.getMouseYNow() + 1);
						}
					}
				}
				
				repaintCanvas();
			}
		}
	};
	
	/**
	 * This AnimationTimer waits until the canvas is cleared before it can capture the screen.
	 */
	AnimationTimer waitFrameRender = new AnimationTimer() {
		private int frameCount = 0;
		
		@Override
		public void start() {
			frameCount = 0;
			super.start();
		}
		
		@Override
		public void handle(final long timestamp) {
			frameCount++;
			if (frameCount >= 5) {
				stop();
				
				// Capture the Image
				BufferedImage image;
				final int[] rect = getRectangleBounds();
				try {
					image = new Robot().createScreenCapture(new Rectangle(rect[0], rect[1], rect[2], rect[3]));
				} catch (final AWTException ex) {
					Logger.getLogger(getClass().getName()).log(Level.INFO, null, ex);
					return;
				} finally {
					mainCanvas.setDisable(false);
				}
				
				// System.out.println("Starting Service")
				
				// Start the Service
				captureService.startService(image);
				
			}
		}
	};
	
	/** The counting thread. */
	Thread countingThread;
	
	/** The main window controller. */
	WindowController mainWindowController;
	
	/** The settings window controller. */
	SettingsWindowController settingsWindowController;
	
	/**
	 * Constructor.
	 */
	public CaptureWindowController(final Stage captureWindowStage) {
		this.captureWindowStage = captureWindowStage;
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "CaptureWindowController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (final IOException ex) {
			//logger.log(Level.SEVERE, "", ex)
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Add the needed references from the other controllers.
	 *
	 * @param mainWindowController
	 *            the main window controller
	 * @param settingsWindowController
	 *            the settings window controller
	 */
	@SuppressWarnings("hiding")
	public void addControllerReferences(final WindowController mainWindowController , final SettingsWindowController settingsWindowController) {
		
		this.mainWindowController = mainWindowController;
		this.settingsWindowController = settingsWindowController;
	}
	
	/**
	 * Will be called as soon as FXML file is loaded.
	 */
	@FXML
	public void initialize() {
		
		stage = new Stage();
		stage.setX(0);
		stage.setY(0);
		//stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")))
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setAlwaysOnTop(true);
		
		// Scene
		final Scene scene = new Scene(stackPane, data.getScreenWidth(), data.getScreenHeight(), Color.TRANSPARENT);
		scene.setCursor(Cursor.NONE);
		stage.setScene(scene);
		addKeyHandlers();
		
		// Canvas
		mainCanvas.setWidth(data.getScreenWidth());
		mainCanvas.setHeight(data.getScreenHeight());
		mainCanvas.setOnMousePressed(m -> {
			if (m.getButton() == MouseButton.PRIMARY) {
				data.setMouseXPressed((int) m.getScreenX());
				data.setMouseYPressed((int) m.getScreenY());
			}
		});
		
		mainCanvas.setOnMouseDragged(m -> {
			if (m.getButton() == MouseButton.PRIMARY) {
				data.setMouseXNow((int) m.getScreenX());
				data.setMouseYNow((int) m.getScreenY());
				repaintCanvas();
			}
		});
		
		// graphics context 2D
		gc = mainCanvas.getGraphicsContext2D();
		gc.setLineDashes(6);
		gc.setFont(Font.font("null", FontWeight.BOLD, 14));
		
		// HideFeaturesPressed
		data.getHideExtraFeatures().addListener((observable , oldValue , newValue) -> repaintCanvas());
	}
	
	/**
	 * Adds the KeyHandlers to the Scene.
	 */
	private void addKeyHandlers() {
		
		// -------------Read the below to understand the Code-------------------
		
		// the default prototype of the below code is
		// 1->when the user is pressing RIGHT ARROW -> The rectangle is
		// increasing from the RIGHT side
		// 2->when the user is pressing LEFT ARROW -> The rectangle is
		// decreasing from the RIGHT side
		// 3->when the user is pressing UP ARROW -> The rectangle is increasing
		// from the UP side
		// 4->when the user is pressing DOWN ARROW -> The rectangle is
		// decreasing from the UP side
		
		// when ->LEFT KEY <- is pressed
		// 1->when the user is pressing RIGHT ARROW -> The rectangle is
		// increasing from the LEFT side
		// 2->when the user is pressing LEFT ARROW -> The rectangle is
		// decreasing from the LEFT side
		// 3->when the user is pressing UP ARROW -> The rectangle is increasing
		// from the DOWN side
		// 4->when the user is pressing DOWN ARROW -> The rectangle is
		// decreasing from the DOWN side
		
		// kemodel.yPressed
		stage.getScene().setOnKeyPressed(key -> {
			if (key.isShiftDown())
				data.getShiftPressed().set(true);
			
			if (key.getCode() == KeyCode.LEFT)
				data.getLeftPressed().set(true);
			
			if (key.getCode() == KeyCode.RIGHT)
				data.getRightPressed().set(true);
			
			if (key.getCode() == KeyCode.UP)
				data.getUpPressed().set(true);
			
			if (key.getCode() == KeyCode.DOWN)
				data.getDownPressed().set(true);
			
			if (key.getCode() == KeyCode.H)
				data.getHideExtraFeatures().set(true);
			
		});
		
		// keyReleased
		stage.getScene().setOnKeyReleased(key -> {
			
			if (key.getCode() == KeyCode.SHIFT)
				data.getShiftPressed().set(false);
			
			if (key.getCode() == KeyCode.RIGHT) {
				if (key.isControlDown()) {
					data.setMouseXNow((int) stage.getWidth());
					repaintCanvas();
				}
				data.getRightPressed().set(false);
			}
			
			if (key.getCode() == KeyCode.LEFT) {
				if (key.isControlDown()) {
					data.setMouseXPressed(0);
					repaintCanvas();
				}
				data.getLeftPressed().set(false);
			}
			
			if (key.getCode() == KeyCode.UP) {
				if (key.isControlDown()) {
					data.setMouseYPressed(0);
					repaintCanvas();
				}
				data.getUpPressed().set(false);
			}
			
			if (key.getCode() == KeyCode.DOWN) {
				if (key.isControlDown()) {
					data.setMouseYNow((int) stage.getHeight());
					repaintCanvas();
				}
				data.getDownPressed().set(false);
			}
			
			if (key.getCode() == KeyCode.A && key.isControlDown())
				selectWholeScreen();
			
			if (key.getCode() == KeyCode.H)
				data.getHideExtraFeatures().set(false);
			
			if (key.getCode() == KeyCode.ESCAPE || key.getCode() == KeyCode.BACK_SPACE) {
				
				// Stop Counting Thread
				if (countingThread != null)
					countingThread.interrupt();
				
				// Stop MaryTTS
				//Main.textToSpeech.stopSpeaking();
				
				// Deactivate all keys
				deActivateAllKeys();
				
				// show the appropriate windows
				captureWindowStage.show();
				stage.close();
			} else if (key.getCode() == KeyCode.ENTER || key.getCode() == KeyCode.SPACE) {
				// Stop MaryTTS
				//Main.textToSpeech.stopSpeaking();
				
				// Deactivate all keys
				deActivateAllKeys();
				
				// Capture Selected Area
				prepareImage();
			}
			
		});
		
		data.getAnyPressed().addListener((obs , wasPressed , isNowPressed) ->
		
		{
			if (isNowPressed.booleanValue()) {
				yPressedAnimation.start();
			} else {
				yPressedAnimation.stop();
			}
		});
	}
	
	/**
	 * Deactivates the keys contained into this method.
	 */
	private void deActivateAllKeys() {
		data.getShiftPressed().set(false);
		data.getUpPressed().set(false);
		data.getRightPressed().set(false);
		data.getDownPressed().set(false);
		data.getLeftPressed().set(false);
		data.getHideExtraFeatures().set(false);
	}
	
	/**
	 * Creates and saves the image.
	 */
	public void prepareImage() {
		// return if it is alive
		if ( ( countingThread != null && countingThread.isAlive() ) || captureService.isRunning())
			return;
		
		countingThread = new Thread(() -> {
			mainCanvas.setDisable(true);
			boolean interrupted = false;
			
			// CountDown
			if (!mainWindowController.getTimeSlider().isDisabled()) {
				for (int i = (int) mainWindowController.getTimeSlider().getValue(); i > 0; i--) {
					final int a = i;
					
					// Lock until it has been refreshed from JavaFX
					// Application Thread
					final CountDownLatch count = new CountDownLatch(1);
					
					// Repaint the Canvas
					Platform.runLater(() -> {
						gc.clearRect(0, 0, stage.getWidth(), stage.getHeight());
						gc.setFill(data.background);
						gc.fillRect(0, 0, stage.getWidth(), stage.getHeight());
						gc.setFill(Color.BLACK);
						gc.fillOval(stage.getWidth() / 2 - 90, stage.getHeight() / 2 - 165, 250, 250);
						gc.setFill(Color.WHITE);
						gc.setFont(Font.font("", FontWeight.BOLD, 120));
						gc.fillText(Integer.toString(a), stage.getWidth() / 2, stage.getHeight() / 2);
						
						// Unlock the Parent Thread
						count.countDown();
					});
					
					try {
						// Wait JavaFX Application Thread
						count.await();
						
						// MaryTTS
						//if (settingsWindowController.getMarryTTSToggle().isSelected())
						//    Main.textToSpeech.speak(i);
						
						// Sleep 1 seconds after that
						Thread.sleep(980);
					} catch (final InterruptedException ex) {
						interrupted = true;
						mainCanvas.setDisable(false);
						countingThread.interrupt();
						Logger.getLogger(getClass().getName()).log(Level.INFO, null, ex);
						break;
					}
				}
			}
			
			// !interrupted?
			if (!Thread.interrupted()) {
				// MaryTTS
				//if (settingsWindowController.getMarryTTSToggle().isSelected())
				//   Main.textToSpeech.speak("Select where the image will be saved.");
				
				Platform.runLater(() -> {
					// Clear the canvas
					gc.clearRect(0, 0, stage.getWidth(), stage.getHeight());
					
					// Wait for frame Render
					waitFrameRender.start();
				});
			} // !interrupted?
		});
		
		countingThread.setDaemon(true);
		countingThread.start();
		
	}
	
	/**
	 * Repaint the canvas of the capture window.
	 */
	protected void repaintCanvas() {
		
		gc.clearRect(0, 0, stage.getWidth(), stage.getHeight());
		gc.setFill(Color.rgb(0, 0, 0, 0.8));
		gc.fillRect(0, 0, stage.getWidth(), stage.getHeight());
		
		gc.setFont(data.font);
		
		// draw the actual rectangle
		gc.setStroke(Color.RED);
		// gc.setFill(model.background)
		gc.setLineWidth(1);
		
		// smart calculation of where the mouse has been dragged
		data.setRectWidth( ( data.getMouseXNow() > data.getMouseXPressed() ) ? data.getMouseXNow() - data.getMouseXPressed() : data.getMouseXPressed() - data.getMouseXNow());
		data.setRectHeight( ( data.getMouseYNow() > data.getMouseYPressed() ) ? data.getMouseYNow() - data.getMouseYPressed() : data.getMouseYPressed() - data.getMouseYNow());
		
		data.setRectUpperLeftX( ( data.getMouseXNow() > data.getMouseXPressed() ) ? data.getMouseXPressed() : data.getMouseXNow());
		data.setRectUpperLeftY( ( data.getMouseYNow() > data.getMouseYPressed() ) ? data.getMouseYPressed() : data.getMouseYNow());
		
		gc.strokeRect(data.getRectUpperLeftX() - 1.00, data.getRectUpperLeftY() - 1.00, data.getRectWidth() + 2.00, data.getRectHeight() + 2.00);
		// gc.fillRect(model.rectUpperLeftX, model.rectUpperLeftY,model.rectWidth, model.rectHeight)
		gc.clearRect(data.getRectUpperLeftX(), data.getRectUpperLeftY(), data.getRectWidth(), data.getRectHeight());
		
		// draw the text
		if (!data.getHideExtraFeatures().getValue()) {
			
			// Show the Size
			final double middle = data.getRectUpperLeftX() + data.getRectWidth() / 2.00;
			gc.setLineWidth(1);
			//			gc.setStroke(Color.FIREBRICK);
			//			gc.strokeRect(middle - 78,
			//			        model.rectUpperLeftY < 25 ? model.rectUpperLeftY + 2 : model.rectUpperLeftY - 26.00, 79, 25);
			gc.setFill(Color.FIREBRICK);
			gc.fillRect(middle - 77, data.getRectUpperLeftY() < 25 ? data.getRectUpperLeftY() + 2 : data.getRectUpperLeftY() - 25.00, 77, 23);
			gc.setFill(Color.WHITE);
			gc.fillText(data.getRectWidth() + "," + data.getRectHeight(), middle - 77 + 9,
					data.getRectUpperLeftY() < 25 ? data.getRectUpperLeftY() + 17.00 : data.getRectUpperLeftY() - 6.00);
			
		}
	}
	
	/**
	 * Selects whole Screen.
	 */
	private void selectWholeScreen() {
		data.setMouseXPressed(0);
		data.setMouseYPressed(0);
		data.setMouseXNow((int) stage.getWidth());
		data.setMouseYNow((int) stage.getHeight());
		repaintCanvas();
	}
	
	/**
	 * Prepares the Window for the User.
	 */
	public void prepareForCapture() {
		stage.show();
		repaintCanvas();
		captureWindowStage.close();
		settingsWindowController.getStage().close();
		//if (settingsWindowController.getMarryTTSToggle().isSelected())
		//  Main.textToSpeech.speak("Select an area of the screen dragging your mouse and then press Enter or Space");
	}
	
	/**
	 * Return an array witch contains the (UPPER_LEFT) Point2D of the rectangle and the width and height of the rectangle.
	 *
	 * @return An array witch contains the (UPPER_LEFT) Point2D of the rectangle and the width and height of the rectangle
	 */
	public int[] getRectangleBounds() {
		
		return new int[]{ data.getRectUpperLeftX() , data.getRectUpperLeftY() , data.getRectWidth() , data.getRectHeight() };
		
	}
	
	/**
	 * The work of the Service is to capture the Image based on the rectangle that user drawn of the Screen.
	 *
	 * @author GOXR3PLUS
	 */
	public class CaptureService extends Service<Boolean> {
		
		/** The file path. */
		String filePath;
		
		/** The image. */
		BufferedImage image;
		
		/**
		 * Constructor.
		 */
		public CaptureService() {
			
			setOnSucceeded(s -> done());
			
			setOnCancelled(c -> done());
			
			setOnFailed(f -> done());
			
		}
		
		/**
		 * Starts the Service.
		 *
		 * @param image2
		 *            The image to be saved.
		 */
		public void startService(final BufferedImage image2) {
			if (isRunning()) //Check if running
				return;
			
			this.image = image2;
			
			// Show the SaveDialog
			fileSaver.get().setInitialFileName("ScreenShot" + data.getRandom().nextInt(50000));
			final File file = fileSaver.get().showSaveDialog(stage);
			if (file == null)
				repaintCanvas();
			else {
				filePath = file.getAbsolutePath();
				reset();
				start();
			}
		}
		
		/**
		 * Service has been done.
		 */
		private void done() {
			
			captureWindowStage.show();
			stage.close();
			
			//Was it seccussful?
			if (!getValue())
				ActionTool.showNotification("Error", "Failed to capture the Screen!", Duration.millis(2000), NotificationType.ERROR);
			else
				ActionTool.showNotification("Successful Capturing", "Image is being saved at:\n" + filePath, Duration.millis(2000), NotificationType.INFORMATION);
		}
		
		/*
		 * (non-Javadoc)
		 * @see javafx.concurrent.Service#createTask()
		 */
		@Override
		protected Task<Boolean> createTask() {
			return new Task<Boolean>() {
				@Override
				protected Boolean call() throws Exception {
					
					boolean written = false;
					
					// Try to write the file to the disc
					try {
						written = ImageIO.write(image, fileSaver.get().getSelectedExtensionFilter().getDescription(), new File(filePath));
					} catch (final IOException ex) {
						Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
						return written;
					}
					
					return written;
				}
				
			};
		}
		
	}
	
}
