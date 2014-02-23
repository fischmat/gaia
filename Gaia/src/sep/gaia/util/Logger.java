package sep.gaia.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;

/**
 * Singleton-class for writing messages into a log-file. The location of the
 * file is specified by an environment-variable in <code>Environment</code>.
 * 
 * @author Matthias Fisch
 * 
 */
public class Logger {

	private static final String LEVEL_0 = "[MESSAGE]";
	private static final String LEVEL_1 = "[WARNING]";
	private static final String LEVEL_2 = "[ERROR]";
	
	/**
	 * The one instance of this class existing.
	 */
	private static Logger instance;

	/**
	 * The logfile to be used for writing messages.
	 */
	private File logFile;

	/**
	 * The stream for writing messages to the logfile.
	 */
	private BufferedWriter logWriter;

	/**
	 * The minimum level messages must have to be printed.
	 */
	private int minimumLevel;

	/**
	 * Default constructor hided because only a single instance should be
	 * existent.
	 * 
	 * @param logFileName
	 *            The path to the file where messages should be stored.
	 */
	private Logger(String logFileName) {
		logFile = new File(logFileName);
	}

	/**
	 * Returns the one instance of the class.
	 * 
	 * @return The one instance of the class.
	 */
	public static Logger getInstance() {
		if (instance != null) {
			return instance;
		} else {
			// Get the path to the logfile:
			Environment env = Environment.getInstance();

			// Create a instance writing to the logfile retrieved:
			instance = new Logger(env.getString(EnvVariable.LOG_FILE));
			return instance;
		}
	}

	/**
	 * Writes a new line to the log-file. The format is
	 * "dd.MM.yyyy HH:mm level: message"
	 * 
	 * @param message
	 *            The message to write.
	 * @param level
	 *            The level of the message.
	 * @param levelName
	 *            The priority-level of the message, e.g. "Error", "Warning".
	 * 
	 * @return <code>true</code> if the line was successfully written.
	 */
	private boolean writeMessage(String message, int level, String levelName) {
		if (level >= minimumLevel) {
			try {
				// If the writer does not exist yet:
				if (logWriter == null) {
					// Create the writer:
					logWriter = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(logFile)));
				}

				// Get and format current date/time:
				Date currentDate = new Date();
				String dateString = new SimpleDateFormat("dd.MM.yyyy HH:mm")
						.format(currentDate);

				// Write line to log-file:
				logWriter.write(dateString + " " + levelName + ": " + message
						+ "\n");

				// Write also on standard output.
				if(levelName == LEVEL_2) {
					System.err.println(levelName + ": " + message);
				} else {
					System.out.println(levelName + ": " + message);
				}

				logWriter.flush(); // Write buffer to file

			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Writes an error-message to the logfile.
	 * 
	 * @param message
	 *            The error message.
	 * @return <code>true</code> if the line was successfully written.
	 */
	public boolean error(String message) {
		return writeMessage(message, 2, LEVEL_2);
	}

	/**
	 * Writes a warning-message to the logfile.
	 * 
	 * @param message
	 *            The message.
	 * @return <code>true</code> if the line was successfully written.
	 */
	public boolean warning(String message) {
		return writeMessage(message, 1, LEVEL_1);
	}

	/**
	 * Writes a message without a priority-level to the logfile.
	 * 
	 * @param message
	 *            The message.
	 * @return <code>true</code> if the line was successfully written.
	 */
	public boolean message(String message) {
		return writeMessage(message, 0, LEVEL_0);
	}

	/**
	 * Returns the minimum level messages must have to be printed.
	 * 
	 * @return The minimum level messages must have to be printed.
	 */
	public int getMinimumLevel() {
		return minimumLevel;
	}

	/**
	 * Sets the minimum level messages must have to be printed.
	 * 
	 * @param minimumLevel
	 *            The minimum level messages must have to be printed.
	 */
	public void setMinimumLevel(int minimumLevel) {
		this.minimumLevel = minimumLevel;
	}

}
