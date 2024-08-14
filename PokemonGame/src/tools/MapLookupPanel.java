package tools;

import javax.swing.*;
import java.awt.*;

import tile.TileManager;

public class MapLookupPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField mapNumberField;
	private JTextField mapNameField;
	private JLabel mapNumberResult;
	private JLabel mapNameResult;
	
	public MapLookupPanel() {
		initialize();
		
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new GridLayout(0, 2));
		
		searchPanel.add(new JLabel("Map Number:"));
		searchPanel.add(mapNumberField);
		
		searchPanel.add(new JLabel("Map Name:"));
		searchPanel.add(mapNameField);
		
		add(searchPanel, BorderLayout.NORTH);
		
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new GridLayout(0, 1));
		resultPanel.add(mapNumberResult);
		resultPanel.add(mapNameResult);
		
		add(resultPanel, BorderLayout.SOUTH);
		
		mapNumberField.addActionListener(e -> searchByNumber());
		
		mapNameField.addActionListener(e -> searchByName());
	}
	
	private void initialize() {
		setLayout(new BorderLayout());
		
		mapNumberField = new JTextField(10);
		mapNameField = new JTextField(10);
		mapNumberResult = new JLabel("Map Name: N/A");
		mapNameResult = new JLabel("Map Number: N/A");
	}
	
	private void searchByNumber() {
		try {
			int mapNumber = Integer.parseInt(mapNumberField.getText());
			if (mapNumber < 0 || mapNumber >= TileManager.mapNames.length || TileManager.mapNames[mapNumber] == null) {
				mapNumberResult.setText("Invalid map number.");
				mapNameResult.setText("Map Number: N/A");
			} else {
				mapNumberResult.setText("Map Name: " + TileManager.mapNames[mapNumber]);
				mapNameResult.setText("Map Number: " + mapNumber);
			}
		} catch (NumberFormatException ex) {
			mapNumberResult.setText("Invalid input. Enter a number.");
			mapNameResult.setText("Map Number: N/A");
		}
	}
	
	private void searchByName() {
		String mapName = mapNameField.getText();
		int mapIndex = -1;
		for (int i = 0; i < TileManager.mapNames.length; i++) {
			if (TileManager.mapNames[i] == null) break;
			if (TileManager.mapNames[i].equalsIgnoreCase(mapName)) {
				mapIndex = i;
				break;
			}
		}
		
		if (mapIndex == -1) {
			mapNumberResult.setText("Map Name: N/A");
			mapNameResult.setText("Map name not found.");
		} else {
			mapNumberResult.setText("Map Name: " + mapName);
			mapNameResult.setText("Map Number: " + mapIndex);
		}
	}
}
