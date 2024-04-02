package Swing;

import java.awt.Color;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import Swing.Pokemon.Task;


public class TextPane extends JTextPane {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    private StyledDocument doc;
    private JScrollPane scrollPane;

    public TextPane() {
        super();
        doc = getStyledDocument();
        setEditable(false);
    }

    public void write(String text, Color color, boolean bold, int size) {
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setForeground(attributes, color);
        StyleConstants.setBold(attributes, bold);
        StyleConstants.setFontSize(attributes, size);

        try {
            doc.insertString(doc.getLength(), text, attributes);
            SwingUtilities.invokeLater(() -> scrollToBottom());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    public void write(String text) {
    	write(text, Color.BLACK, false, 12);
    }
    
    public void write(String text, boolean bold) {
    	write(text, Color.BLACK, bold, 12);
    }
    
    public void write(String text, boolean bold, int size) {
    	write(text, Color.BLACK, bold, size);
    }
    
    
    
    public void writeln(String text, Color color, boolean bold, int size) {
    	write(text, color, bold, size);
    	write("\n");
    }
    
    public void writeln(String text) {
    	writeln(text, Color.BLACK, false, 12);
    }
    
    public void writeln(String text, boolean bold) {
    	writeln(text, Color.BLACK, bold, 12);
    }
    
    public void writeln(String text, boolean bold, int size) {
    	writeln(text, Color.BLACK, bold, size);
    }

	public void writeln() {
		write("\n");
	}
	
	private void scrollToBottom() {
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		verticalScrollBar.setValue(verticalScrollBar.getMaximum());
	}

	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	public void writeAbility(Pokemon p) {
		Task t = Pokemon.addTask(Task.ABILITY, "[" + p.nickname + "'s " + p.ability + "]:");
		t.setAbility(p);
	}
	
	public void writeAbility(Pokemon p, boolean newLine) {
		if (newLine) {
			writeAbility(p);
		} else {
			write("[" + p.nickname + "'s " + p.ability + "]:", false, 14);
		}
	}

}
