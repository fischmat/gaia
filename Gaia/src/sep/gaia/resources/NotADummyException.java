package sep.gaia.resources;

/**
 * A exception that is thrown if a passed <code>DataResource</code> object
 * unexpectedly does not have the dummy-flag set.
 * 
 * @author Johannes Bauer (Spezifikation: Matthias Fisch)
 * 
 */
public class NotADummyException extends IllegalArgumentException {
	private static final long serialVersionUID = -5890558029746893515L;

	/**
	 * Initializes the exception without specifying an error-message.
	 */
	public NotADummyException() {
		super();
	}

	/**
	 * Initializes the exception with a error-message.
	 * 
	 * @param s
	 *            The error-message to be stored in the exception.
	 */
	public NotADummyException(String s) {
		super(s);
	}

}
