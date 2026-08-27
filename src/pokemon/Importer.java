package pokemon;

import java.util.ArrayList;
import java.util.List;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;

public class Importer {

    public static List<Pokemon> importPokemon(String text) throws IllegalArgumentException {

        List<Pokemon> pokemon = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("No Pokémon were provided.");
        }

        // Split entries by one or more blank lines
        String[] entries = text.trim().split("\\R\\s*\\R");

        for (String entry : entries) {
            Pokemon p = parsePokemon(entry.trim());

            if (p != null) {
                pokemon.add(p);
            }
        }

        return pokemon;
    }

    private static Pokemon parsePokemon(String entry) {

        String[] lines = entry.split("\\R");

        if (lines.length < 4) {
            throw new IllegalArgumentException("Invalid Pokémon entry:\n" + entry);
        }

        String firstLine = lines[0].trim();

        String[] itemParts = firstLine.split(" @ ", 2);

        String namePart = itemParts[0].trim();
        String itemName = itemParts.length > 1 ? itemParts[1].trim() : null;

        String pokemonName = namePart;
        String nickname = null;

        // Extract nickname from parentheses
        int openParen = namePart.lastIndexOf("(");
        int closeParen = namePart.lastIndexOf(")");

        if (openParen >= 0 && closeParen > openParen) {
            pokemonName = namePart.substring(0, openParen).trim();
            nickname = namePart.substring(openParen + 1, closeParen).trim();
        }

        Integer id = Pokemon.getIDFromName(pokemonName);

        if (id == null) {
            throw new IllegalArgumentException(
                "Could not find Pokémon: " + pokemonName
            );
        }

        Item item = null;

        if (itemName != null && !itemName.isEmpty()) {
            item = Item.getEnum(itemName);

            if (item == null) {
                throw new IllegalArgumentException(
                    "Could not find item \"" + itemName
                    + "\" for " + pokemonName
                );
            }
        }
        int level = 100;
        Item ball = Item.POKEBALL;
        boolean shiny = false;

        Ability ability = null;
        Nature nature = null;
        int[] ivs = new int[] {31, 31, 31, 31, 31, 31};

        ArrayList<Move> moves = new ArrayList<>();
        ArrayList<Integer> movePP = new ArrayList<>();

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("Ability:")) {
                String abilityName = line.substring("Ability:".length()).trim();
                ability = Ability.getEnum(abilityName);

                if (ability == null) {
                    throw new IllegalArgumentException(
                        "Could not find ability \"" + abilityName
                        + "\" for " + pokemonName
                    );
                }
            } else if (line.equalsIgnoreCase("Shiny: Yes")) {
                shiny = true;
            } else if (line.startsWith("Level:")) {
                try {
                    level = Integer.parseInt(
                        line.substring("Level:".length()).trim()
                    );
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        "Invalid level for " + pokemonName
                    );
                }

                if (level < 1 || level > 100) {
                    throw new IllegalArgumentException(
                        "Level must be between 1 and 100 for " + pokemonName
                    );
                }
            } else if (line.startsWith("Ball:")) {
                String ballName = line.substring("Ball:".length()).trim();
                ball = Item.getEnum(ballName);

                if (ball == null) {
                    throw new IllegalArgumentException(
                        "Could not find Poké Ball \"" + ballName
                        + "\" for " + pokemonName
                    );
                }
            } else if (line.endsWith(" Nature")) {
                String natureName = line.substring(
                    0,
                    line.length() - " Nature".length()
                ).trim();

                nature = Nature.getEnum(natureName);

                if (nature == null) {
                    throw new IllegalArgumentException(
                        "Could not find nature \"" + natureName
                        + "\" for " + pokemonName
                    );
                }
            } else if (line.startsWith("IVs:")) {
                ivs = parseIVs(
                    line.substring("IVs:".length()).trim()
                );
            } else if (line.startsWith("- ")) {
                ParsedMove parsedMove = parseMove(line.substring(2).trim());

                Move move = Move.getEnum(parsedMove.name);

                if (move == null) {
                    throw new IllegalArgumentException(
                        "Could not find move \"" + parsedMove.name
                        + "\" for " + pokemonName
                    );
                }

                moves.add(move);
                movePP.add(parsedMove.pp);
            }
        }

        if (ability == null) {
            throw new IllegalArgumentException(
                pokemonName + " does not have an ability."
            );
        }
        
        if (nature == null) {
            throw new IllegalArgumentException(
                pokemonName + " does not have a nature."
            );
        }
        
        if (moves.size() != 4) {
            throw new IllegalArgumentException(
                pokemonName + " must have exactly 4 moves. Found: "
                + moves.size()
            );
        }
        
        // Build a Set using your existing system
        Set set = new Set(id);
        
        set.setItems(item);
        set.setAbility(ability);
        set.setNatures(nature);
        set.setIVs(ivs);
        
        for (int i = 0; i < 4; i++) {
            set.setMoves(i, moves.get(i));
        }
        
        Pokemon p = set.makePokemon(level);
        
        p.shiny = shiny;
        p.ball = ball;
        
        // Restore nickname
        if (nickname != null && !nickname.isEmpty()) {
            p.nickname = nickname;
        }
        
        // Restore imported current PP
        for (int i = 0; i < 4; i++) {
            if (movePP.get(i) != null) {
                p.moveset[i].currentPP = Math.min(
                    movePP.get(i),
                    p.moveset[i].maxPP
                );
                p.moveset[i].maxPP = p.moveset[i].currentPP;
            }
        }
        
        return p;
    }

    private static class ParsedMove {

        String name;
        Integer pp;

        ParsedMove(String name, Integer pp) {
            this.name = name;
            this.pp = pp;
        }
    }

    private static ParsedMove parseMove(String text) {

        // Supports both:
        // Leaf Storm
        // Leaf Storm (8)

        Integer pp = null;
        String moveName = text;

        int openParen = text.lastIndexOf("(");
        int closeParen = text.lastIndexOf(")");

        if (openParen >= 0 && closeParen > openParen) {

            String possiblePP = text.substring(
                openParen + 1,
                closeParen
            ).trim();

            try {
                pp = Integer.parseInt(possiblePP);

                moveName = text.substring(0, openParen).trim();

            } catch (NumberFormatException e) {
                // It's not a PP value, so treat the entire line as the move name
            }
        }

        return new ParsedMove(moveName, pp);
    }

    private static int[] parseIVs(String text) {

        // HP, Atk, Def, SpA, SpD, Spe
        int[] ivs = new int[] {31, 31, 31, 31, 31, 31};

        String[] parts = text.split("/");

        for (String part : parts) {

            part = part.trim();

            // Example: "30 HP"
            String[] pieces = part.split("\\s+", 2);

            if (pieces.length != 2) {
                throw new IllegalArgumentException(
                    "Invalid IV: " + part
                );
            }

            int value;

            try {
                value = Integer.parseInt(pieces[0]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Invalid IV value: " + pieces[0]
                );
            }

            if (value < 0 || value > 31) {
                throw new IllegalArgumentException(
                    "IV must be between 0 and 31: " + value
                );
            }

            String stat = pieces[1].trim();

            switch (stat) {
                case "HP":
                    ivs[0] = value;
                    break;
                case "Atk":
                    ivs[1] = value;
                    break;
                case "Def":
                    ivs[2] = value;
                    break;
                case "SpA":
                    ivs[3] = value;
                    break;
                case "SpD":
                    ivs[4] = value;
                    break;
                case "Spe":
                    ivs[5] = value;
                    break;
                default:
                    throw new IllegalArgumentException(
                        "Unknown IV stat: " + stat
                    );
            }
        }

        return ivs;
    }
    
    public static List<Pokemon> showImportDialog() {

        JTextArea textArea = new JTextArea(25, 60);
        textArea.setLineWrap(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 500));

        int result = JOptionPane.showConfirmDialog(
            null,
            scrollPane,
            "Import Pokémon",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        try {
            return importPokemon(textArea.getText());

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                null,
                e.getMessage(),
                "Import Error",
                JOptionPane.ERROR_MESSAGE
            );

            return null;
        }
    }
}