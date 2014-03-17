package sep.gaia.resources.geoimage;

import sep.gaia.resources.AbstractLoaderWorker;
import sep.gaia.resources.Cache;
import sep.gaia.resources.WorkerFactory;

public class GeoImageParseWorkerFactory implements WorkerFactory<GeoImageParseQuery, GeoImageData> {

	@Override
	public AbstractLoaderWorker<GeoImageParseQuery, GeoImageData> createWorker(
			GeoImageParseQuery query, Cache<GeoImageData> cache) {
		
		return new GeoImageParseWorker(query);
	}

}
