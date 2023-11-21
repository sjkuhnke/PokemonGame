package Overworld;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
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
            img = ImageIO.read(getClass().getResourceAsStream("/gen/background.png"));
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
        continueButton.addActionListener(e -> {
        	Main.loadSave(window, gp, "player", this);
        });
        optionsPanel.add(continueButton);
        
        JGradientButton newGameButton = new JGradientButton("New Game");
        newGameButton.addActionListener(e -> {
        	
        });
        optionsPanel.add(newGameButton);
        
        JTextField fileName = new JTextField("player");
        optionsPanel.add(fileName);
        
        JGradientButton manageButton = new JGradientButton("Manage");
        optionsPanel.add(manageButton);
        
        add(titleLabel, BorderLayout.NORTH);
        add(subtitleLabel, BorderLayout.CENTER);
        add(optionsPanel, BorderLayout.SOUTH);
        
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

}
