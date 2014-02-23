package sep.gaia.ui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.util.Logger;

/**
 * Creates ImageIcon from image resources.
 * 
 * @author Johannes Bauer
 */
public class IconFactory {

	private IconFactory() {
	}

	/**
	 * Creates a new ImageIcon for the passed icon name.
	 * 
	 * @param pathToImage
	 *            The path to the Icons (which are included in it)
	 * @return The new ImageIcon
	 */
	public static ImageIcon createIcon(String imageURL) {
		if (imageURL != null && !imageURL.toString().isEmpty()) {
			return (new ImageIcon(imageURL));
		} else {
			Logger.getInstance().error("Couldn't find icon at " + imageURL);
			return null;
		}
		
		
	}

	public static Image createImage(String imageURL) {
		try {
			if (imageURL == null || imageURL.isEmpty()) {
				Logger.getInstance().error("Couldn't find icon at " + imageURL);
				return null;
			}

			return ImageIO.read(new File(imageURL));
		} catch (IOException e) {
			Logger.getInstance().error("Couldn't read image at " + imageURL);
			e.printStackTrace();
		}

		return null;
	}
	
	public static Image getIcon(String iconName) {
		
		return (createImage(Environment.getInstance().getString(EnvVariable.ICONS) + System.getProperty("file.separator") + iconName));
	}
}
