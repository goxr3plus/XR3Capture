/**
 * 
 */
/**
 * @author GOXR3PLUSSTUDIO
 *
 */
module XR3Capture {
	requires transitive java.compiler;
	exports main.java.com.goxr3plus.xr3capture.controllers;
	exports main.java.com.goxr3plus.xr3capture.application;
	exports main.java.com.goxr3plus.xr3capture.tools;
	opens main.java.com.goxr3plus.xr3capture.controllers to javafx.fxml;

	requires com.jfoenix;
	requires java.desktop;
	requires java.logging;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires controlsfx;
}