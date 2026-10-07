package overworld;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public class Sound {

	URL[] soundURL = new URL[30];
	public int volumeScale;
	float volume;
	int index;
	private Voice current;

	// CONSTANTS
	public static final int M_MENU_1 = 0;
	public static final int M_MENU_2 = 1;
	public static final int S_MENU_1 = 2;
	public static final int S_MENU_CON = 3;
	public static final int S_MENU_CAN = 4;
	public static final int S_MENU_START = 5;
	public static final int S_TYPE = 6;
	public static final int S_BACKSPACE = 7;

	// ---- Shared software mixer ----
	private static final AudioFormat FMT = new AudioFormat(44100f, 16, 2, true, false);
	private static final byte[][] pcm = new byte[30][];
	private static final List<Voice> voices = new CopyOnWriteArrayList<>();
	private static final int MAX_VOICES = 32;
	private static SourceDataLine line;
	private static volatile boolean running;
	private static boolean failed;

	private static final class Voice {
		final byte[] data;
		int pos;
		volatile boolean loop;
		volatile boolean done;
		volatile float gain = 1f;
		Voice(byte[] data) { this.data = data; }
	}

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

	// Optional: java -Dsound.mixer=PCH -jar Game.jar to prefer a mixer by name
	private static int mixerRank(Mixer.Info mi) {
		String n = mi.getName().toLowerCase();
		String pref = System.getProperty("sound.mixer");
		if (pref != null && n.contains(pref.toLowerCase())) return -1;
		if (n.contains("pulse") || n.contains("pipewire") || n.startsWith("default")) return 0;
		if (n.contains("plughw")) return 1;
		if (n.contains("hw:")) return 3;
		return 2;
	}

	private static synchronized void startMixer() {
		if (running || failed) return;

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, FMT);
		List<Mixer.Info> infos = new ArrayList<>(Arrays.asList(AudioSystem.getMixerInfo()));
		infos.removeIf(mi -> mi.getName().startsWith("Port"));
		infos.sort(Comparator.comparingInt(Sound::mixerRank));

		System.out.println("Available mixers:");
		for (Mixer.Info mi : infos) System.out.println("  " + mi.getName());

		for (Mixer.Info mi : infos) {
			try {
				Mixer m = AudioSystem.getMixer(mi);
				if (!m.isLineSupported(info)) continue;
				SourceDataLine l = (SourceDataLine) m.getLine(info);
				l.open(FMT, 16384);
				l.start();
				line = l;
				running = true;
				System.out.println("Audio mixer in use: " + mi.getName());
				Thread t = new Thread(Sound::mixLoop, "audio-mixer");
				t.setDaemon(true);
				t.start();
				return;
			} catch (Exception e) {
				// try the next mixer
			}
		}
		failed = true;
		System.err.println("No usable audio output found; sound disabled.");
	}

	private static void mixLoop() {
		final int frames = 1024;
		int[] acc = new int[frames * 2];
		byte[] out = new byte[frames * 4];

		while (running) {
			Arrays.fill(acc, 0);

			for (Voice v : voices) {
				if (v.done) { voices.remove(v); continue; }
				byte[] d = v.data;
				int p = v.pos;
				float g = v.gain;
				for (int s = 0; s < acc.length; s++) {
					if (p + 1 >= d.length) {
						if (v.loop && d.length >= 2) {
							p = 0;
						} else {
							v.done = true;
							break;
						}
					}
					short sample = (short) ((d[p + 1] << 8) | (d[p] & 0xFF));
					acc[s] += (int) (sample * g);
					p += 2;
				}
				v.pos = p;
			}

			for (int s = 0; s < acc.length; s++) {
				int x = Math.max(-32768, Math.min(32767, acc[s]));
				out[2 * s] = (byte) x;
				out[2 * s + 1] = (byte) (x >> 8);
			}
			line.write(out, 0, out.length); // blocks, which paces the loop
		}
	}

	private static synchronized byte[] loadPcm(int i, URL url) throws Exception {
		if (pcm[i] == null) {
			try (AudioInputStream src = AudioSystem.getAudioInputStream(url);
					AudioInputStream conv = AudioSystem.getAudioInputStream(FMT, src)) {
				pcm[i] = conv.readAllBytes();
			}
		}
		return pcm[i];
	}

	// ---- Instance API (unchanged) ----
	public void setFile(int i) {
		current = null;
		try {
			startMixer();
			if (failed) return;
			current = new Voice(loadPcm(i, soundURL[i]));
			checkVolume();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play() {
		Voice v = current;
		if (v == null || voices.size() >= MAX_VOICES) return;
		v.pos = 0;
		v.done = false;
		if (!voices.contains(v)) voices.add(v);
	}

	public void loop() {
		Voice v = current;
		if (v == null) return;
		v.loop = true;
		if (!voices.contains(v)) {
			v.pos = 0;
			v.done = false;
			voices.add(v);
		}
	}

	public void stop() {
		Voice v = current;
		if (v != null) {
			v.done = true;
			voices.remove(v);
		}
	}

	public static void disposeAll() {
		running = false;
		voices.clear();
		if (line != null) {
			line.stop();
			line.close();
		}
	}

	public void checkVolume() {
		switch (volumeScale) {
		case 0: volume = -80f; break;
		case 1: volume = -20f; break;
		case 2: volume = -12f; break;
		case 3: volume = -5f; break;
		case 4: volume = 1f; break;
		case 5: volume = 6f; break;
		}
		if (current != null) {
			current.gain = volume <= -79f ? 0f : (float) Math.pow(10, volume / 20.0);
		}
	}
}