package sep.gaia.renderer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;

import sep.gaia.environment.Environment;
import sep.gaia.util.Logger;

/**
 * This class provides functionality to save the current OpenGL frame buffer in
 * order to save it as a screenshot.
 * 
 * @author Johannes Bauer
 */
public class Screenshot {

	private static final String PNG_FORMAT = "png";

	// Used for naming the screenshots.
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	// Hide default constructor.
	private Screenshot() {
	}

	/**
	 * Takes a "screenshot" (i.e. a snapshot of the current frame buffer) from
	 * the OpenGL context. The format is: 3 bytes, three channels (BGR).
	 * 
	 * @param gl
	 *            The OpenGL context to access the frame buffer.
	 */
	public static BufferedImage screenshot(GL2 gl) {
		BufferedImage image;

		int[] readBufferName = new int[1];
		int[] pixelStoreSwapBytes = new int[1];
		int[] pixelStoreLength = new int[1];
		int[] pixelStoreRows = new int[1];
		int[] pixelStorePixels = new int[1];
		int[] pixelStoreAlignment = new int[1];

		// There is no glPushAttrib() Argument which saves pixel store flags.
		// All this flags must be saved/restored manually. I know,
		// glReadBuffer()
		// can be pushed (GL_PIXEL_MODE_BIT).

		gl.glGetIntegerv(GL2.GL_READ_BUFFER, readBufferName, 0);
		gl.glGetIntegerv(GL2.GL_PACK_ALIGNMENT, pixelStoreAlignment, 0);
		gl.glGetIntegerv(GL2.GL_PACK_SKIP_ROWS, pixelStoreRows, 0);
		gl.glGetIntegerv(GL2.GL_PACK_SKIP_PIXELS, pixelStorePixels, 0);
		gl.glGetIntegerv(GL2.GL_PACK_SWAP_BYTES, pixelStoreSwapBytes, 0);
		gl.glGetIntegerv(GL2.GL_PACK_ROW_LENGTH, pixelStoreLength, 0);

		// Get size of viewport.
		int[] viewport = new int[4];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		int width = viewport[2];
		int height = viewport[3];

		// Create the client side image.
		// Reserve 3 bytes for each pixel (BGR). Pixel number: width * height.
		int bytes = width * height * 3;
		image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		ByteBuffer data = ByteBuffer.allocate(bytes);

		// Select buffer and set pixel storage parameters
		gl.glReadBuffer(GL2.GL_FRONT);
		gl.glPixelStorei(GL2.GL_PACK_ALIGNMENT, 1);
		gl.glPixelStorei(GL2.GL_PACK_SKIP_ROWS, 0);
		gl.glPixelStorei(GL2.GL_PACK_SKIP_PIXELS, 0);
		gl.glPixelStorei(GL2.GL_PACK_SWAP_BYTES, GL2.GL_FALSE);
		gl.glPixelStorei(GL2.GL_PACK_ROW_LENGTH, width);

		// Read whole frame buffer data.
		gl.glReadPixels(0, 0, width, height, GL2.GL_BGR, GL2.GL_UNSIGNED_BYTE,
				data);

		// Restore state changes.
		gl.glPixelStorei(GL2.GL_PACK_ROW_LENGTH, pixelStoreLength[0]);
		gl.glPixelStorei(GL2.GL_PACK_SWAP_BYTES, pixelStoreSwapBytes[0]);
		gl.glPixelStorei(GL2.GL_PACK_SKIP_PIXELS, pixelStorePixels[0]);
		gl.glPixelStorei(GL2.GL_PACK_SKIP_ROWS, pixelStoreRows[0]);
		gl.glPixelStorei(GL2.GL_PACK_ALIGNMENT, pixelStoreAlignment[0]);
		gl.glReadBuffer(readBufferName[0]);

		// Copy the pixel data to a BufferedImage data buffer.
		byte[] bufferedImageData = ((DataBufferByte) image.getRaster()
				.getDataBuffer()).getData();

		int srcOffset = 0;
		int numOfBytesPerRow = width * 3;
		int destOffset = numOfBytesPerRow * (height - 1);

		for (int y = 0; y < height; y++) {
			System.arraycopy(data.array(), srcOffset, bufferedImageData,
					destOffset, numOfBytesPerRow);
			srcOffset += numOfBytesPerRow;
			destOffset -= numOfBytesPerRow;
		}

		Logger.getInstance().message("Toke Screenshot.");

		return image;
	}

	/**
	 * Takes a screenshot and saves it as a "PNG"-formatted image file to the
	 * default location (specified in the <code>Environment</code> class.
	 * 
	 * @param gl
	 *            The OpenGL context to access the framebuffer.
	 */
	public static void screenshotAndSave(GL2 gl) {

		// Prepare new file for saving the screenshot.
		Date date = new Date();
		final File destination = new File(Environment.getInstance().getString(
				Environment.EnvVariable.SCREENSHOT_FOLDER)
				+ System.getProperty("file.separator")
				+ dateFormat.format(date) + ".png");

		// Get image data from opengl.
		final BufferedImage screenshot = screenshot(gl);

		// Write image to file in another thread.
		(new Thread() {
			@Override
			public void run() {
				try {
					ImageIO.write(screenshot, PNG_FORMAT, destination);
					Logger.getInstance().message(
							"Save screenshot at " + destination.toString());
				} catch (IOException e) {
					Logger.getInstance().error(
							"Screenshot konnte nicht abgespeichert werden in "
									+ destination.toString());
					e.printStackTrace();
					return;
				}
			}
		}).start();
	}
}