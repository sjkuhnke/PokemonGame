package Swing;

import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

class CustomProgressBarUI extends BasicProgressBarUI {
    private Color stringColor;

    public CustomProgressBarUI(Color stringColor) {
        this.stringColor = stringColor;
    }

    @Override
    protected void paintString(Graphics g, int x, int y, int width, int height, int amountFull, Insets b) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(stringColor);
        String progressString = progressBar.getString();
        if (progressBar.isStringPainted() && progressString != null && !progressString.isEmpty()) {
            g2d.drawString(progressString, x + 2, y + height - 2);
        }
    }
}

