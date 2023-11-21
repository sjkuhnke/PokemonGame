package Overworld;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
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
		
		setLayout(new BorderLayout());
		
		try {
			Random rand = new Random();
			int random = rand.nextInt(2) + 1;
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
        
        optionsPanel = new JPanel(new GridLayout(2, 2));
        
        JGradientButton continueButton = new JGradientButton("Continue");
        optionsPanel.add(continueButton);
        
        JGradientButton newGameButton = new JGradientButton("New Game");
        newGameButton.addActionListener(e -> {
        	
        });
        optionsPanel.add(newGameButton);
        
        JComboBox<String> fileName = new JComboBox<>(getDatFiles());
        optionsPanel.add(fileName);
        
        JGradientButton manageButton = new JGradientButton("Manage");
        optionsPanel.add(manageButton);
        
        continueButton.addActionListener(e -> {
        	String save = (String) fileName.getSelectedItem();
        	if (save == null) {
        		JOptionPane.showMessageDialog(this, "No save to load from!");
        		return;
        	}
        	gp.player.currentSave = save;
        	Main.loadSave(window, gp, save, this);
        });
        
        add(titleLabel, BorderLayout.NORTH);
        add(subtitleLabel, BorderLayout.CENTER);
        add(optionsPanel, BorderLayout.SOUTH);
        
	}
	
	private String[] getDatFiles() {
		ArrayList<String> fileNames = new ArrayList<>();

        // Get the current working directory
        String currentDirectory = System.getProperty("user.dir");

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(currentDirectory), "*.dat")) {
            for (Path path : directoryStream) {
                fileNames.add(path.getFileName().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNames.toArray(new String[0]);
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

}
