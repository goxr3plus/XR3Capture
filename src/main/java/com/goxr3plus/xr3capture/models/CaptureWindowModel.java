/**
 * 
 */
package main.java.com.goxr3plus.xr3capture.models;

import java.util.Random;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;

/**
 * @author GOXR3PLUS
 *
 */
public class CaptureWindowModel {
	
	/** The random. */
	private Random random = new Random();
	
	/** The x pressed. */
	private int mouseXPressed = 0;
	
	/** The y pressed. */
	private int mouseYPressed = 0;
	
	/** The x now. */
	private int mouseXNow = 0;
	
	/** The y now. */
	private int mouseYNow = 0;
	
	/** The upper left X. */
	private int rectUpperLeftX = 0;
	
	/** The upper left Y. */
	private int rectUpperLeftY = 0;
	
	/** The rectangle width. */
	private int rectWidth;
	
	/** The rectangle height. */
	private int rectHeight;
	
	// ----------------
	
	/** The background. */
	public Color background = Color.rgb(0, 0, 0, 0.3);
	
	/** The font. */
	public Font font = Font.font("", FontWeight.BOLD, 14);
	
	// ---------------
	
	/** The shift pressed. */
	private BooleanProperty shiftPressed = new SimpleBooleanProperty();
	
	/** The up pressed. */
	private BooleanProperty upPressed = new SimpleBooleanProperty();
	
	/** The right pressed. */
	private BooleanProperty rightPressed = new SimpleBooleanProperty();
	
	/** The down pressed. */
	private BooleanProperty downPressed = new SimpleBooleanProperty();
	
	/** The left pressed. */
	private BooleanProperty leftPressed = new SimpleBooleanProperty();
	
	/** The any pressed. */
	private BooleanBinding anyPressed = upPressed.or(downPressed).or(leftPressed).or(rightPressed);
	
	/** The hide extra features. */
	private BooleanProperty hideExtraFeatures = new SimpleBooleanProperty();
	
	// ------------
	
	/** The screen width. */
	private int screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
	
	/** The screen height. */
	private int screenHeight = (int) Screen.getPrimary().getBounds().getHeight();

	/**
	 * @return the rightPressed
	 */
	public BooleanProperty getRightPressed() {
		return rightPressed;
	}

	/**
	 * @param rightPressed the rightPressed to set
	 */
	public void setRightPressed(final BooleanProperty rightPressed) {
		this.rightPressed = rightPressed;
	}

	/**
	 * @return the mouseXPressed
	 */
	public int getMouseXPressed() {
		return mouseXPressed;
	}

	/**
	 * @param mouseXPressed the mouseXPressed to set
	 */
	public void setMouseXPressed(final int mouseXPressed) {
		this.mouseXPressed = mouseXPressed;
	}

	/**
	 * @return the shiftPressed
	 */
	public BooleanProperty getShiftPressed() {
		return shiftPressed;
	}

	/**
	 * @param shiftPressed the shiftPressed to set
	 */
	public void setShiftPressed(final BooleanProperty shiftPressed) {
		this.shiftPressed = shiftPressed;
	}

	/**
	 * @return the mouseXNow
	 */
	public int getMouseXNow() {
		return mouseXNow;
	}

	/**
	 * @param mouseXNow the mouseXNow to set
	 */
	public void setMouseXNow(final int mouseXNow) {
		this.mouseXNow = mouseXNow;
	}

	/**
	 * @return the leftPressed
	 */
	public BooleanProperty getLeftPressed() {
		return leftPressed;
	}

	/**
	 * @param leftPressed the leftPressed to set
	 */
	public void setLeftPressed(final BooleanProperty leftPressed) {
		this.leftPressed = leftPressed;
	}

	/**
	 * @return the mouseYPressed
	 */
	public int getMouseYPressed() {
		return mouseYPressed;
	}

