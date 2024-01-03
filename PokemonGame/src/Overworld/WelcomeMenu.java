package Overworld;

import javax.swing.*;

import Entity.PlayerCharacter;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import Swing.Player;
import Swing.Pokemon;
import Swing.Battle.JGradientButton;

public class WelcomeMenu extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel titleLabel;
	private JLabel subtitleLabel;
	private JPanel optionsPanel;

	private Image img;
	
	public WelcomeMenu(JFrame window, GamePanel gp) {
		this.setPreferredSize(new Dimension(gp.screenWidth, gp.screenHeight));
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		try {
			Random rand = new Random();
			int random = rand.nextInt(3) + 1;
            img = ImageIO.read(getClass().getResourceAsStream("/gen/background" + random + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		titleLabel = new JLabel("Shae’s Pokemon Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        
        subtitleLabel = new JLabel("Welcome to Shae’s Pokemon Game!", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleLabel.setForeground(Color.BLACK);
        
        JPanel checkBoxPanel = new JPanel(new GridLayout(0, 1));
        checkBoxPanel.setOpaque(false);
        
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        iconPanel.setOpaque(false);
        JLabel[] icons = new JLabel[6];
        for (int i = 0; i < 6; i++) {
        	icons[i] = new JLabel();
        	iconPanel.add(icons[i]);
        }
        
        JLabel location = new JLabel("N/A");
        location.setFont(new Font(location.getFont().getName(), Font.BOLD, 16));
        JPanel locationPanel = new JPanel(new GridLayout(0, 1));
        locationPanel.setOpaque(false);
        locationPanel.add(location);
        
        JLabel space = new JLabel("<html><br><br><br><br><br><br><br><br><br><br><br><br><br></html>");

        JLabel generateDoc = new JLabel("Generate Documentation?");
        generateDoc.setFont(new Font("Arial", Font.BOLD, 16));
        generateDoc.setForeground(Color.BLACK);

        JCheckBox trainersCheckBox = new JCheckBox("Trainers");
        trainersCheckBox.setOpaque(false);
        JCheckBox pokemonCheckBox = new JCheckBox("Pokemon");
        pokemonCheckBox.setOpaque(false);
        JCheckBox encountersCheckBox = new JCheckBox("Encounters");
        encountersCheckBox.setOpaque(false);
        JCheckBox movesCheckBox = new JCheckBox("Moves");
        movesCheckBox.setOpaque(false);
        JCheckBox defensiveTypesBox = new JCheckBox("Defensive Types");
        defensiveTypesBox.setOpaque(false);
        JCheckBox offensiveTypesBox = new JCheckBox("Offensive Types");
        offensiveTypesBox.setOpaque(false);
        
        checkBoxPanel.add(generateDoc);
        checkBoxPanel.add(trainersCheckBox);
        checkBoxPanel.add(pokemonCheckBox);
        checkBoxPanel.add(encountersCheckBox);
        checkBoxPanel.add(movesCheckBox);
        checkBoxPanel.add(defensiveTypesBox);
        checkBoxPanel.add(offensiveTypesBox);
        
        optionsPanel = new JPanel(new GridLayout(2, 2));
        
        JGradientButton continueButton = new JGradientButton("Continue");
        continueButton.setBackground(new Color(255, 215, 0).darker());
        optionsPanel.add(continueButton);
        
        JGradientButton newGameButton = new JGradientButton("New Game");
        newGameButton.setBackground(Color.green.darker());
        optionsPanel.add(newGameButton);
        
        ArrayList<String> files = getDatFiles();
        JComboBox<String> fileName = new JComboBox<>(files.toArray(new String[0]));
        optionsPanel.add(fileName);
        
        fileName.addActionListener(e -> {
        	String name = (String) fileName.getSelectedItem();
        	
        	if (name != null) {
        		try {
        			// Check if the directory exists, create it if not
                    Path savesDirectory = Paths.get("./saves/");
                    if (!Files.exists(savesDirectory)) {
                        try {
        					Files.createDirectories(savesDirectory);
        				} catch (IOException f) {
        					f.printStackTrace();
        				}
                    }
                    
        			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./saves/" + name));
        	        Player current = (Player) ois.readObject();
        	        for (int i = 0; i < 6; i++) {
        	        	Pokemon p = current.team[i];
        	        	icons[i].setIcon(getMiniSprite(p));
        	        }
        	        PMap.getLoc(current.currentMap, (int) Math.round(current.getPosX() * 1.0 / 48), (int) Math.round(current.getPosY() * 1.0 / 48));
        	        location.setText("     " + PlayerCharacter.currentMapName);
        	        repaint();
        	        ois.close();
        	    } catch (IOException | ClassNotFoundException | ClassCastException f) {
        	    	f.printStackTrace();
        	    }
        	}
        });
        
        if (fileName.getItemCount() != 0) fileName.setSelectedIndex(0);
        
        JGradientButton manageButton = new JGradientButton("Manage");
        optionsPanel.add(manageButton);
        
        continueButton.addActionListener(e -> {
        	String save = (String) fileName.getSelectedItem();
        	if (save == null) {
        		JOptionPane.showMessageDialog(this, "No save to load from!");
        		return;
        	}
        	
        	boolean[] selectedOptions = {
                    trainersCheckBox.isSelected(),
                    pokemonCheckBox.isSelected(),
                    encountersCheckBox.isSelected(),
                    movesCheckBox.isSelected(),
                    defensiveTypesBox.isSelected(),
                    offensiveTypesBox.isSelected(),
            };
        	
        	gp.player.currentSave = save;
        	Main.loadSave(window, gp, save, this, selectedOptions);
        });
        
        newGameButton.addActionListener(e -> {
        	String save = null;
        	
        	do {
        		save = JOptionPane.showInputDialog(this, "Enter a new save file name (A-Z, 0-9, _, and - are permitted, <= 20 characters):");
        		
        		if (save == null) {
                    return;
                }
        		
        		if (!isValidFileName(save)) {
        			JOptionPane.showMessageDialog(this, "Invalid file name. Please use only alphanumeric characters, underscores, and hyphens, and ensure the length is no more than 20 characters.");
        		}
        		if (files.contains(save + ".dat")) {
        			JOptionPane.showMessageDialog(this, "Save file already exists! Select the save file and press 'Continue'");
        			save = "$";
        		}
        	} while (!isValidFileName(save));
        	
        	boolean[] selectedOptions = {
                    trainersCheckBox.isSelected(),
                    pokemonCheckBox.isSelected(),
                    encountersCheckBox.isSelected(),
                    movesCheckBox.isSelected()
            };
        	
        	save += ".dat";
        	gp.player.currentSave = save;
        	
        	Main.loadSave(window, gp, save, this, selectedOptions);
        });
        
        manageButton.addActionListener(e -> {
            String selectedFile = (String) fileName.getSelectedItem();
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(this, "No file selected for management!");
                return;
            }

            manageFile(selectedFile, fileName);
        });
        
        add(titleLabel);
        add(subtitleLabel);
        add(locationPanel);
        add(iconPanel);
        add(space);
        add(checkBoxPanel);
        add(optionsPanel);
        
	}
	
	private ArrayList<String> getDatFiles() {
		ArrayList<String> fileNames = new ArrayList<>();

		// Check if the directory exists, create it if not
        Path savesDirectory = Paths.get("./saves/");
        if (!Files.exists(savesDirectory)) {
            try {
				Files.createDirectories(savesDirectory);
			} catch (IOException f) {
				f.printStackTrace();
			}
        }

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("./saves/"), "*.dat")) {
            for (Path path : directoryStream) {
                fileNames.add(path.getFileName().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNames;
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
	
	private boolean isValidFileName(String fileName) {
	    return fileName.matches("[a-zA-Z0-9_\\-]+") && fileName.length() <= 20 && !fileName.trim().isEmpty();
	}
	
	private void manageFile(String name, JComboBox<String> fileNames) {
        String[] options = {"Rename", "Delete", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
                "Select an action for " + name + ":",
                "File Management",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[2]);

        switch (choice) {
            case 0: // Rename
            	String newFileName = null;
            	do {
            		newFileName = JOptionPane.showInputDialog(this, "Enter the new file name:");
            		
            		if (newFileName == null) {
                        return;
                    }
            		
            		if (!isValidFileName(newFileName)) {
            			JOptionPane.showMessageDialog(this, "Invalid file name. Please use only alphanumeric characters, underscores, and hyphens, and ensure the length is no more than 20 characters.");
            		}
            		
            	} while (!isValidFileName(newFileName));
            	try {
                    Path oldPath = Paths.get("./saves/" + name);
                    Path newPath = oldPath.resolveSibling(newFileName + ".dat");
                    Files.move(oldPath, newPath);
                    updateFileList(fileNames);
                } catch (FileAlreadyExistsException e) {
                    JOptionPane.showMessageDialog(this, "File with the specified name already exists. Please choose a different name.");
                } catch (IOException ex) {
                    ex.printStackTrace(); // Handle other IOExceptions as needed
                }
            	break;
            case 1: // Delete
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete " + name + "?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Files.delete(Paths.get("./saves/" + name));
                        updateFileList(fileNames);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            case 2: // Cancel
                break;
        }
    }

    private void updateFileList(JComboBox<String> fileName) {
        ArrayList<String> files = getDatFiles();
        fileName.setModel(new DefaultComboBoxModel<>(files.toArray(new String[0])));
    }
    
    private Icon getMiniSprite(Pokemon p) {
    	ImageIcon originalSprite = null;
    	if (p == null) {
    		try {
				originalSprite = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/minisprites/_001.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}
    	} else {
    		originalSprite = new ImageIcon(p.getMiniSprite());
    	}
    	
		Image image = originalSprite.getImage();
		
		ImageIcon imageIcon = new ImageIcon(image);
		
		return imageIcon;
	}

}
