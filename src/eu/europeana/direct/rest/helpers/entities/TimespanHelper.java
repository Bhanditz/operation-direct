package eu.europeana.direct.rest.helpers.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpanLangaugeAware;

public class TimespanHelper {

	public void sortByLanguage(CulturalHeritageObject cho, List<TimeSpan> temporal, String language) {
		// go through concepts and check if any of them has the same language
		// value as specified in request parameter
		List<TimeSpanLangaugeAware> timespanAwareList = new ArrayList<TimeSpanLangaugeAware>();
		for (int i = 0; i < cho.getTemporal().size(); i++) {
			if (cho.getTemporal().get(i).getLanguageAwareFields().stream()
					.anyMatch(t -> t.getLanguage().equals(language))) {
				// find all lang aware object that match the language parameter
				timespanAwareList.addAll(cho.getTemporal().get(i).getLanguageAwareFields().stream()
						.filter(co -> co.getLanguage().equals(language)).collect(Collectors.toList()));
				// clear concept lang aware object
				cho.getTemporal().get(i).getLanguageAwareFields().clear();
				// add only concept lang aware objects that match the language
				// parameter
				cho.getTemporal().get(i).getLanguageAwareFields().addAll(timespanAwareList);
			} else {
				// remove concept from cho response
				cho.getTemporal().remove(i);
			}
		}
	}

}
