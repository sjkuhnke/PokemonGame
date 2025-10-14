package overworld;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
	
	Clip clip;
	URL[] soundURL = new URL[30];
	public int volumeScale;
	FloatControl fc;
	float volume;
	int index;
	
	// CONSTANTS
	public static final int M_MENU_1 = 0;
	public static final int M_MENU_2 = 1;
	public static final int S_MENU_1 = 2;
	public static final int S_MENU_CON = 3;
	public static final int S_MENU_CAN = 4;
	public static final int S_MENU_START = 5;
	public static final int S_TYPE = 6;
	public static final int S_BACKSPACE = 7;
	
	public Sound(int volumeScale) {
		setup("music", "videogame1-1");
		setup("music", "videogame1-2");
		setup("sfx", "menu-1");
		setup("sfx", "menu-con");
		setup("sfx", "menu-can");
		setup("sfx", "menu-start");
		setup("sfx", "type");
		setup("sfx", "backspace");
		
		this.volumeScale = volumeScale;
	}
	
	private void setup(String folder, String file) {
		soundURL[index++] = getClass().getResource("/" + folder + "/" + file + ".wav");
	}

	public void setFile(int i) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
			clip = AudioSystem.getClip();
			clip.open(ais);
			fc = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			checkVolume();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		clip.start();
	}
	
	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop() {
		if (clip != null) clip.stop();
	}
	
	public void checkVolume() {
		switch(volumeScale) {
		case 0: volume = -80f; break;
		case 1: volume = -20f; break;
		case 2: volume = -12f; break;
		case 3: volume = -5f; break;
		case 4: volume = 1f; break;
		case 5: volume = 6f; break;
		}
		fc.setValue(volume);
	}
}
