package overworld;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class LoadingScreen {
	private GamePanel gp;
	private int progress; // 0-100
	private String currentTask;
	private Image icon;
	
	public LoadingScreen(GamePanel gp) {
		this.gp = gp;
		reset();
		loadIcon();
	}
	
	private void loadIcon() {
		try {
			icon = ImageIO.read(getClass().getResourceAsStream("/gen/icon4.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void setProgress(int progress, String task) {
		this.progress = Math.min(100, Math.max(0, progress));
		this.currentTask = task;
		
		SwingUtilities.invokeLater(() -> {
			gp.repaint();
		});
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void reset() {
		this.progress = 0;
		this.currentTask = "Initializing...";
	}
	
	public void draw(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		g2.setFont(gp.marumonica);
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		
		if (icon != null) {
			int iconSize = gp.tileSize * 3;
			int iconX = (gp.screenWidth - iconSize) / 2;
			int iconY = gp.tileSize * 2;
			g2.drawImage(icon, iconX, iconY, iconSize, iconSize, null);
		}
		
		g2.setColor(Color.WHITE);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
		String title = Main.gameTitle;
		FontMetrics fm = g2.getFontMetrics();
		int titleX = (gp.screenWidth - fm.stringWidth(title)) / 2;
		int titleY = gp.tileSize * 6;
		g2.drawString(title, titleX, titleY);
		
		int barWidth = gp.tileSize * 10;
		int barHeight = gp.tileSize / 2;
		int barX = (gp.screenWidth - barWidth) / 2;
		int barY = gp.tileSize * 8;
		
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(barX, barY, barWidth, barHeight);
		
		int fillWidth = (int) (barWidth * (progress / 100.0));
		g2.setColor(new Color(0, 200, 100));
		g2.fillRect(barX, barY, fillWidth, barHeight);
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
		String progressText = progress + "%";
		fm = g2.getFontMetrics();
		int progressX = (gp.screenWidth - fm.stringWidth(progressText)) / 2;
		int progressY = barY + barHeight + gp.tileSize;
		g2.setColor(Color.WHITE);
		g2.drawString(progressText, progressX, progressY);
		
		g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 20F));
		fm = g2.getFontMetrics();
		
		int taskY = progressY + gp.tileSize;
		g2.setColor(Color.LIGHT_GRAY);
		for (String task : currentTask.split("\n")) {
			int taskX = (gp.screenWidth - fm.stringWidth(task)) / 2;
			g2.drawString(task, taskX, taskY);
			taskY += gp.tileSize / 2;
		}
	}
}
