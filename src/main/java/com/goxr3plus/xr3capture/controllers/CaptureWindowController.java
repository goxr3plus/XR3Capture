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
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import main.java.com.goxr3plus.xr3capture.application.CaptureWindow;
import main.java.com.goxr3plus.xr3capture.tools.ActionTool;
import main.java.com.goxr3plus.xr3capture.tools.NotificationType;
import main.java.com.goxr3plus.xr3capture.tools.SFileChooser;

/**
 * This is the Window which is used from the user to draw the rectangle representing an area on the screen to be captured.
 *
 * @author GOXR3PLUS
 */
public class CaptureWindowController extends Stage {

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
	    precisionLevel = (long) (settingsWindowController.getPrecisionSlider().getValue() * 1_000_000L);
	    super.start();
	}

	@Override
	public void handle(long nanos) {

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
		if (data.rightPressed.get()) {
		    if (data.shiftPressed.get()) { // Special Key?
			if (data.mouseXNow > data.mouseXPressed) { // Mouse gone Right?
			    data.mouseXPressed += 1;
			} else {
			    data.mouseXNow += 1;
			}
		    } else {
			if (data.mouseXNow > data.mouseXPressed) { // Mouse gone Right?
			    data.mouseXNow += 1;
			} else {
			    data.mouseXPressed += 1;
			}
		    }
		}

		if (data.leftPressed.get()) {
		    if (data.shiftPressed.get()) { // Special Key?
			if (data.mouseXNow > data.mouseXPressed) { // Mouse gone Right?
			    data.mouseXPressed -= 1;
			} else {
			    data.mouseXNow -= 1;
			}
		    } else {
			if (data.mouseXNow > data.mouseXPressed) { // Mouse gone Right?
			    data.mouseXNow -= 1;
			} else {
			    data.mouseXPressed -= 1;
			}
		    }
		}

		if (data.upPressed.get()) {
		    if (data.shiftPressed.get()) { // Special Key?
			if (data.mouseYNow > data.mouseYPressed) { // Mouse gone UP?
			    data.mouseYNow -= 1;
			} else {
			    data.mouseYPressed -= 1;
			}
		    } else {
			if (data.mouseYNow > data.mouseYPressed) { // Mouse gone UP?
			    data.mouseYPressed -= 1;
			} else {
			    data.mouseYNow -= 1;
			}
		    }
		}

		if (data.downPressed.get()) {
		    if (data.shiftPressed.get()) { // Special Key?
			if (data.mouseYNow > data.mouseYPressed) { // Mouse gone UP?
			    data.mouseYNow += 1;
			} else {
			    data.mouseYPressed += 1;
			}
		    } else {
			if (data.mouseYNow > data.mouseYPressed) { // Mouse gone UP?
			    data.mouseYPressed += 1;
			} else {
			    data.mouseYNow += 1;
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
	public void handle(long timestamp) {
	    frameCount++;
	    if (frameCount >= 5) {
		stop();

		// Capture the Image
		BufferedImage image;
		int[] rect = getRectangleBounds();
		try {
		    image = new Robot().createScreenCapture(new Rectangle(rect[0], rect[1], rect[2], rect[3]));
		} catch (AWTException ex) {
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
    MainWindowController mainWindowController;

    /** The settings window controller. */
    SettingsWindowController settingsWindowController;

    /**
     * Constructor.
     */
    public CaptureWindowController() {

	setX(0);
	setY(0);
	getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
	initStyle(StageStyle.TRANSPARENT);
	setAlwaysOnTop(true);

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
    public void addControllerReferences(MainWindowController mainWindowController, SettingsWindowController settingsWindowController) {

	this.mainWindowController = mainWindowController;
	this.settingsWindowController = settingsWindowController;
    }

    /**
     * Will be called as soon as FXML file is loaded.
     */
    @FXML
    public void initialize() {

	// System.out.println("CaptureWindow initialized")

	// Scene
	Scene scene = new Scene(stackPane, data.screenWidth, data.screenHeight, Color.TRANSPARENT);
	scene.setCursor(Cursor.NONE);
	setScene(scene);
	addKeyHandlers();

	// Canvas
	mainCanvas.setWidth(data.screenWidth);
	mainCanvas.setHeight(data.screenHeight);
	mainCanvas.setOnMousePressed(m -> {
	    if (m.getButton() == MouseButton.PRIMARY) {
		data.mouseXPressed = (int) m.getScreenX();
		data.mouseYPressed = (int) m.getScreenY();
	    }
	});

	mainCanvas.setOnMouseDragged(m -> {
	    if (m.getButton() == MouseButton.PRIMARY) {
		data.mouseXNow = (int) m.getScreenX();
		data.mouseYNow = (int) m.getScreenY();
		repaintCanvas();
	    }
	});

	// graphics context 2D
	gc = mainCanvas.getGraphicsContext2D();
	gc.setLineDashes(6);
	gc.setFont(Font.font("null", FontWeight.BOLD, 14));

	// HideFeaturesPressed
	data.hideExtraFeatures.addListener((observable, oldValue, newValue) -> repaintCanvas());
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
	getScene().setOnKeyPressed(key -> {
	    if (key.isShiftDown())
		data.shiftPressed.set(true);

	    if (key.getCode() == KeyCode.LEFT)
		data.leftPressed.set(true);

	    if (key.getCode() == KeyCode.RIGHT)
		data.rightPressed.set(true);

	    if (key.getCode() == KeyCode.UP)
		data.upPressed.set(true);

	    if (key.getCode() == KeyCode.DOWN)
		data.downPressed.set(true);

	    if (key.getCode() == KeyCode.H)
		data.hideExtraFeatures.set(true);

	});

	// keyReleased
	getScene().setOnKeyReleased(key -> {

	    if (key.getCode() == KeyCode.SHIFT)
		data.shiftPressed.set(false);

	    if (key.getCode() == KeyCode.RIGHT) {
		if (key.isControlDown()) {
		    data.mouseXNow = (int) getWidth();
		    repaintCanvas();
		}
		data.rightPressed.set(false);
	    }

	    if (key.getCode() == KeyCode.LEFT) {
		if (key.isControlDown()) {
		    data.mouseXPressed = 0;
		    repaintCanvas();
		}
		data.leftPressed.set(false);
	    }

	    if (key.getCode() == KeyCode.UP) {
		if (key.isControlDown()) {
		    data.mouseYPressed = 0;
		    repaintCanvas();
		}
		data.upPressed.set(false);
	    }

	    if (key.getCode() == KeyCode.DOWN) {
		if (key.isControlDown()) {
		    data.mouseYNow = (int) getHeight();
		    repaintCanvas();
		}
		data.downPressed.set(false);
	    }

	    if (key.getCode() == KeyCode.A && key.isControlDown())
		selectWholeScreen();

	    if (key.getCode() == KeyCode.H)
		data.hideExtraFeatures.set(false);

	    if (key.getCode() == KeyCode.ESCAPE || key.getCode() == KeyCode.BACK_SPACE) {

		// Stop Counting Thread
		if (countingThread != null)
		    countingThread.interrupt();

		// Stop MaryTTS
		//Main.textToSpeech.stopSpeaking();

		// Deactivate all keys
		deActivateAllKeys();

		// show the appropriate windows
		CaptureWindow.stage.show();
		close();
	    } else if (key.getCode() == KeyCode.ENTER || key.getCode() == KeyCode.SPACE) {
		// Stop MaryTTS
		//Main.textToSpeech.stopSpeaking();

		// Deactivate all keys
		deActivateAllKeys();

		// Capture Selected Area
		prepareImage();
	    }

	});

	data.anyPressed.addListener((obs, wasPressed, isNowPressed) ->

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
	data.shiftPressed.set(false);
	data.upPressed.set(false);
	data.rightPressed.set(false);
	data.downPressed.set(false);
	data.leftPressed.set(false);
	data.hideExtraFeatures.set(false);
    }

    /**
     * Creates and saves the image.
     */
    public void prepareImage() {
	// return if it is alive
	if ((countingThread != null && countingThread.isAlive()) || captureService.isRunning())
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
		    CountDownLatch count = new CountDownLatch(1);

		    // Repaint the Canvas
		    Platform.runLater(() -> {
			gc.clearRect(0, 0, getWidth(), getHeight());
			gc.setFill(data.background);
			gc.fillRect(0, 0, getWidth(), getHeight());
			gc.setFill(Color.BLACK);
			gc.fillOval(getWidth() / 2 - 90, getHeight() / 2 - 165, 250, 250);
			gc.setFill(Color.WHITE);
			gc.setFont(Font.font("", FontWeight.BOLD, 120));
			gc.fillText(Integer.toString(a), getWidth() / 2, getHeight() / 2);

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
		    } catch (InterruptedException ex) {
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
		    gc.clearRect(0, 0, getWidth(), getHeight());

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

	gc.clearRect(0, 0, getWidth(), getHeight());
	gc.setFill(Color.rgb(0, 0, 0, 0.8));
	gc.fillRect(0, 0, getWidth(), getHeight());

	gc.setFont(data.font);

	// draw the actual rectangle
	gc.setStroke(Color.RED);
	// gc.setFill(model.background)
	gc.setLineWidth(1);

	// smart calculation of where the mouse has been dragged
	data.rectWidth = (data.mouseXNow > data.mouseXPressed) ? data.mouseXNow - data.mouseXPressed // RIGHT
		: data.mouseXPressed - data.mouseXNow // LEFT
	;
	data.rectHeight = (data.mouseYNow > data.mouseYPressed) ? data.mouseYNow - data.mouseYPressed // DOWN
		: data.mouseYPressed - data.mouseYNow // UP
	;

	data.rectUpperLeftX = // -------->UPPER_LEFT_X
		(data.mouseXNow > data.mouseXPressed) ? data.mouseXPressed // RIGHT
			: data.mouseXNow// LEFT
	;
	data.rectUpperLeftY = // -------->UPPER_LEFT_Y
		(data.mouseYNow > data.mouseYPressed) ? data.mouseYPressed // DOWN
			: data.mouseYNow // UP
	;

	gc.strokeRect(data.rectUpperLeftX - 1.00, data.rectUpperLeftY - 1.00, data.rectWidth + 2.00, data.rectHeight + 2.00);
	// gc.fillRect(model.rectUpperLeftX, model.rectUpperLeftY,model.rectWidth, model.rectHeight)
	gc.clearRect(data.rectUpperLeftX, data.rectUpperLeftY, data.rectWidth, data.rectHeight);

	// draw the text
	if (!data.hideExtraFeatures.getValue()) {

	    // Show the Size
	    double middle = data.rectUpperLeftX + data.rectWidth / 2.00;
	    gc.setLineWidth(1);
	    //			gc.setStroke(Color.FIREBRICK);
	    //			gc.strokeRect(middle - 78,
	    //			        model.rectUpperLeftY < 25 ? model.rectUpperLeftY + 2 : model.rectUpperLeftY - 26.00, 79, 25);
	    gc.setFill(Color.FIREBRICK);
	    gc.fillRect(middle - 77, data.rectUpperLeftY < 25 ? data.rectUpperLeftY + 2 : data.rectUpperLeftY - 25.00, 77, 23);
	    gc.setFill(Color.WHITE);
	    gc.fillText(data.rectWidth + "," + data.rectHeight, middle - 77 + 9,
		    data.rectUpperLeftY < 25 ? data.rectUpperLeftY + 17.00 : data.rectUpperLeftY - 6.00);

	}
    }

    /**
     * Selects whole Screen.
     */
    private void selectWholeScreen() {
	data.mouseXPressed = 0;
	data.mouseYPressed = 0;
	data.mouseXNow = (int) getWidth();
	data.mouseYNow = (int) getHeight();
	repaintCanvas();
    }

    /**
     * Prepares the Window for the User.
     */
    public void prepareForCapture() {
	show();
	repaintCanvas();
	CaptureWindow.stage.close();
	settingsWindowController.close();
	//if (settingsWindowController.getMarryTTSToggle().isSelected())
	//  Main.textToSpeech.speak("Select an area of the screen dragging your mouse and then press Enter or Space");
    }

    /**
     * Return an array witch contains the (UPPER_LEFT) Point2D of the rectangle and the width and height of the rectangle.
     *
     * @return An array witch contains the (UPPER_LEFT) Point2D of the rectangle and the width and height of the rectangle
     */
    public int[] getRectangleBounds() {

	return new int[] { data.rectUpperLeftX, data.rectUpperLeftY, data.rectWidth, data.rectHeight };

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
	public void startService(BufferedImage image2) {
	    if (isRunning()) //Check if running
		return;

	    this.image = image2;

	    // Show the SaveDialog
	    fileSaver.get().setInitialFileName("ScreenShot" + data.random.nextInt(50000));
	    File file = fileSaver.get().showSaveDialog(CaptureWindowController.this);
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

		CaptureWindow.stage.show();
	    close();

	    //Was it seccussful?
	    if (!getValue())
		ActionTool.showNotification("Error", "Failed to capture the Screen!", Duration.millis(2000), NotificationType.ERROR);
	    else
		ActionTool.showNotification("Successful Capturing", "Image is being saved at:\n" + filePath, Duration.millis(2000),
			NotificationType.INFORMATION);
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask() */
	@Override
	protected Task<Boolean> createTask() {
	    return new Task<Boolean>() {
		@Override
		protected Boolean call() throws Exception {

		    boolean written = false;

		    // Try to write the file to the disc
		    try {
			written = ImageIO.write(image, fileSaver.get().getSelectedExtensionFilter().getDescription(), new File(filePath));
		    } catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
			return written;
		    }

		    return written;
		}

	    };
	}

    }

}
