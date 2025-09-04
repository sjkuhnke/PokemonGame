package tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IntegerPackingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IntegerPackingPanel() {
		setLayout(new BorderLayout());
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(4, 2));
		
		JLabel xLabel = new JLabel("Enter x index (# of badges): ");
		JTextField xField = new JTextField();
		JLabel yLabel = new JLabel("Enter y index (index in split): ");
		JTextField yField = new JTextField();
		JButton calculateButton = new JButton("Calculate");
		JLabel resultLabel = new JLabel("Result: ");
		
		inputPanel.add(xLabel);
		inputPanel.add(xField);
		inputPanel.add(yLabel);
		inputPanel.add(yField);
		inputPanel.add(new JLabel());
        inputPanel.add(calculateButton);
        
        add(inputPanel, BorderLayout.CENTER);
        add(resultLabel, BorderLayout.SOUTH);
        
        calculateButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		try {
        			int x = Integer.parseInt(xField.getText());
        			int y = Integer.parseInt(yField.getText());
        			int result = pack(x, y);
        			resultLabel.setText("Put the following int in an NPC flag field for flag[" + x + "][" + y + "]: " + result);
        		} catch (NumberFormatException ex) {
        			resultLabel.setText("Invalid input. Please enter integers.");
        		}
        	}
        });
	}
	
	public static int pack(int x, int y) {
		return (x << 5) | y;
	}

}
