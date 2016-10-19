/**
 * 
 */
package application;

import java.util.Arrays;

import javax.imageio.ImageIO;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * A special implementation of FileChooser
 * 
 * @author GOXR3PLUS
 *
 */
public class SFileChooser {
	
	private FileChooser fileChooser = new FileChooser();
	
	/**
	 * 
	 */
	public SFileChooser() {
		fileChooser.setTitle("Save Image");
		
		// Extension Filter + Descriptions
		fileChooser.getExtensionFilters().add(new ExtensionFilter("png", ".png"));
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
				fileChooser.getExtensionFilters().add(new ExtensionFilter(format, description));
			// System.out.println(format);
		});
		
	}
	
	/**
	 * @return the FileChooser
	 */
	public FileChooser get() {
		return fileChooser;
	}
}
