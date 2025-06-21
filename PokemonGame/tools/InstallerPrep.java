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
        Path plist = Paths.get("tools/mac_template/Info.plist");

        replaceInFile(launch4j, "<jar>.*?</jar>", "<jar>dist\\\\" + jarName + "</jar>");
        replaceInFile(inno, "Source: \".*?\\.jar\"", "Source: \"dist\\\\" + jarName + "\"");
        replaceInFile(plist, "<string>.*?</string>", "<string>" + version + "</string>", 1); // Only replace the *first* <string>

        Path launcherScript = Paths.get("tools/mac_template/PokemonXhenos");
        String script = "#!/bin/bash\n" +
                        "DIR=\"$( cd \"$( dirname \"$0\" )\" && pwd )\"\n" +
                        "java -jar \"$DIR/../Resources/" + jarName + "\"\n";
        Files.writeString(launcherScript, script);

        launcherScript.toFile().setExecutable(true);

        System.out.println("Updated launch4j.xml, setup.iss, mac_template/Info.plist, and mac_template/PokemonXhenos to use: " + jarName);
    }

    private static void replaceInFile(Path file, String regex, String replacement) throws IOException {
        replaceInFile(file, regex, replacement, -1); // Unlimited
    }

    private static void replaceInFile(Path file, String regex, String replacement, int limit) throws IOException {
        String content = Files.readString(file);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        int count = 0;
        while (matcher.find()) {
            if (limit != -1 && count++ >= limit) break;
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        Files.writeString(file, sb.toString());
    }
}
