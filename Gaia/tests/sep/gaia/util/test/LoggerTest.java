package sep.gaia.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sep.gaia.environment.Environment;
import sep.gaia.environment.Environment.EnvVariable;
import sep.gaia.util.Logger;

public class LoggerTest {

	private static Logger logger;
	private static BufferedReader logFile;
	
	/**
	 * Initializes the test by getting the instance of the Singleton class
	 * <code>Logger</code>.
	 */
	@BeforeClass
	public static void initTest() throws FileNotFoundException {
		
		logger = Logger.getInstance();
		String logFilePath = Environment.getInstance().getString(EnvVariable.LOG_FILE);
		logFile = new BufferedReader(new InputStreamReader(new FileInputStream(logFilePath)));
	}
	
	private String getDateString() {
		Date currentDate = new Date();
		return new SimpleDateFormat(
				"dd.MM.yyyy HH:mm").format(currentDate);
	}
	
	@Test
	public void testError() {
		
		logger.error("Some message!");
		
		String line = null;
		try {
			line = logFile.readLine();
		} catch (IOException e) {
			fail("File cannot be read.");
		}
		assertEquals(line, getDateString() + " Error: Some message!");
	}

	@Test
	public void testWarning() {
		logger.warning("Some message!");
		
		String line = null;
		try {
			line = logFile.readLine();
		} catch (IOException e) {
			fail("File cannot be read.");
		}
		assertEquals(line, getDateString() + " Warning: Some message!");
	}

	@Test
	public void testMessage() {
		logger.message("Some message!");
		
		String line = null;
		try {
			line = logFile.readLine();
		} catch (IOException e) {
			fail("File cannot be read.");
		}
		assertEquals(line, getDateString() + " : Some message!");
	}

	@AfterClass
	public static void closeFile() throws IOException {
		logFile.close();
	}
}
