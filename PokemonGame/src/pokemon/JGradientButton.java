package pokemon;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.JButton;

public class JGradientButton extends JButton {
    private static final long serialVersionUID = 639680055516122456L;
    private Color backgroundColorA;
    private Color backgroundColorB;
    private boolean solid = false;
    private boolean solidGradient = false;

    public JGradientButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBackground(Color.WHITE, Color.WHITE); // Default gradient colors
    }
    
    public void setSolid(boolean a) {
    	solid = a;
    }
    
    public void setSolidGradient(boolean a) {
    	solidGradient = a;
    }

    // Set the background gradient colors
    public void setBackground(Color a, Color b) {
    	if (a == null && b == null) {
            throw new NullPointerException("Both colors cannot be null.");
        }
    	
    	if (a == null) {
    		a = b;
    	} else if (b == null) {
    		b = a;
    	}
    	
        this.backgroundColorA = a;
        this.backgroundColorB = b;
        repaint(); // Repaint to apply the new colors
    }
    
    @Override
    public void setBackground(Color c) {
    	if (c == null) c = Color.white;
    	this.backgroundColorA = c;
    	this.backgroundColorB = c;
    	repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
    	if (!solidGradient) {
    		Color middle = solid ? backgroundColorA : Color.white;
	    	
    		Graphics2D g2 = (Graphics2D) g.create();
	        g2.setPaint(new GradientPaint(
	                new Point(0, 0),
	                backgroundColorA,
	                new Point(0, getHeight() / 3),
	                middle));
	        g2.fillRect(0, 0, getWidth(), getHeight() / 3);
	        g2.setPaint(new GradientPaint(
	                new Point(0, getHeight() / 3),
	                middle,
	                new Point(0, getHeight()),
	                backgroundColorB));
	        g2.fillRect(0, getHeight() / 3, getWidth(), getHeight());
	        g2.dispose();
	    	
	    	super.paintComponent(g);
    	} else {
    		Graphics2D g2 = (Graphics2D) g;
    		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
    		int w = getWidth();
    		int h = getHeight();
    		GradientPaint gp = new GradientPaint(0, 0, backgroundColorA, w, h, new Color(245, 225, 210));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            
            super.paintComponent(g);
    	}
    }
}
