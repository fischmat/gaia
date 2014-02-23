package sep.gaia.util.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sep.gaia.util.MeasureConverter;


public class MeasureConverterTest {

	@Test
	/**
	 * Tests if the temperature in Celsius is correctly converted from 
	 * the temperature in Kelvin.
	 */
	public void testKelvinToCelsius() {
		double kelvin = - 22.0;
		double celsius = MeasureConverter.kelvinToCelsius(kelvin);
		double result = kelvin + 273.15;
		assertTrue(result == celsius);
	}
	@Test
	/**
	 * Tests if the length measured in meter is correctly converted from 
	 * the length measured in feet.
	 * 
	 */
	public void testFeetToMeter() {
		double feet = 15.0;
		double meter = MeasureConverter.feetToMeter(feet);
		double result = feet / 0.3048;
		assertTrue(result == meter);
	}

}
