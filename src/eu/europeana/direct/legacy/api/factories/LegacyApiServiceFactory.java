package eu.europeana.direct.legacy.api.factories;

import eu.europeana.direct.legacy.api.LegacyApiService;
import eu.europeana.direct.legacy.api.impl.LegacyApiServiceImpl;

public class LegacyApiServiceFactory {

	 private final static LegacyApiService service = new LegacyApiServiceImpl();

	   public static LegacyApiService getLegacyApi()
	   {
	      return service;
	   }
}
