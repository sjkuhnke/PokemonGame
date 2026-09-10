package util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import pokemon.Moveslot;
import pokemon.Pokemon;

public class Exporter {
	public static String exportTeam(Pokemon[] team) {
		StringBuilder result = new StringBuilder();
		boolean firstPokemon = true;
		
		for (Pokemon p : team) {
			if (p == null) continue;
			
			if (!firstPokemon) {
				result.append("\n");
			}
			
			result.append(exportPokemon(p));
			firstPokemon = false;
		}
		return result.toString().trim();
	}
	
	private static String exportPokemon(Pokemon p) {
		StringBuilder result = new StringBuilder();
		
		// Name / Nickname / Item
		String speciesName = p.name();
		
		boolean hasNickname = p.nickname != null && !p.nickname.trim().isEmpty() && !p.nickname.equals(speciesName);
		result.append(speciesName);
		
		if (hasNickname) {
			result.append(" (");
			result.append(p.nickname);
			result.append(")");
		}
		
		if (p.item != null) {
			result.append(" @ ");
			result.append(p.item.toString());
		}
		
		result.append("\n");
		
		// Ability
		if (p.ability != null) {
			result.append("Ability: ");
			result.append(p.ability.toString());
			result.append("\n");
		}
		
		// Shiny
		if (p.shiny) {
			result.append("Shiny: Yes\n");
		}
		
		// Level
		result.append("Level: ");
		result.append(p.level);
		result.append("\n");
		
		// Pokeball
		if (p.ball != null) {
			result.append("Ball: ");
			result.append(p.ball.toString());
			result.append("\n");
		}
		
		// Nature
		if (p.nat != null) {
			result.append(p.nat.toString());
			result.append(" Nature\n");
		}
		
		// IVs
		if (p.ivs != null && p.ivs.length >= 6) {
			result.append("IVs: ");
			
			result.append(p.ivs[0]).append(" HP / ");
			result.append(p.ivs[1]).append(" Atk / ");
			result.append(p.ivs[2]).append(" Def / ");
			result.append(p.ivs[3]).append(" SpA / ");
			result.append(p.ivs[4]).append(" SpD / ");
			result.append(p.ivs[5]).append(" Spe");
			
			result.append("\n");
		}
		
		// Moves + Current PP
		if (p.moveset != null) {
			for (Moveslot slot : p.moveset) {
				if (slot == null || slot.move == null) continue;
				
				result.append("- ");
				result.append(slot.move.toString());
				
				result.append("(");
				result.append(slot.currentPP);
				result.append(")");
				
				result.append("\n");
			}
		}
		
		return result.toString();
	}
	
	public static void showExportDialog(Pokemon[] team) {
		String exportedText = exportTeam(team);
		if (exportedText.isEmpty()) return;
		
		JDialog dialog = new JDialog();
		
		dialog.setTitle("Export Team");
		dialog.setModal(false);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		dialog.setLayout(new BorderLayout(10, 10));
		
		JTextArea textArea = new JTextArea(exportedText);
		
		textArea.setEditable(false);
		textArea.setLineWrap(false);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(550, 500));
		
		JButton copyButton = new JButton("Copy Team");
		copyButton.addActionListener(e -> {
			StringSelection selection = new StringSelection(textArea.getText());
			
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
			copyButton.setText("Copied!");
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(copyButton);
		
		dialog.add(scrollPane, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		dialog.pack();
		dialog.setLocationRelativeTo(Pokemon.gp);
		dialog.setVisible(true);
	}
}
