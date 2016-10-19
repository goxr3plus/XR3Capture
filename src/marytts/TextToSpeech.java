/**
 * 
 */
package marytts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;

import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.modules.synthesis.Voice;

/**
 * @author GOXR3PLUS
 *
 */
public class TextToSpeech {
	
	private AudioPlayer		tts;
	private MaryInterface	marytts;
	Map<Integer,String>		numbersMap	= new HashMap<>();
	
	/**
	 * Constructor
	 */
	public TextToSpeech() {
		try {
			marytts = new LocalMaryInterface();
			
			// Bunch of voices available
			Voice.getAvailableVoices().stream().forEach(System.out::println);
			marytts.setVoice("dfki-poppy-hsmm");
			
		} catch (MaryConfigurationException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
		}
		
		numbersMap.put(1, "one");
		numbersMap.put(2, "two");
		numbersMap.put(3, "three");
		numbersMap.put(4, "four");
		numbersMap.put(5, "five");
		numbersMap.put(6, "six");
		numbersMap.put(7, "seven");
		numbersMap.put(8, "eight");
		numbersMap.put(9, "nine");
	}
	
	/**
	 * Change the default voice of the MaryTTS
	 * 
	 * @param voice
	 */
	public void setVoice(String voice) {
		marytts.setVoice(voice);
	}
	
	/**
	 * Transform number to speech
	 * 
	 * @param number
	 */
	public void speak(int number) {
		speak(numbersMap.get(number));
	}
	
	/**
	 * Stop the MaryTTS from Speaking
	 */
	public void stopSpeaking() {
		// Stop the previous player
		if (tts != null)
			tts.cancel();
	}
	
	/**
	 * Transform text to speech
	 * 
	 * @param text
	 */
	public void speak(String text) {
		
		// Stop the previous player
		stopSpeaking();
		
		try (AudioInputStream audio = marytts.generateAudio(text)) {
			
			// Player is a thread(threads can only run one time) so it can be
			// used has to be initiated every time
			tts = new AudioPlayer();
			tts.setAudio(audio);
			tts.setDaemon(true);
			tts.start();
			
		} catch (SynthesisException ex) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error saying phrase.", ex);
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "IO Exception", ex);
		}
	}
}
