package tools;

import javax.swing.*;

import overworld.GamePanel;
import pokemon.Field;
import pokemon.Pokemon;
import tile.TileManager;

public class GameTools {
	
	static GamePanel gp;
	static DefaultComboBoxModel<Pokemon> allPokemon;

	public static void main(String[] args) {
		gp = new GamePanel(null);
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Pokemon Tools");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800, 600);
			
			load();
			
			JTabbedPane tabbedPane = new JTabbedPane();
			
			tabbedPane.addTab("Pokemon", new PokemonLookupPanel());
			tabbedPane.addTab("Int Pack", new IntegerPackingPanel());
			tabbedPane.addTab("Map", new MapLookupPanel());
			tabbedPane.addTab("Trainers", new TrainerLookupPanel());
			
			frame.add(tabbedPane);
			frame.pack();
			
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});

	}
	
	public static void load() {
		Pokemon.field = new Field();
		Pokemon.readInfoFromCSV();
		Pokemon.readMovebanksFromCSV();
		Pokemon.readTMsFromCSV();
		Pokemon.setEvolutionMaps();
		Pokemon.readTrainersFromCSV();
		new TileManager(gp);
		setupAllPokemon();
	}
	
	public static void setupAllPokemon() {
		Pokemon[] p = new Pokemon[Pokemon.MAX_POKEMON];
		for (int k = 1; k <= Pokemon.MAX_POKEMON; k++) {
			p[k - 1] = new Pokemon(k, 50, true, false);
		}
		allPokemon = new DefaultComboBoxModel<>(p);
	}

}
