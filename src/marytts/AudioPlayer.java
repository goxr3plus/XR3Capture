/*
 * 
 */
package marytts;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import application.Main;
import marytts.util.data.audio.MonoAudioInputStream;
import marytts.util.data.audio.StereoAudioInputStream;

// TODO: Auto-generated Javadoc
/**
 * A single Thread Audio Player
 * Once used it has to be reinitialized.
 *
 * @author GOXR3PLUS
 */
public class AudioPlayer extends Thread {
	
	/** The Constant MONO. */
	public static final int		MONO			= 0;
	
	/** The Constant STEREO. */
	public static final int		STEREO			= 3;
	
	/** The Constant LEFT_ONLY. */
	public static final int		LEFT_ONLY		= 1;
	
	/** The Constant RIGHT_ONLY. */
	public static final int		RIGHT_ONLY		= 2;
	
	/** The ais. */
	private AudioInputStream	ais;
	
	/** The line listener. */
	private LineListener		lineListener;
	
	/** The line. */
	private SourceDataLine		line;
	
	/** The output mode. */
	private int					outputMode;
	
	/** The status. */
	private Status				status			= Status.WAITING;
	
	/** The exit requested. */
	private boolean				exitRequested	= false;
	
	/**
	 * The status of the player.
	 *
	 * @author GOXR3PLUS
	 */
	public enum Status {
		
		/** The waiting. */
		WAITING,
		
		/** The playing. */
		PLAYING;
	}
	
	/**
	 * AudioPlayer which can be used if audio stream is to be set separately,
	 * using setAudio().
	 *
	 */
	public AudioPlayer() {
	}
	
	/**
	 * Instantiates a new audio player.
	 *
	 * @param audioFile the audio file
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedAudioFileException the unsupported audio file exception
	 */
	public AudioPlayer(File audioFile) throws IOException, UnsupportedAudioFileException {
		this.ais = AudioSystem.getAudioInputStream(audioFile);
	}
	
	/**
	 * Instantiates a new audio player.
	 *
	 * @param ais the ais
	 */
	public AudioPlayer(AudioInputStream ais) {
		this.ais = ais;
	}
	
	/**
	 * Instantiates a new audio player.
	 *
	 * @param audioFile the audio file
	 * @param lineListener the line listener
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedAudioFileException the unsupported audio file exception
	 */
	public AudioPlayer(File audioFile, LineListener lineListener) throws IOException, UnsupportedAudioFileException {
		this.ais = AudioSystem.getAudioInputStream(audioFile);
		this.lineListener = lineListener;
	}
	
	/**
	 * Instantiates a new audio player.
	 *
	 * @param ais the ais
	 * @param lineListener the line listener
	 */
	public AudioPlayer(AudioInputStream ais, LineListener lineListener) {
		this.ais = ais;
		this.lineListener = lineListener;
	}
	
	/**
	 * Instantiates a new audio player.
	 *
	 * @param audioFile the audio file
	 * @param line the line
	 * @param lineListener the line listener
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedAudioFileException the unsupported audio file exception
	 */
	public AudioPlayer(File audioFile, SourceDataLine line, LineListener lineListener)
			throws IOException, UnsupportedAudioFileException {
		this.ais = AudioSystem.getAudioInputStream(audioFile);
		this.line = line;
		this.lineListener = lineListener;
	}
	
	/**
	 * Instantiates a new audio player.
	 *
	 * @param ais the ais
	 * @param line the line
	 * @param lineListener the line listener
	 */
	public AudioPlayer(AudioInputStream ais, SourceDataLine line, LineListener lineListener) {
		this.ais = ais;
		this.line = line;
		this.lineListener = lineListener;
	}
	
	/**
	 * Instantiates a new audio player.
	 *
	 * @param audioFile        audiofile
	 * @param line        line
	 * @param lineListener        lineListener
	 * @param outputMode        if MONO, force output to be mono; if STEREO, force output to be
	 *        STEREO; if LEFT_ONLY, play a mono signal over
	 *        the left channel of a stereo output, or mute the right channel of
	 *        a stereo signal; if RIGHT_ONLY, do the same
	 *        with the right output channel.
	 * @throws IOException         IOException
	 * @throws UnsupportedAudioFileException         UnsupportedAudioFileException
	 */
	public AudioPlayer(File audioFile, SourceDataLine line, LineListener lineListener, int outputMode)
			throws IOException, UnsupportedAudioFileException {
		this.ais = AudioSystem.getAudioInputStream(audioFile);
		this.line = line;
		this.lineListener = lineListener;
		this.outputMode = outputMode;
	}
	
