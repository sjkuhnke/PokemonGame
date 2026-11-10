package docs;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import pokemon.Ability;

public class AbilitiesDoc {

	// ----------------------------------------------------------------------
	// Moved from Main.java (formerly writeAbilities); output unchanged.
	// ----------------------------------------------------------------------
	public static void writeAbilitiesToTxt(Path dir) {
		try {
			Path outPath = dir.resolve("AbilitiesInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile());
			writer.write("--------------------------------------\n");
			writer.write("Abilities Info:\n");
			writer.write("--------------------------------------\n");
			Ability[] allAbilities = Ability.values();
			for (Ability a : allAbilities) {
				String ability = a.toString();
				while (ability.length() < 18) {
					ability += " ";
				}
				String desc = a.desc;
				writer.write(String.format("%s | %s\n", ability, desc));
			}

			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}