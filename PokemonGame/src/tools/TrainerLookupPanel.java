package tools;

import javax.swing.*;
import java.awt.*;

import pokemon.JGradientButton;
import pokemon.Move;
import pokemon.Moveslot;
import pokemon.Pokemon;
import pokemon.Trainer;

public class TrainerLookupPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField trainerIndexField;
	private JTextField trainerNameField;
	private JLabel trainerIndexResult;
	private JLabel trainerNameResult;
	private JButton showInfoButton;
	
	private Trainer t;
	
	public TrainerLookupPanel() {
		initialize();
		
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new GridLayout(0, 2));
		
		searchPanel.add(new JLabel("Trainer Index:"));
		searchPanel.add(trainerIndexField);
		
		searchPanel.add(new JLabel("Trainer Name:"));
		searchPanel.add(trainerNameField);
		
		add(searchPanel, BorderLayout.NORTH);
		
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new GridLayout(0, 1));
		resultPanel.add(trainerIndexResult);
		resultPanel.add(trainerNameResult);
		resultPanel.add(showInfoButton);
		
		add(resultPanel, BorderLayout.SOUTH);
		
		trainerIndexField.addActionListener(e -> searchByIndex());
		trainerNameField.addActionListener(e -> searchByName());
		showInfoButton.addActionListener(e -> showTrainerDetails());
	}
	
	private void initialize() {
		setLayout(new BorderLayout());
		
		trainerIndexField = new JTextField(10);
		trainerNameField = new JTextField(10);
		trainerIndexResult = new JLabel("Trainer Index: N/A");
		trainerNameResult = new JLabel("Trainer Name: N/A");
		showInfoButton = new JButton("Trainer Details");
	}
	
	private void searchByIndex() {
		try {
			int index = Integer.parseInt(trainerIndexField.getText());
			if (index < 0 || index >= Trainer.trainers.length || Trainer.trainers[index] == null) {
				trainerIndexResult.setText("Invalid trainer index.");
				trainerNameResult.setText("Trainer Name: N/A");
				t = null;
			} else {
				Trainer trainer = Trainer.trainers[index];
				trainerIndexResult.setText("Trainer Index: " + index);
				trainerNameResult.setText("Trainer Name: " + trainer.getName());
				t = trainer;
			}
		} catch (NumberFormatException ex) {
			trainerIndexResult.setText("Invalid input. Enter a number.");
			trainerNameResult.setText("Trainer Name: N/A");
			t = null;
		}
	}

	private void searchByName() {
		String trainerName = trainerNameField.getText();
		int trainerIndex = -1;
		for (int i = 0; i < Trainer.trainers.length; i++) {
			if (Trainer.trainers[i] != null && Trainer.trainers[i].getName().equalsIgnoreCase(trainerName)) {
				trainerIndex = i;
				break;
			}
		}
		
		if (trainerIndex == -1) {
			trainerIndexResult.setText("Trainer Index: N/A");
			trainerNameResult.setText("Trainer name not found.");
		} else {
			trainerIndexResult.setText("Trainer Index: " + trainerIndex);
			trainerNameResult.setText("Trainer Name: " + trainerName);
			t = Trainer.trainers[trainerIndex];
		}
	}
	
	private void showTrainerDetails() {
		if (t == null) {
			JOptionPane.showMessageDialog(this, "No trainer selected!");
			return;
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(3, 1));
		
		String moneyInfo = "Money Reward: " + t.getMoney();
		String itemInfo = "Item Reward: " + t.getItem();
		String flagInfo = "Flag Indices: flag[" + t.getFlagX() + "][" + t.getFlagY() + "] (" + t.getFlagIndex() + ")";
		
		infoPanel.add(new JLabel(moneyInfo));
		infoPanel.add(new JLabel(itemInfo));
		infoPanel.add(new JLabel(flagInfo));
		infoPanel.add(new JLabel("________________________________"));
		
		panel.add(infoPanel, BorderLayout.NORTH);
		
		JPanel teamPanel = new JPanel();
		teamPanel.setLayout(new GridLayout(0, 2));
		
		for (Pokemon p : t.team) {
			JPanel pokemonPanel = new JPanel();
			pokemonPanel.setLayout(new BorderLayout());
			
			String pokemonName = p.name + " Lv. " + p.level + " @ " + p.item;
			String abilityName = "Ability: " + p.ability;
			JLabel nameLabel = new JLabel(pokemonName);
			JLabel abilityLabel = new JLabel(abilityName);
			pokemonPanel.add(nameLabel, BorderLayout.NORTH);
			pokemonPanel.add(abilityLabel, BorderLayout.CENTER);
			
			JPanel movesPanel = new JPanel();
			movesPanel.setLayout(new GridLayout(2, 2));
			
			for (Moveslot slot : p.moveset) {
				if (slot == null) {
					movesPanel.add(new JGradientButton("N/A"));
					continue;
				}
				Move m = slot.move;
				JButton moveButton = new JGradientButton(m.toString());
				
				moveButton.setBackground(m.mtype.getColor());
				moveButton.addActionListener(e -> JOptionPane.showMessageDialog(null, m.getMoveSummary(p, null), m.toString() + " Details", JOptionPane.PLAIN_MESSAGE));
				
				movesPanel.add(moveButton);
			}
			
			pokemonPanel.add(movesPanel, BorderLayout.SOUTH);
			teamPanel.add(pokemonPanel);
		}
		
		panel.add(teamPanel, BorderLayout.SOUTH);
		
		JOptionPane.showMessageDialog(this, panel, t.getName() + " Details", JOptionPane.PLAIN_MESSAGE);
	}
}