	/**
	 * Instantiates a new audio player.
	 *
	 * @param ais        ais
	 * @param line        line
	 * @param lineListener        lineListener
	 * @param outputMode        if MONO, force output to be mono; if STEREO, force output to be
	 *        STEREO; if LEFT_ONLY, play a mono signal over
	 *        the left channel of a stereo output, or mute the right channel of
	 *        a stereo signal; if RIGHT_ONLY, do the same
	 *        with the right output channel.
	 */
	public AudioPlayer(AudioInputStream ais, SourceDataLine line, LineListener lineListener, int outputMode) {
		this.ais = ais;
		this.line = line;
		this.lineListener = lineListener;
		this.outputMode = outputMode;
	}
	
	/**
	 * Sets the audio.
	 *
	 * @param audio the new audio
	 */
	public void setAudio(AudioInputStream audio) {
		if (status == Status.PLAYING) {
			throw new IllegalStateException("Cannot set audio while playing");
		}
		this.ais = audio;
	}
	
	/**
	 * Cancel the AudioPlayer which will cause the Thread to exit.
	 */
	public void cancel() {
		if (line != null) {
			line.stop();
		}
		exitRequested = true;
	}
	
	/**
	 * Gets the line.
	 *
	 * @return The SourceDataLine
	 */
	public SourceDataLine getLine() {
		return line;
	}
	
	/**
	 * Sets Gain value. Line should be opened before calling this method. Linear
	 * scale 0.0 <--> 1.0 Threshold Coef. : 1/2 to avoid saturation.
	 *
	 * @param fGain the new gain
	 */
	public void setGain(double fGain) {
		
		System.out.println( ( (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN) ).getValue());
		
		// Better type
		if (line != null && line.isControlSupported(FloatControl.Type.MASTER_GAIN))
			( (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN) )
					.setValue((float) ( 20 * Math.log10(fGain <= 0.0 ? 0.0000 : fGain) ));
		// OR (Math.log(fGain == 0.0 ? 0.0000 : fGain) / Math.log(10.0))
		
		System.out.println( ( (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN) ).getValue());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		
		status = Status.PLAYING;
		AudioFormat audioFormat = ais.getFormat();
		if (audioFormat.getChannels() == 1) {
			if (outputMode != 0) {
				ais = new StereoAudioInputStream(ais, outputMode);
				audioFormat = ais.getFormat();
			}
		} else {
			assert audioFormat.getChannels() == 2 : "Unexpected number of channels: " + audioFormat.getChannels();
			if (outputMode == 0) {
				ais = new MonoAudioInputStream(ais);
			} else if (outputMode == 1 || outputMode == 2) {
				ais = new StereoAudioInputStream(ais, outputMode);
			} else {
				assert outputMode == 3 : "Unexpected output mode: " + outputMode;
			}
		}
		
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		
		try {
			if (line == null) {
				boolean bIsSupportedDirectly = AudioSystem.isLineSupported(info);
				if (!bIsSupportedDirectly) {
					AudioFormat sourceFormat = audioFormat;
					AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
							sourceFormat.getSampleRate(), sourceFormat.getSampleSizeInBits(),
							sourceFormat.getChannels(),
							sourceFormat.getChannels() * ( sourceFormat.getSampleSizeInBits() / 8 ),
							sourceFormat.getSampleRate(), sourceFormat.isBigEndian());
					
					ais = AudioSystem.getAudioInputStream(targetFormat, ais);
					audioFormat = ais.getFormat();
				}
				info = new DataLine.Info(SourceDataLine.class, audioFormat);
				line = (SourceDataLine) AudioSystem.getLine(info);
			}
			if (lineListener != null) {
				line.addLineListener(lineListener);
			}
			line.open(audioFormat);
		} catch (Exception ex) {
			Logger.getLogger(Main.class.getName()).log(Level.WARNING, null, ex);
			return;
		}
		
		line.start();
		setGain(2.0);
		
		int nRead = 0;
		byte[] abData = new byte[65532];
		while ( ( nRead != -1 ) && ( !exitRequested )) {
			try {
				nRead = ais.read(abData, 0, abData.length);
			} catch (IOException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.WARNING, null, ex);
			}
			if (nRead >= 0) {
				line.write(abData, 0, nRead);
			}
		}
		if (!exitRequested) {
			line.drain();
		}
		line.close();
	}
	
}
