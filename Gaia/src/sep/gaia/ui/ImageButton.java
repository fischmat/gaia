package sep.gaia.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JButton;

public class ImageButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image icon;
	
	public ImageButton(final File image) {
		super();
		
		// Load icon.
		try {
			icon = ImageIO.read(image);
		} catch (IOException e) {
			/* TODO Fehlermeldung*/
			e.printStackTrace();
		} 
		
		setIcon(new Icon() {
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.drawImage(icon, c.getWidth()/4, c.getHeight()/4, c.getWidth()/2, c.getHeight()/2, null);
			}
			
			@Override
			public int getIconWidth() {
				return icon.getWidth(null);
			}
			
			@Override
			public int getIconHeight() {
				return icon.getHeight(null);
			}
		});
		
		repaint();
	}
	
	public ImageButton(String label, final File image) {
		super(label);
		
		// Load icon.
		try {
			icon = ImageIO.read(image);
		} catch (IOException e) {
			/* TODO Fehlermeldung*/
			e.printStackTrace();
		} 
		
		setIcon(new Icon() {
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				//g.drawImage(icon, c.getWidth()/4, c.getHeight()/4, c.getWidth()/2, c.getHeight()/2, null);
				g.drawImage(icon, 0, 0, c.getWidth(), c.getHeight(), null);
			}
			
			@Override
			public int getIconWidth() {
				return icon.getWidth(null);
			}
			
			@Override
			public int getIconHeight() {
				return icon.getHeight(null);
			}
		});
		
		repaint();
	}
}
