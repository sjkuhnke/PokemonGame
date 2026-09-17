package util;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class ScalePanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JComponent content;
    private final Dimension baseSize;

    public ScalePanel(JComponent content, Dimension baseSize) {
        this.content = content;
        this.baseSize = baseSize;

        setLayout(null);
        add(content);
    }

    @Override
    public void doLayout() {
        double scaleX = getWidth() / (double) baseSize.width;
        double scaleY = getHeight() / (double) baseSize.height;

        double scale = Math.min(scaleX, scaleY);

        int width = (int) (baseSize.width * scale);
        int height = (int) (baseSize.height * scale);

        int x = (getWidth() - width) / 2;
        int y = (getHeight() - height) / 2;

        content.setBounds(x, y, width, height);
    }
}