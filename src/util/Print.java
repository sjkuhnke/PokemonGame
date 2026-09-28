package util;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import overworld.GamePanel;

public class Print {

	private static final List<String> buffer = new ArrayList<>();
	private static final List<String> battleBuffer = new ArrayList<>();
	private static boolean inBattle = false;
	
	// The save file whose logs are currently receiving output.
	private static String activeSaveFile = null;
	private static Path activeLogPath = null;
	
	// Self-play / headless runs: drop DEBUG lines entirely (no buffering, no timestamps).
	private static volatile boolean debugSuppressed = false;
	
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

	public static void setDebugSuppressed(boolean suppressed) {
		debugSuppressed = suppressed;
	}

	public static boolean isDebugSuppressed() {
		return debugSuppressed;
	}

	/**
	 * Sets the save file that future logs should be written to.
	 *
	 * If another save is currently active, its log is closed first.
	 */
	public static void setSaveFile(String fileName) {
		synchronized (lock) {
			
			// Don't restart the logging session if we're already logging this save.
			if (fileName != null && fileName.equals(activeSaveFile)) {
				return;
			}

			// Close the previous save's logging session.
			if (activeSaveFile != null) {
				endSaveInternal();
			}

			if (fileName == null || fileName.trim().isEmpty()) {
				activeSaveFile = null;
				activeLogPath = null;
				return;
			}

			activeSaveFile = fileName;
			
			try {
				Path saveLogsDirectory = SaveManager.getSaveLogsDirectory(fileName);
				activeLogPath = saveLogsDirectory.resolve("log.txt");
				
				buffer.add("===== SESSION START " + LocalDateTime.now() + " =====");
				
			} catch (Exception e) {
				activeLogPath = null;
				error("Could not initialize log for save: " + fileName, e);
			}
		}
	}

	/**
	 * Ends logging for the currently active save.
	 *
	 * This should be called when returning to the title screen/main menu.
	 */
	public static void endSave() {
		synchronized (lock) {
			endSaveInternal();
		}
	}

	/**
	 * Internal version of endSave().
	 *
	 * Assumes the lock is already held.
	 */
	private static void endSaveInternal() {
		if (activeSaveFile == null) {
			return;
		}

		if (inBattle) {
			endBattleLogInternal("Battle ended early");
		}

		buffer.add("===== SESSION END " + LocalDateTime.now() + " =====");
		
		flushInternal();

		activeSaveFile = null;
		activeLogPath = null;
	}

	public static void log(LogLevel level, String msg) {
		if (level == LogLevel.DEBUG && debugSuppressed) return;

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
			endBattleLogInternal(name);
		}
	}

	/**
	 * Internal version of endBattleLog().
	 *
	 * Assumes the lock is already held.
	 */
	private static void endBattleLogInternal(String name) {
		if (!inBattle) {
			return;
		}

		battleBuffer.add("===== BATTLE END " + name + " =====\n");
		buffer.addAll(battleBuffer);
		battleBuffer.clear();
		inBattle = false;
	}

	public static void flush() {
		synchronized (lock) {
			flushInternal();
		}
	}

	/**
	 * Internal version of flush().
	 *
	 * Assumes the lock is already held.
	 */
	private static void flushInternal() {
		if (inBattle) {
			endBattleLogInternal("Battle ended early");
		}

		if (buffer.isEmpty()) return;

		try {
			Path logPath = getLogPath();

			if (logPath == null) {
				return;
			}

			Path parent = logPath.getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}

			try (BufferedWriter writer = Files.newBufferedWriter(
					logPath,
					StandardOpenOption.CREATE,
					StandardOpenOption.APPEND)) {

				for (String line : buffer) {
					writer.write(line);
					writer.newLine();
				}
			}

			buffer.clear();

		} catch (IOException e) {
			// Do NOT clear the buffer if writing failed.
			e.printStackTrace();
		}
	}

	/**
	 * Gets the file that should currently receive log output.
	 *
	 * Logs associated with a save go into that save's log folder.
	 * Logs generated before a save is loaded go into an unsaved log.
	 */
	private static Path getLogPath() {
		if (activeLogPath != null) {
			return activeLogPath;
		}

		try {
			return SaveManager.getLogsDirectory().resolve("unsaved").resolve("log.txt");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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