package sep.gaia.resources.weather;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.media.opengl.GLProfile;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class WeatherImageFactory {

	private static final float FONT_SIZE_BIG = 28.0f;
	private static final float FONT_SIZE_MEDIUM = 20.0f;

	private static boolean initialized;

	private static Font standardFont;
	private static Font font;

	private static Map<Integer, BufferedImage> iconsDay;
	private static Map<Integer, BufferedImage> iconsNight;

	@SuppressWarnings("rawtypes")
	public static BufferedImage createImage(WeatherResource res, int width,
			int height) {
		if (res == null) {
			return null;
		}

		init();

		// Coordinates for the image.
		int x = 0;
		int y = 0;

		// Weather resource data.
		String name = res.getName();
		String temp = String.valueOf(res.getTemperature());
		temp = temp.substring(0, Math.min(6, temp.length())) + "°C";
		int weatherCode = res.getWeatherConditionCode();
		BufferedImage weatherIcon = (weatherCode == 0) ? (null) : getWeatherIcon(weatherCode);

		// Generate the image.
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);

		// Draw in the image.
		Graphics2D g = image.createGraphics();

		// Make fonts nice.
		@SuppressWarnings("rawtypes")
		Map desktopHints = null;

		if (desktopHints == null) {
			Toolkit tk = Toolkit.getDefaultToolkit();
			desktopHints = (Map) (tk
					.getDesktopProperty("awt.font.desktophints"));
		}
		if (desktopHints != null) {
			g.addRenderingHints(desktopHints);
		}

		g.setColor(new Color(0.2f, 0.0f, 1.0f, 0.5f));
		g.fillRect(x, y, width, height);
		g.setColor(new Color(0.0f, 0.0f, 0.0f, 1.0f));
		g.setFont(fontSize(FONT_SIZE_BIG));
		g.drawString(name, x + 10, y + 25);
		g.setFont(fontSize(FONT_SIZE_MEDIUM));
		g.drawString(temp, x + 10, y + 50);

		if (weatherIcon != null) {
			int dx1 = (int) (width - width * 0.4f);
			int dy1 = 0;
			int dx2 = width;
			int dy2 = (int) (height - height * 0.4f);
			int sx1 = 0;
			int sy1 = 0;
			int sx2 = weatherIcon.getWidth();
			int sy2 = weatherIcon.getHeight();

			g.drawImage(weatherIcon, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
					null);
		}

		return image;
	}
	
	private WeatherImageFactory() {
	}

	private static void init() {
		if (!initialized) {
			// Create new font.
			try {
				standardFont = Font.createFont(
						Font.TRUETYPE_FONT,
						new File(Environment.getInstance().getString(
								EnvVariable.WEATHER_FONT)));
				standardFont = standardFont.deriveFont(22.0f);
				standardFont = standardFont.deriveFont(Font.BOLD);
				font = standardFont;
			} catch (FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Create maps for storing weather icons.
			iconsDay = new HashMap<Integer, BufferedImage>();
			iconsNight = new HashMap<Integer, BufferedImage>();

			// Now read in the weather icons.
			File iconsDayFolder = new File(Environment.getInstance().getString(
					EnvVariable.WEATHER_ICONS_DAY));
			for (String currentIconName : iconsDayFolder.list()) {
				// Create texture data for weather resource.
				File imageFile = new File(iconsDayFolder.toString() + System.getProperty("file.separator") + currentIconName);
				try {
					BufferedImage image = ImageIO
							.read(imageFile);
					Integer key = Integer
							.parseInt(currentIconName.split(Pattern.quote("."))[0]);
					iconsDay.put(key, image);

				} catch (IOException e) {
					System.err.println("Could not read in weather icon: " + currentIconName + "\nAt location: " + iconsDayFolder);
					e.printStackTrace();
				}
			}

			File iconsNightFolder = new File(Environment.getInstance()
					.getString(EnvVariable.WEATHER_ICONS_NIGHT));
			for (String currentIconName : iconsNightFolder.list()) {
				// Create texture data for weather resource.
				File imageFile = new File(iconsNightFolder.toString() + System.getProperty("file.separator") + currentIconName);
				try {
					BufferedImage image = ImageIO
							.read(imageFile);
					Integer key = Integer
							.parseInt(currentIconName.split(Pattern.quote("."))[0]);
					iconsNight.put(key, image);

				} catch (IOException e) {
					System.err.println("Could not read in weather icon: " + currentIconName + "\nAt location: " + iconsNightFolder);
					e.printStackTrace();
				}
			}

			initialized = true;
		}
	}

	private static Font fontSize(float size) {
		return standardFont.deriveFont(size);
	}

	private static BufferedImage getWeatherIcon(int weatherConditionCode) {
		
		int firstDigit = weatherConditionCode / 100;
		int firstTwoDigits = weatherConditionCode / 10;
		int firstThreeDigits = weatherConditionCode;
		
		switch (firstDigit) {
		case 2:
			return iconsDay.get(11);
		case 3:
			return iconsDay.get(9);
		case 6:
			return iconsDay.get(13);
		case 7:
			return iconsDay.get(50);
		case 5:
			switch (firstTwoDigits) {
			case 50:
				return iconsDay.get(10);
			case 51:
				return iconsDay.get(13);
			case 52:
			case 53: 
				return iconsDay.get(9);
			}
		case 8:
			switch (firstThreeDigits) {
			case 800:
				return iconsDay.get(1);
			case 801:
				return iconsDay.get(2);
			case 802:
				return iconsDay.get(3);
			case 803:
				return iconsDay.get(4);
			case 804:
				return iconsDay.get(4);
			}
		}	
	
	return null;

	}
}
