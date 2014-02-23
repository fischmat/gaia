package sep.gaia.resources.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import sep.gaia.resources.AbstractLoaderWorker;
import sep.gaia.resources.LoaderEventListener;
import sep.gaia.resources.NotADummyException;
import sep.gaia.resources.Query;
import sep.gaia.util.Logger;
import sep.gaia.util.MeasureConverter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * Worker loads weather data.
 * 
 * @author Johannes Bauer
 */
public class WeatherLoaderWorker extends
		AbstractLoaderWorker<Query, WeatherResource> {

	/**
	 * URL to the server which requests the api calls. The parameters are marked
	 * with "$lat" and "$lon". For concrete requests they'll get replaced by the
	 * concrete latitude and longitude coordinate.
	 */
	private static final String API_REQUEST_URL = "http://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon";

	private LoaderEventListener<WeatherResource> listener;
	private WeatherResource dummy;

	/**
	 * Constructor with the task, which this Worker has to do as a
	 * <code>Query</code> object.
	 * 
	 * @param subQuery
	 *            The query, which this thread has to treat.
	 */
	public WeatherLoaderWorker(Query subQuery,
			LoaderEventListener<WeatherResource> listener) {
		super(subQuery);
		this.listener = listener;
		this.dummy = (WeatherResource) subQuery.getResources().iterator()
				.next();
	}

	@Override
	public void run() {
		// Check if dummy is really a dummy.
		if (!dummy.isDummy()) {
			throw (new NotADummyException(
					"Current weather dummy is not a dummy."));
		}

		String requestURL = getRequestURL(dummy.getLat(), dummy.getLon());

		// Load resource from API.
		Gson gson = new Gson();
		// Read json from input stream.
		InputStream is;
		try {
			is = (new URL(requestURL)).openStream();
		} catch(IOException e) {
			Logger.getInstance().warning(e.getMessage());
			return;
		}
		
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(is));
		
		JsonObject json;
		try {
			json = gson.fromJson(bufReader, JsonObject.class);
		
		} catch(JsonSyntaxException | JsonIOException e) {
			Logger.getInstance().warning("Error parsing json. Details: " + e.getMessage());
			return;
		}
		// Read weather data.
		
		// Read name of location.
		String name = json.get("name").getAsString();
		
		// Read temperature.
		JsonObject weatherMain = json.getAsJsonObject("main");
		float temperature = weatherMain.get("temp").getAsFloat();
		temperature = MeasureConverter.kelvinToCelsius(temperature);
		
		// Read weather condition code if available
		JsonArray weather = json.getAsJsonArray("weather");
		int weatherCode = 0;
		if (weather != null && weather.size() > 0) {
			JsonObject firstEntry = weather.get(0).getAsJsonObject();
			weatherCode = firstEntry.get("id").getAsInt();
		}
		
		// Assign weather data to dummy object.
		dummy.setName(name);
		dummy.setTemperature(temperature);
		dummy.setWeatherConditionCode(weatherCode);
		dummy.setDummy(false);

		// Push results to listener.
		dummy.setDummy(false);
		Collection<WeatherResource> results = new HashSet<>();
		results.add(dummy);
		listener.onResourcesAvailable(results);
	}

	private String getRequestURL(float lat, float lon) {
		String requestURL = new String(API_REQUEST_URL);
		requestURL = requestURL.replace("$lat", String.valueOf(lat));
		requestURL = requestURL.replace("$lon", String.valueOf(lon));

		System.out.println(requestURL);
		
		return requestURL;
	}

}
