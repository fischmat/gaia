package sep.gaia.util.exception;

/**
 * To be thrown if a bounding-box is created with corners not forming
 * a rectangle.
 * @author Matthias Fisch
 *
 */
public class NotABoxException extends IllegalArgumentException {
	private static final long serialVersionUID = 6789662737738573048L;

	/**
	 * Initializes the exception without specifying an error-message.
	 */
	public NotABoxException() {
		super();
	}

	/**
	 * Initializes the exception with an error-message.
	 * @param s The error-message to be stored in the exception.
	 */
	public NotABoxException(String s) {
		super(s);
	}
}
