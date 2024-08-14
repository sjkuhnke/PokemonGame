package tools;

import javax.swing.*;

import overworld.GamePanel;
import pokemon.Pokemon;
import tile.TileManager;

public class Driver {
	
	static GamePanel gp;

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
		loadPokemon();
		new TileManager(gp);
	}
	
	public static void loadPokemon() {
		Pokemon.readInfoFromCSV();
		Pokemon.readMovebanksFromCSV();
		Pokemon.readTrainersFromCSV();
		Pokemon.readEntiresFromCSV();
	}

}
