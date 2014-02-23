package sep.gaia.util;

/**
 * 
 * Class to provide functions that convert internationally diverting
 * measures.
 * 
 * @author Max Witzelsperger
 *
 */
public final class MeasureConverter {

	private MeasureConverter() { } // utility class constructor
	
	/**
	 * Converts a given temperature in kelvin into the same temperature
	 * in celsius.
	 * 
	 * @param kelvin the given temperature in kelvin
	 * 
	 * @return the temperature in celsius
	 */
	public static double kelvinToCelsius(double kelvin) {
		
		return kelvin - 273.15;
	}
	
	/**
	 * Converts a given temperature in kelvin into the same temperature
	 * in celsius.
	 * 
	 * @param kelvin the given temperature in kelvin
	 * 
	 * @return the temperature in celsius
	 */
	public static float kelvinToCelsius(float kelvin) {
		
		return kelvin - 273.15f;
	}
	
	/**
	 * Converts a given length in feet into the same length in meters.
	 * 
	 * @param feet the given length in feet
	 * 
	 * @return the same length in meters
	 */
	public static double feetToMeter(double feet) {
		
		return feet / 0.3048;
	}
}
