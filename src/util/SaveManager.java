package util;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.imageio.ImageIO;

import pokemon.*;

public class SaveManager {
	private static final boolean USE_LOCAL_SAVES = Boolean.parseBoolean(System.getProperty("devMode", "false")); // put -DdevMode=true in Eclipse's VM Arguments in Run Configurations
	
    private static final String GAME_FOLDER = "PokemonXhenos";
    private static final String SAVE_DIR = getSafeSaveDir();

    private static String getSafeSaveDir() {
    	if (USE_LOCAL_SAVES) {
    		Path localPath = Paths.get("./saves");
    		try {
    			Files.createDirectories(localPath);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		return localPath.toString();
    	}
    	
        String os = System.getProperty("os.name").toLowerCase();
        String basePath;

        if (os.contains("win")) {
            basePath = System.getenv("APPDATA");
        } else if (os.contains("mac")) {
            basePath = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            basePath = System.getProperty("user.home") + "/.local/share";
        }

        Path path = Paths.get(basePath, GAME_FOLDER, "saves");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path.toString();
    }

    public static void savePlayer(Player p, String fileName) throws IOException {
        File file = new File(SAVE_DIR + File.separator + fileName);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(p);
        oos.close();
    }

    public static Player loadPlayer(String fileName) {
        File file = new File(SAVE_DIR + File.separator + fileName);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Player) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getSaveFiles() {
        File folder = new File(SAVE_DIR);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) return new ArrayList<>();

        ArrayList<String> names = new ArrayList<>();
        for (File f : files) {
            names.add(f.getName());
        }

        names.sort((f1, f2) -> Long.compare(
            new File(SAVE_DIR, f2).lastModified(),
            new File(SAVE_DIR, f1).lastModified()
        ));

        return names;
    }

    public static boolean deleteSave(String fileName) throws IOException {
        File file = new File(SAVE_DIR + File.separator + fileName);
        return file.exists() && file.delete();
    }

    public static boolean renameSave(String oldName, String newName) throws FileAlreadyExistsException, IOException {
        File oldFile = new File(SAVE_DIR + File.separator + oldName);
        File newFile = new File(SAVE_DIR + File.separator + newName);
        if (newFile.exists()) return false;
        return oldFile.renameTo(newFile);
    }
    
	public static void showInExplorer(String name) {
    	File file = getSaveFile(name);
    	if (file != null && file.exists()) {
    		try {
    			String os = System.getProperty("os.name").toLowerCase();
    			ProcessBuilder pb;
    			
				if (os.contains("win")) {
					pb = new ProcessBuilder("explorer", "/select,", file.getAbsolutePath());
				} else if (os.contains("mac")) {
					pb = new ProcessBuilder("open", "-R", file.getAbsolutePath());
				} else {
					System.err.println("Unsupported OS " + os);
					return;
				}
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	} else {
    		System.err.println("File " + name + " not found.");
    	}
    }
    
    public static File getSaveFile(String name) {
    	return new File(SAVE_DIR + File.separator + name);
    }

    public static Path getSavePath(String name) {
    	return Paths.get(SAVE_DIR, name);
    }
    
    public static Path getDocsDirectory() {
    	String os = System.getProperty("os.name").toLowerCase();
        Path docsPath;

        if (os.contains("win")) {
            docsPath = Paths.get(System.getProperty("user.home"), "Documents", GAME_FOLDER, "docs");
        } else if (os.contains("mac")) {
            docsPath = Paths.get(System.getProperty("user.home"), "Documents", GAME_FOLDER, "docs");
        } else {
            docsPath = Paths.get(System.getProperty("user.home"), "Documents", GAME_FOLDER, "docs");
        }

        try {
            Files.createDirectories(docsPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return docsPath;
    }
    
    public static void saveScreenshot(BufferedImage image) {
    	try {
    		Path screenshotsDir = getDocsDirectory().getParent().resolve("screenshots");
    		Files.createDirectories(screenshotsDir);
    		
    		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    		String fileName = "Screenshot_" + timestamp + ".png";
    		
    		File screenshotFile = new File(screenshotsDir.toFile(), fileName);
    		ImageIO.write(image, "png", screenshotFile);
    		Desktop.getDesktop().open(screenshotsDir.toFile());
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}
