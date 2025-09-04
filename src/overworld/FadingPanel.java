package overworld;

import javax.swing.JPanel;

public class FadingPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private int alpha;
		
		public FadingPanel() {
			alpha = 0;
		}
		
		public int getAlpha() {
			return alpha;
		}
		
		public void setAlpha(int alpha) {
			this.alpha = alpha;
			repaint();
		}
		
//		protected void paintComponent(Graphics g) {
//	        super.paintComponent(g);
//	        Graphics2D g2d = (Graphics2D) g.create();
//	        try {
//		        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
//		        g2d.setColor(getBackground());
//		        g2d.fillRect(0, 0, getWidth(), getHeight());
//		        g2d.dispose();
//	        } catch (IllegalArgumentException e) {
//	        	System.out.println(alpha + " is out of range");
//	        	e.printStackTrace();
//	        }
//	    }
	}