package eu.europeana.direct.rest.helpers.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

public class AgentHelper {

	public void sortByLanguage(CulturalHeritageObject cho, List<Agent> agents, String language) {
		// go through agents and check if any of them has the same language
		// value as specified in request parameter
		List<AgentLanguageAware> agentAwareList = new ArrayList<AgentLanguageAware>();
		for (int i = 0; i < cho.getAgents().size(); i++) {
			if (cho.getAgents().get(i).getLanguageAwareFields().stream()
					.anyMatch(t -> t.getLanguage().equals(language))) {
				// find all lang aware object that match the language parameter
				agentAwareList.addAll(cho.getAgents().get(i).getLanguageAwareFields().stream()
						.filter(co -> co.getLanguage().equals(language)).collect(Collectors.toList()));
				// clear agent lang aware object
				cho.getAgents().get(i).getLanguageAwareFields().clear();
				// add only agent lang aware objects that match the language
				// parameter
				cho.getAgents().get(i).getLanguageAwareFields().addAll(agentAwareList);
			} else {
				// remove agent from cho response
				cho.getAgents().remove(i);
			}
		}
	}

}
