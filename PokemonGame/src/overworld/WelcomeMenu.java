package overworld;

import javax.swing.*;

import entity.PlayerCharacter;
import pokemon.Player;
import pokemon.Pokemon;
import pokemon.JGradientButton;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

public class WelcomeMenu extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int NUM_BACKGROUNDS = 4;
	
	private OutlineLabel titleLabel;
	private OutlineLabel subtitleLabel;
	private JPanel optionsPanel;

	private Image img;
	
	public WelcomeMenu(JFrame window, GamePanel gp) {
	    this.setPreferredSize(new Dimension(gp.screenWidth, gp.screenHeight));
	    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    
	    Color textColor = null;

	    try {
	        Random rand = new Random();
	        int random = rand.nextInt(NUM_BACKGROUNDS) + 1;
	        img = ImageIO.read(getClass().getResourceAsStream("/gen/background" + random + ".png"));
	        Color[] textColors = new Color[]{new Color(221, 184, 188), new Color(247, 229, 123), new Color(196, 197, 83), new Color(0, 77, 129)};
	        textColor = textColors[random - 1];
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    titleLabel = new OutlineLabel(gp.gameTitle, SwingConstants.CENTER);
	    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
	    titleLabel.setForeground(textColor);
	    titleLabel.setOutlineColor(Color.BLACK);

	    subtitleLabel = new OutlineLabel(gp.gameTitle, SwingConstants.CENTER);
	    subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
	    subtitleLabel.setForeground(textColor);
	    subtitleLabel.setOutlineColor(Color.BLACK);

	    JPanel checkBoxPanel = new JPanel(new GridLayout(0, 2));
	    checkBoxPanel.setOpaque(false);
	    checkBoxPanel.setPreferredSize(new Dimension(400, 120));
	    checkBoxPanel.setMaximumSize(new Dimension(400, 120));
	    
	    JPanel checkBoxContainer = new JPanel();
	    checkBoxContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
	    checkBoxContainer.setOpaque(false);
	    checkBoxContainer.add(checkBoxPanel);

	    JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    iconPanel.setOpaque(false);
	    JLabel[] icons = new JLabel[6];
	    for (int i = 0; i < 6; i++) {
	        icons[i] = new JLabel();
	        iconPanel.add(icons[i]);
	    }

	    OutlineLabel location = new OutlineLabel("N/A");
	    location.setFont(new Font(location.getFont().getName(), Font.BOLD, 16));
	    JPanel locationPanel = new JPanel(new GridLayout(0, 1));
	    locationPanel.setOpaque(false);
	    location.setForeground(textColor);
	    location.setOutlineColor(Color.BLACK);
	    locationPanel.add(location);

	    OutlineLabel generateDoc = new OutlineLabel("Generate Documentation?");
	    generateDoc.setFont(new Font("Arial", Font.BOLD, 16));
	    generateDoc.setForeground(textColor);
	    generateDoc.setOutlineColor(Color.BLACK);

	    OutlineCheckBox trainersCheckBox = new OutlineCheckBox("Trainers", textColor);
	    styleCheckBox(trainersCheckBox);
	    OutlineCheckBox pokemonCheckBox = new OutlineCheckBox("Pokemon", textColor);
	    styleCheckBox(pokemonCheckBox);
	    OutlineCheckBox encountersCheckBox = new OutlineCheckBox("Encounters", textColor);
	    styleCheckBox(encountersCheckBox);
	    OutlineCheckBox movesCheckBox = new OutlineCheckBox("Moves", textColor);
	    styleCheckBox(movesCheckBox);
	    OutlineCheckBox defensiveTypesBox = new OutlineCheckBox("Defensive Types", textColor);
	    styleCheckBox(defensiveTypesBox);
	    OutlineCheckBox offensiveTypesBox = new OutlineCheckBox("Offensive Types", textColor);
	    styleCheckBox(offensiveTypesBox);
	    OutlineCheckBox itemsCheckBox = new OutlineCheckBox("Items", textColor);
	    styleCheckBox(itemsCheckBox);
	    OutlineCheckBox abilitiesCheckBox = new OutlineCheckBox("Abilities", textColor);
	    styleCheckBox(abilitiesCheckBox);

	    checkBoxPanel.add(trainersCheckBox);
	    checkBoxPanel.add(pokemonCheckBox);
	    checkBoxPanel.add(encountersCheckBox);
	    checkBoxPanel.add(movesCheckBox);
	    checkBoxPanel.add(defensiveTypesBox);
	    checkBoxPanel.add(offensiveTypesBox);
	    checkBoxPanel.add(itemsCheckBox);
	    checkBoxPanel.add(abilitiesCheckBox);

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
	    
	    // Create the label for displaying last modified time
	    OutlineLabel lastModifiedLabel = new OutlineLabel("Last Modified: N/A");
	    lastModifiedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
	    lastModifiedLabel.setForeground(textColor);
	    lastModifiedLabel.setOutlineColor(Color.BLACK);

	    // Add ActionListener to update labels
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

	                // Set the last modified time of the selected file
	                File saveFile = new File("./saves/" + name);
	                long lastModified = saveFile.lastModified();
	                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
	                String lastModifiedStr = "    " + sdf.format(new Date(lastModified));

	                // Update the last modified label
	                lastModifiedLabel.setText(lastModifiedStr);

	            } catch (IOException | ClassNotFoundException | ClassCastException f) {
	                f.printStackTrace();
	            }
	        }
	    });

	    // Add the last modified label below the icon panel
	    JPanel lastModifiedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    lastModifiedPanel.setPreferredSize(new Dimension(400, 10));
	    lastModifiedPanel.setOpaque(false);
	    lastModifiedPanel.add(lastModifiedLabel);
	    
	    // Add the last modified label below the icon panel
	    JPanel generateDocsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    generateDocsPanel.setPreferredSize(new Dimension(400, 10));
	    generateDocsPanel.setOpaque(false);
	    generateDocsPanel.add(generateDoc);

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
	            itemsCheckBox.isSelected(),
	            abilitiesCheckBox.isSelected()
	        };

	        gp.player.currentSave = save;
	        Main.loadSave(window, save, this, selectedOptions, false);
	    });

	    newGameButton.addActionListener(e -> {
	        String save = null;
	        boolean nuzlocke = false;

	        ArrayList<String> filesFinal = getDatFiles();
	        
	        JPanel panel = new JPanel(new BorderLayout(5, 5));

	        JPanel inputPanel = new JPanel(new GridLayout(0, 1));
	        JTextField saveField = new JTextField();
	        JCheckBox nuzlockeBox = new JCheckBox("Enable Nuzlocke Mode");
	        
	        inputPanel.add(new JLabel("Enter a new save file name (A-Z, 0-9, _, and - are permitted, ≤ 20 characters):"));
	        inputPanel.add(saveField);
	        inputPanel.add(nuzlockeBox);

	        panel.add(inputPanel, BorderLayout.CENTER);

	        int result;
	        do {
	            result = JOptionPane.showConfirmDialog(
	                null, 
	                panel, 
	                "Create New Save", 
	                JOptionPane.OK_CANCEL_OPTION, 
	                JOptionPane.PLAIN_MESSAGE
	            );

	            if (result != JOptionPane.OK_OPTION) {
	                return; // user cancelled
	            }

	            save = saveField.getText().trim();
	            nuzlocke = nuzlockeBox.isSelected();

	            if (!isValidFileName(save)) {
	                JOptionPane.showMessageDialog(null, "Invalid file name. Please use only A-Z, 0-9, underscores (_), and hyphens (-), and make sure it's ≤ 20 characters.");
	            } else if (filesFinal.contains(save + ".dat")) {
	                JOptionPane.showMessageDialog(null, "Save file already exists! Select it and press 'Continue'.");
	                save = "$"; // force re-prompt
	            }
	        } while (!isValidFileName(save));

	        boolean[] selectedOptions = {
	            trainersCheckBox.isSelected(),
	            pokemonCheckBox.isSelected(),
	            encountersCheckBox.isSelected(),
	            movesCheckBox.isSelected(),
	            defensiveTypesBox.isSelected(),
	            offensiveTypesBox.isSelected(),
	            itemsCheckBox.isSelected(),
	            abilitiesCheckBox.isSelected()
	        };

	        save += ".dat";
	        gp.player.currentSave = save;

	        Main.loadSave(window, save, this, selectedOptions, nuzlocke);
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
	    add(lastModifiedPanel);
	    add(generateDocsPanel);
	    add(checkBoxContainer);
	    add(optionsPanel);
	}
	
	private void styleCheckBox(JCheckBox box) {
		box.setOpaque(false);
		box.setFont(box.getFont().deriveFont(16F));
		box.setContentAreaFilled(false);
		box.setBorderPainted(false);
		box.setFocusPainted(false);
		box.setMaximumSize(new Dimension(Integer.MAX_VALUE, box.getPreferredSize().height));
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
        
        // Sort the file names based on the last modified time
        fileNames.sort(Comparator.comparing(path -> {
            try {
            	FileTime time = Files.getLastModifiedTime(Paths.get("./saves/" + path));
                return time.toMillis();
            } catch (IOException e) {
                e.printStackTrace();
                return 0L;
            }
        }).reversed());

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
    		originalSprite = new ImageIcon(p.getMiniSprite(p.id));
    	}
    	
		Image image = originalSprite.getImage();
		
		ImageIcon imageIcon = new ImageIcon(image);
		
		return imageIcon;
	}
    
    public class OutlineLabel extends JLabel {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Color outlineColor = Color.WHITE;
        private boolean isPaintingOutline = false;
        private boolean forceTransparent = false;

        public OutlineLabel() {
            super();
        }

        public OutlineLabel(String text) {
            super(text);
        }

        public OutlineLabel(String text, int horizontalAlignment) {
            super(text, horizontalAlignment);
        }

        public Color getOutlineColor() {
            return outlineColor;
        }

        public void setOutlineColor(Color outlineColor) {
            this.outlineColor = outlineColor;
            this.invalidate();
        }

        @Override
        public Color getForeground() {
            if (isPaintingOutline) {
                return outlineColor;
            } else {
                return super.getForeground();
            }
        }

        @Override
        public boolean isOpaque() {
            if (forceTransparent) {
                return false;
            } else {
                return super.isOpaque();
            }
        }

        @Override
        public void paint(Graphics g) {

            String text = getText();
            if (text == null || text.length() == 0) {
                super.paint(g);
                return;
            }

            // 1 2 3
            // 8 9 4
            // 7 6 5

            if (isOpaque())
                super.paint(g);

            forceTransparent = true;
            isPaintingOutline = true;
            g.translate(-1, -1);
            super.paint(g); // 1
            g.translate(1, 0);
            super.paint(g); // 2
            g.translate(1, 0);
            super.paint(g); // 3
            g.translate(0, 1);
            super.paint(g); // 4
            g.translate(0, 1);
            super.paint(g); // 5
            g.translate(-1, 0);
            super.paint(g); // 6
            g.translate(-1, 0);
            super.paint(g); // 7
            g.translate(0, -1);
            super.paint(g); // 8
            g.translate(1, 0); // 9
            isPaintingOutline = false;

            super.paint(g);
            forceTransparent = false;

        }
    }
    
    public class OutlineCheckBox extends JCheckBox {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Color outlineColor = Color.BLACK;
        private Color textColor = Color.WHITE;

        public OutlineCheckBox(String text, Color textColor) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            this.textColor = textColor;
        }

        public void setOutlineColor(Color outlineColor) {
            this.outlineColor = outlineColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Get the text and icon positioning
            String text = getText();
            FontMetrics fm = g.getFontMetrics();

            // Calculate text position
            Insets insets = getInsets();
            int iconWidth = 18;
            int x = insets.left + iconWidth;
            int y = (getHeight() + fm.getAscent()) / 2 - 2;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw outline
            g2.setColor(outlineColor);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) {
                        g2.drawString(text, x + dx, y + dy);
                    }
                }
            }

            // Draw main text
            g2.setColor(textColor);
            g2.drawString(text, x, y);

            g2.dispose();
        }
    }

}
