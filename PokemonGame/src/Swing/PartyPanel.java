package Swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;

import Swing.CompoundIcon.Axis;

public class PartyPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel memberPanel = new JPanel();
	private JLabel partyText = new JLabel("");
	private JLabel partySprite = new JLabel("");
	private JProgressBar hpBar = new JProgressBar();
	private Pokemon master;
	@SuppressWarnings("unused")
	private boolean changeColor;
	
	public PartyPanel(Pokemon p, boolean changeColor) {
		if (p == null) {
			partyText.setVisible(false);
			partySprite.setVisible(false);
			hpBar.setVisible(false);
			return;
		}
		
		this.changeColor = changeColor;
		master = p;
		partyText = setupPartyText();
		partySprite = setupPartySprite();
		hpBar = setupHPBar();
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.add(partySprite);
		
		memberPanel = new JPanel(new BorderLayout());
		memberPanel.add(partyText, BorderLayout.NORTH);
        memberPanel.add(hpBar, BorderLayout.SOUTH);
        memberPanel.setOpaque(false);
		this.add(memberPanel);
		Color color2 = p.type2 == null ? p.type1.getColor() : p.type2.getColor();
		this.setBackground(p.type1.getColor(), color2);
		if (p.isFainted()) {
			this.setBackground(new Color(200, 0, 0));
		}
		this.setOpaque(true);
		
		PartyPanel pp = this;
		this.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				pp.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				if (changeColor) {
					if (p.isFainted()) {
						pp.setBackground(new Color(200, 0, 0).brighter());
					} else {
						pp.setBackground(pp.backgroundColorA.brighter(), pp.backgroundColorB.brighter());
					}
				}
			}
			public void mouseExited(MouseEvent evt) {
				pp.setBorder(BorderFactory.createEmptyBorder());
				if (changeColor) {
					if (p.isFainted()) {
						pp.setBackground(new Color(200, 0, 0));
					} else {
						Color color2 = p.type2 == null ? p.type1.getColor() : p.type2.getColor();
						pp.setBackground(p.type1.getColor(), color2);
					}
				}
			}
		});
		this.setPreferredSize(new Dimension(250, 125));
	}
	
	public JLabel getPartyText() {
		return partyText;
	}
	
	private JLabel setupPartyText() {
		JLabel party = new JLabel("");
    	//party.setBackground(master.type1.getColor(), master.type2 == null ? null : master.type2.getColor());
        party.setText(master.nickname + "  lv " + master.getLevel());
        party.setHorizontalAlignment(SwingConstants.CENTER);
        party.setFont(new Font(party.getFont().getName(), Font.BOLD, 16));
        party.setVisible(true);
        party.setOpaque(false);
        
        //party.setPreferredSize(new Dimension(175, 40));
        return party;
	}
	
	private JLabel setupPartySprite() {
		JLabel partySprite = new JLabel();
		
		ImageIcon spriteIcon = new ImageIcon(master.getMiniSprite());
		if (master.isFainted()) {
        	ImageFilter filter = new GrayFilter(true, 25);
        	ImageProducer producer = new FilteredImageSource(master.getMiniSprite().getSource(), filter);
        	spriteIcon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(producer));
        }		
    	ImageIcon itemIcon = null;
    	if (master.item != null) itemIcon = new ImageIcon(setupImage("/items/item.png"));
    	CompoundIcon icon = itemIcon == null ? new CompoundIcon(Axis.Z_AXIS, 0, CompoundIcon.LEFT, CompoundIcon.BOTTOM, spriteIcon) : new CompoundIcon(Axis.Z_AXIS, 0, CompoundIcon.LEFT, CompoundIcon.BOTTOM, spriteIcon, itemIcon);
        partySprite.setIcon(icon);
        
        
        
        return partySprite;
	}
	
	private JProgressBar setupHPBar() {
		JProgressBar partyHP = new JProgressBar(0, 50);
		if (master != null) {
            partyHP.setMaximum(master.getStat(0));
            partyHP.setValue(master.currentHP);
            if (partyHP.getPercentComplete() > 0.5) {
                partyHP.setForeground(new Color(0, 255, 0));
            } else if (partyHP.getPercentComplete() <= 0.5 && partyHP.getPercentComplete() > 0.25) {
                partyHP.setForeground(new Color(255, 255, 0));
            } else {
                partyHP.setForeground(new Color(255, 0, 0));
            }
            partyHP.setVisible(true);
        } else {
            partyHP.setVisible(false);
        }
		return partyHP;
	}
	
	private Color backgroundColorA;
    private Color backgroundColorB;

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
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
		int w = getWidth();
		int h = getHeight();
		GradientPaint gp = new GradientPaint(0, 0, backgroundColorA, w, h, backgroundColorB);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
    	
	}
	
	private BufferedImage setupImage(String path) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
		} catch (Exception e) {
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/items/null.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return image;
	}
}
