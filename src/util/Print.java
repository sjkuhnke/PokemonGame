package util;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import overworld.GamePanel;

public class Print {

	private static final List<String> buffer = new ArrayList<>();
	private static final List<String> battleBuffer = new ArrayList<>();
	private static boolean inBattle = false;
	private static final Object lock = new Object();

	static {
		// Flush on clean JVM shutdown
		Runtime.getRuntime().addShutdownHook(new Thread(Print::flush));

		// Flush on uncaught exception
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			error("Uncaught exception on thread: " + thread.getName(), throwable);
			flush();
		});
	}

	public static void log(LogLevel level, String msg) {
		String line = format(level, msg);

		// Console only in DEBUG
		if (GamePanel.DEBUG && level == LogLevel.DEBUG) {
			System.out.print(line);
		}

		synchronized (lock) {
			if (inBattle) {
				battleBuffer.add(line);
			} else {
				buffer.add(line);
			}
		}
	}

	public static void debug(String msg) {
		log(LogLevel.DEBUG, msg);
	}

	public static void info(String msg) {
		log(LogLevel.INFO, msg);
	}

	public static void error(String msg) {
		log(LogLevel.ERROR, msg);
	}

	public static void error(String msg, Throwable t) {
		log(LogLevel.ERROR, msg);
		log(LogLevel.ERROR, stackTraceToString(t));
	}
	
	public static void startBattleLog(String name) {
	    synchronized (lock) {
	        inBattle = true;
	        battleBuffer.clear();
	        battleBuffer.add("===== BATTLE START " + name + " =====");
	    }
	}

	public static void endBattleLog(String name) {
	    synchronized (lock) {
	        battleBuffer.add("===== BATTLE END " + name + " =====\n");
	        buffer.addAll(battleBuffer); // merge into main buffer
	        battleBuffer.clear();
	        inBattle = false;
	    }
	}

	public static void flush() {
		synchronized (lock) {
			if (inBattle) endBattleLog("Battle ended early");
			if (buffer.isEmpty()) return;

			try {
				Path logPath = SaveManager
						.getDocsDirectory()
						.getParent()
						.resolve("log.txt");

				try (BufferedWriter writer =
						new BufferedWriter(new FileWriter(logPath.toFile(), true))) {

					for (String line : buffer) {
						writer.write(line);
						writer.newLine();
					}
				}

				buffer.clear();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String format(LogLevel level, String msg) {
		return "[" + LocalDateTime.now() + "][" + level + "] " + msg;
	}

	private static String stackTraceToString(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	public enum LogLevel {
		DEBUG,
		INFO,
		WARN,
		ERROR
	}
}