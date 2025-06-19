package tools;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

public class InstallerPrep {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage: java InstallerPrep <version> (e.g. 1.2.3)");
            return;
        }

        String version = args[0];
        String jarName = "PokemonXhenos-v" + version + ".jar";
        Path distDir = Paths.get("tools/dist");
        Path jarPath = distDir.resolve(jarName);

        // Rename the most recent JAR
        Path latestJar = Files.list(distDir)
                .filter(p -> p.toString().endsWith(".jar"))
                .max((p1, p2) -> Long.compare(p1.toFile().lastModified(), p2.toFile().lastModified()))
                .orElseThrow();

        if (!latestJar.getFileName().toString().equals(jarName)) {
            Files.move(latestJar, jarPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Renamed latest JAR to: " + jarName);
        }

        Path launch4j = Paths.get("tools/launch4j.xml");
        Path inno = Paths.get("tools/setup.iss");

        replaceInFile(launch4j, "<jar>.*?</jar>", "<jar>dist\\\\" + jarName + "</jar>");
        replaceInFile(inno, "Source: \".*?\\.jar\"", "Source: \"dist\\\\" + jarName + "\"");

        System.out.println("Updated launch4j.xml and setup.iss to use: " + jarName);
    }

    private static void replaceInFile(Path file, String regex, String replacement) throws IOException {
        String content = Files.readString(file);
        content = content.replaceAll(regex, replacement);
        Files.writeString(file, content);
    }
}
