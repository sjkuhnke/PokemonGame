package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import overworld.GamePanel;

public class Print {
	
	
	public static void print(String string) {
		if (GamePanel.DEBUG) {
			// print to console
		}
		// add String to the log data structure to be printed later
	}
	
	public static void println(String string) {
		print(string + "\n");
	}
	
	public static void log() {
		// print log to a file
		try {
			Path logPath = SaveManager.getDocsDirectory().getParent().resolve("log.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(logPath.toFile(), true)); // append
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
