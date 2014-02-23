/**
 * 
 */
package sep.gaia.state;

import java.io.InvalidClassException;
import java.util.Collection;
import java.util.LinkedList;

/** 
 * A class for holding all settings of the application represented as key-value-pair.
 * Also functionality for persistently storing and loading the stored data is provided.
 * 
 * @author Matthias Fisch
 */
public class Preferences {
	
	/**
	 * bean associating the name, value and type of a setting. 
	 * 
	 * @author Matthias Fisch
	 *
	 */
	private static class Setting {
		
		/**
		 * Enumeration of all types supported for settings.
		 * 
		 * @author Matthias Fisch
		 *
		 */
		enum SettingType {String, Integer, Float};
		
		
		/**
		 * The name of the setting.
		 */
		private String key;
		
		/**
		 * The value of the setting.
		 */
		private Object value;
		
		/**
		 * The type of <code>value</code>.
		 */
		private SettingType type;

		/**
		 * Initializes the bean.
		 * @param key The name of the setting.
		 * @param value The value of the setting.
		 * @param type The type of <code>value</code>.
		 * @throws Thrown if <code>value</code> has not the type specified in <code>type</code>.
		 */
		public Setting(String key, Object value, SettingType type) throws ClassCastException {
			super();
			this.key = key;
			this.value = value;
			this.type = type;
		}

		/**
		 * Returns the name of the setting.
		 * @return The name of the setting.
		 */
		public String getKey() {
			return key;
		}

		/**
		 * Sets the name of the setting.
		 * @param key The name of the setting.
		 */
		public void setKey(String key) {
			this.key = key;
		}

		/**
		 * Returns the value of the setting.
		 * @return The value of the setting.
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * Sets the value of the setting.
		 * @param value The value of the setting.
		 * @throws Thrown if <code>value</code> has not the same 
		 * type as <code>getType()</code>.
		 */
		public void setValue(Object value) throws ClassCastException {
			this.value = value;
		}

		/**
		 * Returns the type of <code>value</code>.
		 * @return The type of <code>value</code>.
		 */
		public SettingType getType() {
			return type;
		}

		/**
		 * Sets the type of <code>value</code>.
		 * @param type The type of <code>value</code>.
		 */
		public void setType(SettingType type) {
			this.type = type;
		}
		
		
	}
	
	/**
	 * The only instance of this class.
	 */
	private static Preferences instance;

	/**
	 * Collection of all settings specified.
	 */
	private Collection<Setting> settings;
	
	/**
	 * Singleton-class. Hide constructor.
	 */
	private Preferences() {
		settings = new LinkedList<>();
	}
	
	/** 
	 * Returns the only instance of this class. If it does not
	 * exist when called, a new instance is created.
	 * When this is done all saved preferences will be loaded from disk.
	 */
	public static Preferences getInstance() {
		if (instance == null) {
			instance = new Preferences();
		}
		return instance;
	}

	/**
	 * Returns the value of the setting with key <code>key</code>. If the setting does not
	 * exits <code>def</code> is returned.
	 * @param key The name of the setting to query.
	 * @return The value of the setting.
	 * @throws InvalidClassException Thrown if the requested setting exists but is not of type 
	 * <code>String</code>.
	 */
	public String getString(String key, String def) throws InvalidClassException {
		return null;
	}
	
	/**
	 * Returns the value of the setting with key <code>key</code>. If the setting does not
	 * exits <code>def</code> is returned.
	 * @param key The name of the setting to query.
	 * @return The value of the setting.
	 * @throws InvalidClassException Thrown if the requested setting exists but is not of type 
	 * <code>int</code>.
	 */
	public String getInteger(String key, int def) throws InvalidClassException {
		return null;
	}
	
	/**
	 * Returns the value of the setting with key <code>key</code>. If the setting does not
	 * exits <code>def</code> is returned.
	 * @param key The name of the setting to query.
	 * @return The value of the setting.
	 * @throws InvalidClassException Thrown if the requested setting exists but is not of type 
	 * <code>float</code>.
	 */
	public String getFloat(String key, float def) throws InvalidClassException {
		return null;
	}
	
	/** 
	 * Loads all preferences stored in the file <code>PREF_FILE</code>.
	 * This method will create an empty map when the file is not existing or
	 * it is not correctly formatted.
	 * For correct formating see the XML-scheme in <i>preferences.xsd</i>.
	 */
	private void load() {
		// begin-user-code
		// TODO Automatisch erstellter Methoden-Stub

		// end-user-code
	}

	/** 
	 * Stores all currently set data to the file <code>PREF_FILE</code>.
	 * If the file does not exist it will be created. If an error occures
	 * no data will be written.
	 * For correct formating confer the XML-scheme in <i>preferences.xsd</i>.
	 */
	public void store() {
		// begin-user-code
		// TODO Automatisch erstellter Methoden-Stub

		// end-user-code
	}
}