	/**
	 * @param mouseYPressed the mouseYPressed to set
	 */
	public void setMouseYPressed(final int mouseYPressed) {
		this.mouseYPressed = mouseYPressed;
	}

	/**
	 * @return the mouseYNow
	 */
	public int getMouseYNow() {
		return mouseYNow;
	}

	/**
	 * @param mouseYNow the mouseYNow to set
	 */
	public void setMouseYNow(final int mouseYNow) {
		this.mouseYNow = mouseYNow;
	}

	/**
	 * @return the upPressed
	 */
	public BooleanProperty getUpPressed() {
		return upPressed;
	}

	/**
	 * @param upPressed the upPressed to set
	 */
	public void setUpPressed(final BooleanProperty upPressed) {
		this.upPressed = upPressed;
	}

	/**
	 * @return the downPressed
	 */
	public BooleanProperty getDownPressed() {
		return downPressed;
	}

	/**
	 * @param downPressed the downPressed to set
	 */
	public void setDownPressed(final BooleanProperty downPressed) {
		this.downPressed = downPressed;
	}

	/**
	 * @return the rectUpperLeftY
	 */
	public int getRectUpperLeftY() {
		return rectUpperLeftY;
	}

	/**
	 * @param rectUpperLeftY the rectUpperLeftY to set
	 */
	public void setRectUpperLeftY(final int rectUpperLeftY) {
		this.rectUpperLeftY = rectUpperLeftY;
	}

	/**
	 * @return the rectHeight
	 */
	public int getRectHeight() {
		return rectHeight;
	}

	/**
	 * @param rectHeight the rectHeight to set
	 */
	public void setRectHeight(final int rectHeight) {
		this.rectHeight = rectHeight;
	}

	/**
	 * @return the hideExtraFeatures
	 */
	public BooleanProperty getHideExtraFeatures() {
		return hideExtraFeatures;
	}

	/**
	 * @param hideExtraFeatures the hideExtraFeatures to set
	 */
	public void setHideExtraFeatures(final BooleanProperty hideExtraFeatures) {
		this.hideExtraFeatures = hideExtraFeatures;
	}

	/**
	 * @return the rectUpperLeftX
	 */
	public int getRectUpperLeftX() {
		return rectUpperLeftX;
	}

	/**
	 * @param rectUpperLeftX the rectUpperLeftX to set
	 */
	public void setRectUpperLeftX(final int rectUpperLeftX) {
		this.rectUpperLeftX = rectUpperLeftX;
	}

	/**
	 * @return the rectWidth
	 */
	public int getRectWidth() {
		return rectWidth;
	}

	/**
	 * @param rectWidth the rectWidth to set
	 */
	public void setRectWidth(final int rectWidth) {
		this.rectWidth = rectWidth;
	}

	/**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * @param random the random to set
	 */
	public void setRandom(final Random random) {
		this.random = random;
	}

	/**
	 * @return the screenWidth
	 */
	public int getScreenWidth() {
		return screenWidth;
	}

	/**
	 * @param screenWidth the screenWidth to set
	 */
	public void setScreenWidth(final int screenWidth) {
		this.screenWidth = screenWidth;
	}

	/**
	 * @return the screenHeight
	 */
	public int getScreenHeight() {
		return screenHeight;
	}

	/**
	 * @param screenHeight the screenHeight to set
	 */
	public void setScreenHeight(final int screenHeight) {
		this.screenHeight = screenHeight;
	}

	/**
	 * @return the anyPressed
	 */
	public BooleanBinding getAnyPressed() {
		return anyPressed;
	}

	/**
	 * @param anyPressed the anyPressed to set
	 */
	public void setAnyPressed(final BooleanBinding anyPressed) {
		this.anyPressed = anyPressed;
	}

	/**
	 * @return the background
	 */
	public Color getBackground() {
		return background;
	}

	/**
	 * @param background the background to set
	 */
	public void setBackground(final Color background) {
		this.background = background;
	}
	
}
