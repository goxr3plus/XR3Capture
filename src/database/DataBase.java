/**
 * 
 */
package database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import application.controllers.SettingsWindowController;

/**
 * @author GOXR3PLUS
 *
 */
public class DataBase {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(DataBase.class.getName());
	
	public static final String JSonFileName = "XR3Capture_V9.json";
	
	/**
	 * Save an Object as JSON file
	 * 
	 * @param object
	 * @param fileName
	 */
	public static void save(Object object , String fileName) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		// Object to JSON in file
		try {
			mapper.writeValue(new File(fileName), object);
			
			// Object to JSON in String
			String jsonInString = mapper.writeValueAsString(object);
			System.out.println(jsonInString);
		} catch (JsonGenerationException ex) {
			logger.severe("Object, String - exception: " + ex); //$NON-NLS-1$
			
		} catch (JsonMappingException ex) {
			logger.severe("Object, String - exception: " + ex); //$NON-NLS-1$
			
		} catch (IOException ex) {
			logger.severe("Object, String - exception: " + ex); //$NON-NLS-1$
			
		}
		
	}
	
	/**
	 * Retrieves an Object which has been saved as JSON File
	 * 
	 * @param object
	 * @param objectClass
	 * @param fileName
	 * @return An Object
	 */
	public static Object retrieve(Object object , Class<?> objectClass , String fileName) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		// Object to JSON in file
		try {
			// JSON from file to Object
			object = mapper.readValue(new File(fileName), objectClass);
		} catch (JsonGenerationException ex) {
			logger.severe("Object, Class<?>, String - exception: " + ex); //$NON-NLS-1$
			
		} catch (JsonMappingException ex) {
			logger.severe("Object, Class<?>, String - exception: " + ex); //$NON-NLS-1$
			
		} catch (IOException ex) {
			logger.severe("Object, Class<?>, String - exception: " + ex); //$NON-NLS-1$
			
		}
		return mapper;
	}
	
	/**
	 * @return The absolute path of the current directory
	 */
	public static String getCurrentDirectoryPath() {
		
		String local = DataBase.class.getClassLoader().getResource("").getPath().replaceAll("%20", " ");
		
		// starts with File.separator?
		String l = local.replaceFirst("[" + File.separator + "/\\\\]", "");
		System.out.println(l);
		return l;
	}
	
	/**
	 * Saves all the settings for the application
	 * 
	 * @param settingsWindowController
	 * @return True if succeeded
	 */
	@SuppressWarnings("unchecked")
	public static boolean saveDataBaseSettings(SettingsWindowController settingsWindowController) {
		
		JSONObject obj = new JSONObject();
		obj.put("RightOrientation", settingsWindowController.getOrientation().isSelected());
		obj.put("MaryTTS", settingsWindowController.getMarryTTSToggle().isSelected());
		obj.put("PrecisionSliderValue", settingsWindowController.getPrecisionSlider().getValue());
		
		try (FileWriter file = new FileWriter(getCurrentDirectoryPath() + JSonFileName)) {
			file.write(obj.toJSONString());
			file.flush();
		} catch (IOException e) {
			logger.severe("SettingsWindowController - exception: " + e); //$NON-NLS-1$
			return false;
		}
		
		return true;
	}
	
	/**
	 * Retrieves all the settings of the application
	 * 
	 * @param settingsWindowController
	 * @return True if succeeded
	 */
	public static boolean loadDataBaseSettings(SettingsWindowController settingsWindowController) {
		if (!new File(getCurrentDirectoryPath() + DataBase.JSonFileName).exists())
			return false;
		
		JSONParser parser = new JSONParser();
		
		try (FileReader fileReader = new FileReader(getCurrentDirectoryPath() + DataBase.JSonFileName)) {
			Object obj = parser.parse(fileReader);
			
			JSONObject jsonObject = (JSONObject) obj;
			
			settingsWindowController.getOrientation().setSelected((boolean) jsonObject.get("RightOrientation"));
			settingsWindowController.getMarryTTSToggle().setSelected((boolean) jsonObject.get("MaryTTS"));
			settingsWindowController.getPrecisionSlider().setValue((double) jsonObject.get("PrecisionSliderValue"));
			
		} catch (IOException | ParseException e) {
			logger.severe("SettingsWindowController - exception: " + e); //$NON-NLS-1$
			return false;
		}
		
		return true;
		
	}
	
}
