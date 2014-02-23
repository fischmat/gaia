package sep.gaia.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GAIAInfoBar extends JPanel {

	/**
	 * A label where the status-information can be printed.
	 */
	private JLabel statusInfoLabel;
	
	private JLabel positionLabel;
	
	public GAIAInfoBar() {
		
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		
		// Add the status-label:
		statusInfoLabel = new JLabel();
		statusInfoLabel.setFont(statusInfoLabel.getFont().deriveFont(Font.BOLD));
		add(statusInfoLabel, BorderLayout.WEST);
		
		positionLabel = new JLabel("0.0 0.0");
		add(positionLabel, BorderLayout.EAST);
	}

	public void setStatusMessage(String message) {
		statusInfoLabel.setText(message);
	}
	
	public void setPosition(float longitude, float latitude, float height) {
		String distanceUnit;
		if(height > 1000) {
			height /= 1000;
			distanceUnit = "km";
		} else {
			distanceUnit = "m";
		}
		String text = longitude + " " + latitude + " | " + Math.round(height) + " " + distanceUnit;
		positionLabel.setText(text);
	}
}
