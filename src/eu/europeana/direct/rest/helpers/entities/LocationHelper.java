package eu.europeana.direct.rest.helpers.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageAware;

public class LocationHelper {

	public void sortByLanguage(CulturalHeritageObject cho, List<Place> spatial, String language) {
		// go through place and check if any of them has the same language
		// value as specified in request parameter
		List<PlaceLanguageAware> locationAwareList = new ArrayList<PlaceLanguageAware>();
		for (int i = 0; i < cho.getSpatial().size(); i++) {
			if (cho.getSpatial().get(i).getLanguageAwareFields().stream()
					.anyMatch(t -> t.getLanguage().equals(language))) {
				// find all lang aware object that match the language parameter
				locationAwareList.addAll(cho.getSpatial().get(i).getLanguageAwareFields().stream()
						.filter(co -> co.getLanguage().equals(language)).collect(Collectors.toList()));
				// clear place lang aware object
				cho.getSpatial().get(i).getLanguageAwareFields().clear();
				// add only place lang aware objects that match the language
				// parameter
				cho.getSpatial().get(i).getLanguageAwareFields().addAll(locationAwareList);
			} else {
				// remove place from cho response
				cho.getSpatial().remove(i);
			}
		}
	}

}
