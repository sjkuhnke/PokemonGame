package tools;

import javax.swing.*;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import pokemon.Pokemon;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PokemonLookupPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JComboBox<Pokemon> pokemonComboBox;
	private JTextField idField;
	private JButton summaryButton;
	private Pokemon p;
	private JLabel selectedPokemon;
	
	public PokemonLookupPanel() {
		initialize();
		
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new GridLayout(0, 2));
		searchPanel.add(new JLabel("ID:"));
		searchPanel.add(idField);
		searchPanel.add(new JLabel("Name:"));
		searchPanel.add(pokemonComboBox);
		
		add(searchPanel, BorderLayout.NORTH);
		
		JPanel resultPanel = new JPanel();
		resultPanel.add(new JLabel("Selected:"));
		resultPanel.add(selectedPokemon);
		resultPanel.add(summaryButton);
		
		add(resultPanel, BorderLayout.SOUTH);
		
		summaryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (p != null) {
					JOptionPane.showMessageDialog(null, p.showSummary(false, null), "Pokemon details", JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "No Pokemon selected!");
				}
			}
		});
		
		idField.addActionListener(e -> searchById());
		pokemonComboBox.addActionListener(e -> searchByName());
	}

	private void initialize() {
		setLayout(new BorderLayout());
		
		pokemonComboBox = new JComboBox<>();
		idField = new JTextField(10);
		summaryButton = new JButton("Summary");
		
		selectedPokemon = new JLabel("N/A");
		
		for (int k = 1; k <= Pokemon.MAX_POKEMON; k++) {
			pokemonComboBox.addItem(new Pokemon(k, 50, true, false));
		}
		AutoCompleteDecorator.decorate(pokemonComboBox);
	}
	
	private void searchById() {
		try {
			int id = Integer.parseInt(idField.getText());
			if (id < 1 || id > Pokemon.MAX_POKEMON) {
				showErrorMessage();
				return;
			}
			p = new Pokemon(id, 50, true, false);
			displaySelected();
		} catch (NumberFormatException ex) {
			showErrorMessage();
		}
		
	}

	private void searchByName() {
		p = (Pokemon) pokemonComboBox.getSelectedItem();
		displaySelected();
		
	}
	
	private void displaySelected() {
		selectedPokemon.setText("[" + formatID() + "] " + p.name + " " + Pokemon.getFormattedDexNo(p.getDexNo()));
		
	}
	
	private void showErrorMessage() {
		JOptionPane.showMessageDialog(this, "Invalid ID. Please enter a valid number.");
	}
	
	private String formatID() {
		String id = p.id + "";
		while (id.length() < 3) {
			id = "0" + id;
		}
		return id;
	}

}
