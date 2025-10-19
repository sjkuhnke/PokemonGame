package overworld;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

public class Sound {
	
	Clip clip;
	URL[] soundURL = new URL[30];
	public int volumeScale;
	FloatControl fc;
	float volume;
	int index;
	
	// Clip Pool
	private static Map<Integer, List<Clip>> clipPools = new HashMap<>();
	private static final int CLIPS_PER_SOUND = 5;
	
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
			List<Clip> pool = clipPools.computeIfAbsent(i, k -> new ArrayList<>());
			
			Clip availableClip = null;
			for (Clip c : pool) {
				if (!c.isRunning()) {
					availableClip = c;
					break;
				}
			}
			if (availableClip == null && pool.size() < CLIPS_PER_SOUND) {
				AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
				availableClip = AudioSystem.getClip();
				availableClip.open(ais);
				pool.add(availableClip);
				
				availableClip.addLineListener(event -> {
					if (event.getType() == LineEvent.Type.STOP) {
						Clip c = (Clip) event.getLine();
						c.setFramePosition(0);
					}
				});
			}
			
			if (availableClip == null && !pool.isEmpty()) {
				availableClip = pool.get(0);
				availableClip.stop();
				
				availableClip.setFramePosition(0);
			}
			
			clip = availableClip;
			
			if (clip != null) {
				clip.setFramePosition(0);
				fc = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
				checkVolume();
			}
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
		if (clip != null) {
			clip.stop();
			clip.setFramePosition(0);
		}
	}
	
	public static void disposeAll() {
		for (List<Clip> pool : clipPools.values()) {
			for (Clip c : pool) {
				if (c != null && c.isOpen()) {
					c.close();
				}
			}
		}
		clipPools.clear();
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
