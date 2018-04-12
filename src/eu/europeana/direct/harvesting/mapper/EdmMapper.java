package eu.europeana.direct.harvesting.mapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import eu.europeana.direct.harvesting.source.edm.model.EdmAgent;
import eu.europeana.direct.harvesting.source.edm.model.EdmConcept;
import eu.europeana.direct.harvesting.source.edm.model.EdmOaiSource;
import eu.europeana.direct.harvesting.source.edm.model.EdmPlace;
import eu.europeana.direct.harvesting.source.edm.model.EdmTimespan;
import eu.europeana.direct.harvesting.source.edm.model.EdmWebResource;
import eu.europeana.direct.helpers.GeoLocationServiceHelper;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware.TypeEnum;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpanLangaugeAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpanLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;

public class EdmMapper implements IMapper<EdmOaiSource> {

	// number of errors appeared while saving CHO in database
	private int errorCount = 0;
	// number of warnings appeared while saving CHO in database
	private int warningCount = 0;
	// flag for interrupting job
	private boolean interrupted = false;
	private List<CulturalHeritageObject> culturalHeritageObjects;
	private List<String> warningMessages;
	private List<String> errorMessages;

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
	final static Logger logger = Logger.getLogger(EdmMapper.class);

	@Override
	public CulturalHeritageObject mapFromEdm(EdmOaiSource edmSource) {

		culturalHeritageObjects = new ArrayList<CulturalHeritageObject>();
		warningMessages = new ArrayList<String>();
		errorMessages = new ArrayList<String>();

		KeyValuePair keyValuePair;

		CulturalHeritageObject culturalHeritageObject = new CulturalHeritageObject();
		List<CulturalHeritageObjectLanguageAware> culturalHeritageObjectLanguageAwareList = new ArrayList<CulturalHeritageObjectLanguageAware>();
		CulturalHeritageObjectLanguageAware culturalHeritageObjectLanguageAware = new CulturalHeritageObjectLanguageAware();
		CulturalHeritageObjectLanguageNonAware culturalHeritageObjectLanguageNonAware = new CulturalHeritageObjectLanguageNonAware();
		List<WebLink> weblinksList = new ArrayList<WebLink>();
		String rights = "";

		try {
			String language = null;

			// CHO LANGUAGE AWARE MAPPING
			if (edmSource.getProvidedCHO().getOaiDc().getTitle().size() > 0) {

				if (!edmSource.getProvidedCHO().getOaiDc().getLanguage().isEmpty()) {
					language = edmSource.getProvidedCHO().getOaiDc().getLanguage().get(0);
				}

				for (Map.Entry<String, String> choLanguageAwareTitleList : edmSource.getProvidedCHO().getOaiDc()
						.getTitle().entrySet()) {

					culturalHeritageObjectLanguageAware.setTitle(choLanguageAwareTitleList.getValue());
					boolean hasLangAttribute = false;

					/*
					 * check if title element has xml:lang attribute, then key
					 * should be 2 letter iso code. Else key value is:
					 * "key {number}"
					 */
					if (choLanguageAwareTitleList.getKey().length() == 2) {
						hasLangAttribute = true;
						if (language == null) {
							language = choLanguageAwareTitleList.getKey();
						}
						culturalHeritageObjectLanguageAware.setLanguage(choLanguageAwareTitleList.getKey());
					} else {
						// if not check for element language
						if (language == null) {
							errorCount++;
							errorMessages.add(
									"ERROR: Cannot map Cultural heritage object, required field Language is missing");
						} else {
							culturalHeritageObjectLanguageAware.setLanguage(language);
						}
					}

					// title language doesnt match description xml:lang
					// attribute
					if (hasLangAttribute && edmSource.getProvidedCHO().getOaiDc().getDescription().size() > 0
							&& !edmSource.getProvidedCHO().getOaiDc().getDescription()
									.containsKey(choLanguageAwareTitleList.getKey())) {
						if (culturalHeritageObjectLanguageAware.getDescription() == null) {
							for (Map.Entry<String, List<String>> descriptionMap : edmSource.getProvidedCHO().getOaiDc()
									.getDescription().entrySet()) {
								for (String d : descriptionMap.getValue()) {
									culturalHeritageObjectLanguageAware.setDescription(d);
								}
							}
						}
					} else {
						if (edmSource.getProvidedCHO().getOaiDc().getDescription().size() > 0) {
							for (Map.Entry<String, List<String>> descriptionMap : edmSource.getProvidedCHO().getOaiDc()
									.getDescription().entrySet()) {

								// if title doesn't have xml:lang attribute
								// neither does description
								if (!hasLangAttribute) {
									for (String d : descriptionMap.getValue()) {
										culturalHeritageObjectLanguageAware.setDescription(d);
									}
								} else {
									// find description with same xml:lang
									// attribute as title
									if (descriptionMap.getKey().equals(choLanguageAwareTitleList.getKey())) {
										for (String d : descriptionMap.getValue()) {
											culturalHeritageObjectLanguageAware.setDescription(d);
										}
									}
								}
							}
						} else {
							// if there is no description element in metadata,
							// title value is set for description of CHO
							culturalHeritageObjectLanguageAware.setDescription(choLanguageAwareTitleList.getValue());
						}
					}

					if (edmSource.getProvidedCHO().getOaiDc().getFormat().size() > 0) {
						for (Map.Entry<String, List<String>> formatMap : edmSource.getProvidedCHO().getOaiDc()
								.getFormat().entrySet()) {
							// if title doesn't have xml:lang attribute neither
							// does description
							if (!hasLangAttribute) {
								for (String f : formatMap.getValue()) {
									culturalHeritageObjectLanguageAware.getFormat().add(f);
								}
							} else {								
								// find format with same xml:lang attribute as
								// title
								if (formatMap.getKey().equals(choLanguageAwareTitleList.getKey())) {								
									for (String f : formatMap.getValue()) {
										culturalHeritageObjectLanguageAware.getFormat().add(f);
									}
								} else {																		
									for (String f : formatMap.getValue()) {
										if (f.length() > 1) {
											culturalHeritageObjectLanguageAware.getFormat().add(f);
										}
									}
								}
							}
						}
					}

					if (edmSource.getProvidedCHO().getOaiDc().getPublisher().size() > 0) {
						for (Map.Entry<String, List<String>> publisherMap : edmSource.getProvidedCHO().getOaiDc()
								.getPublisher().entrySet()) {

							// if title doesn't have xml:lang attribute neither
							// does publisher
							if (!hasLangAttribute) {
								for (String p : publisherMap.getValue()) {
									culturalHeritageObjectLanguageAware.getPublisher().add(p);
								}
							} else {
								// find format with same xml:lang attribute as
								// title
								if (publisherMap.getKey().equals(choLanguageAwareTitleList.getKey())) {
									for (String p : publisherMap.getValue()) {
										culturalHeritageObjectLanguageAware.getPublisher().add(p);
									}
								}
							}
						}
					}

					if (edmSource.getProvidedCHO().getOaiDc().getSource().size() > 0) {
						for (Map.Entry<String, List<String>> sourceMap : edmSource.getProvidedCHO().getOaiDc()
								.getSource().entrySet()) {

							// if title doesn't have xml:lang attribute neither
							// does source
							if (!hasLangAttribute) {
								for (String s : sourceMap.getValue()) {
									culturalHeritageObjectLanguageAware.getSource().add(s);
								}
							} else {
								// find source with same xml:lang attribute as
								// title
								if (sourceMap.getKey().equals(choLanguageAwareTitleList.getKey())) {
									for (String s : sourceMap.getValue()) {
										culturalHeritageObjectLanguageAware.getSource().add(s);
									}
								}
							}
						}
					}

					if (edmSource.getProvidedCHO().getAlternativeTitle().size() > 0) {
						for (Map.Entry<String, List<String>> alternativeTitleMap : edmSource.getProvidedCHO()
								.getAlternativeTitle().entrySet()) {

							// if title doesn't have xml:lang attribute neither
							// does alternative
							if (!hasLangAttribute) {
								for (String alt : alternativeTitleMap.getValue()) {
									culturalHeritageObjectLanguageAware.getAlternative().add(alt);
								}
							} else {
								// find source with same xml:lang attribute as
								// title
								if (alternativeTitleMap.getKey().equals(choLanguageAwareTitleList.getKey())) {
									for (String alt : alternativeTitleMap.getValue()) {
										culturalHeritageObjectLanguageAware.getAlternative().add(alt);
									}
								}
							}
						}
					}

					if (edmSource.getProvidedCHO().getIssued().size() > 0) {
						for (Map.Entry<String, String> issuedMap : edmSource.getProvidedCHO().getIssued().entrySet()) {

							// if title doesn't have xml:lang attribute neither
							// does issued
							if (!hasLangAttribute) {
								culturalHeritageObjectLanguageAware.setIssued(issuedMap.getValue());
							} else {
								culturalHeritageObjectLanguageAware.setIssued(issuedMap.getValue());
							}
						}
					}

					if (edmSource.getProvidedCHO().getExtent().size() > 0) {
						for (Map.Entry<String, List<String>> extentMap : edmSource.getProvidedCHO().getExtent()
								.entrySet()) {

							// if title doesn't have xml:lang attribute neither
							// does extent
							if (!hasLangAttribute) {
								for (String e : extentMap.getValue()) {
									culturalHeritageObjectLanguageAware.getExtent().add(e);
								}
							} else {
								// find source with same xml:lang attribute as
								// title
								if (extentMap.getKey().equals(choLanguageAwareTitleList.getKey())) {
									for (String e : extentMap.getValue()) {
										culturalHeritageObjectLanguageAware.getExtent().add(e);
									}
								} else {
									for (String e : extentMap.getValue()) {
										culturalHeritageObjectLanguageAware.getExtent().add(e);
									}
								}
							}
						}
					}

					if (edmSource.getProvidedCHO().getMedium().size() > 0) {
						for (Map.Entry<String, List<String>> mediumMap : edmSource.getProvidedCHO().getMedium()
								.entrySet()) {

							// if title doesn't have xml:lang attribute neither
							// does medium
							if (!hasLangAttribute) {
								for (String m : mediumMap.getValue()) {
									culturalHeritageObjectLanguageAware.getMedium().add(m);
								}
							} else {
								// find medium with same xml:lang attribute as
								// title
								if (mediumMap.getKey().equals(choLanguageAwareTitleList.getKey())) {
									for (String m : mediumMap.getValue()) {
										culturalHeritageObjectLanguageAware.getMedium().add(m);
									}
								}
							}
						}
					}

					if (edmSource.getProvidedCHO().getProvenance().size() > 0) {
						for (Map.Entry<String, List<String>> provenanceMap : edmSource.getProvidedCHO().getProvenance()
								.entrySet()) {

							// if title doesn't have xml:lang attribute neither
							// does provenance
							if (!hasLangAttribute) {
								for (String pro : provenanceMap.getValue()) {
									culturalHeritageObjectLanguageAware.getProvenance().add(pro);
								}
							} else {
								// find source with same xml:lang attribute as
								// title
								if (provenanceMap.getKey().equals(choLanguageAwareTitleList.getKey())) {
									for (String pro : provenanceMap.getValue()) {
										culturalHeritageObjectLanguageAware.getProvenance().add(pro);
									}
								}
							}
						}
					}

					/*
					 * if
					 * (edmSource.getProvidedCHO().getOaiDc().getRights().size()
					 * > 0) { int counter = 0; for (Map.Entry<String, String>
					 * rightsMap :
					 * edmSource.getProvidedCHO().getOaiDc().getRights()
					 * .entrySet()) {
					 * 
					 * if (interrupted) { break mainloop; }
					 * 
					 * // if title doesn't have xml:lang attribute neither does
					 * rights if(!hasLangAttribute){ keyValuePair = new
					 * KeyValuePair(); keyValuePair.setKey("Rights "
					 * +(++counter));
					 * keyValuePair.setValue(rightsMap.getValue());
					 * culturalHeritageObjectLanguageAware.getCustomFields().add
					 * (keyValuePair); } else { // find source with same
					 * xml:lang attribute as title if (rightsMap.getKey() ==
					 * choLanguageAwareTitleList.getKey()) { keyValuePair = new
					 * KeyValuePair(); keyValuePair.setKey("Rights "
					 * +(++counter));
					 * keyValuePair.setValue(rightsMap.getValue());
					 * culturalHeritageObjectLanguageAware.getCustomFields().add
					 * (keyValuePair); } } } }
					 */

					culturalHeritageObjectLanguageAwareList.add(culturalHeritageObjectLanguageAware);
				}

			} else {
				errorCount++;
				errorMessages
						.add("ERROR: Cannot map Cultural heritage object, required field (Title, Language) is missing");
			}

			// CHO LANGUAGE NON AWARE MAPPING
			if (!edmSource.getProvidedCHO().getOaiDc().getIdentifier().isEmpty())
				culturalHeritageObjectLanguageNonAware
						.setIdentifier(edmSource.getProvidedCHO().getOaiDc().getIdentifier());

			if (!edmSource.getProvidedCHO().getOaiDc().getRelation().isEmpty())
				culturalHeritageObjectLanguageNonAware.setRelation(edmSource.getProvidedCHO().getOaiDc().getRelation());

			if (edmSource.getOreAggregation().getOwner() != null) {
				culturalHeritageObjectLanguageNonAware.setDataOwner(edmSource.getOreAggregation().getOwner());
			}else{
				if (edmSource.getOreAggregation().getDataOwner() != null) {
					culturalHeritageObjectLanguageNonAware.setDataOwner(edmSource.getOreAggregation().getDataOwner());
				}	
			}			

			boolean foundType = false;

			if (edmSource.getEdmType() != null) {
				String type = edmSource.getEdmType();
				switch (type.toUpperCase()) {

				case "IMAGE":
					culturalHeritageObjectLanguageNonAware.setType(TypeEnum.IMAGE);
					foundType = true;
					break;
				case "AUDIO":
					culturalHeritageObjectLanguageNonAware.setType(TypeEnum.AUDIO);
					foundType = true;
					break;
				case "VIDEO":
					culturalHeritageObjectLanguageNonAware.setType(TypeEnum.VIDEO);
					foundType = true;
					break;
				case "3D":
					culturalHeritageObjectLanguageNonAware.setType(TypeEnum._3D);
					foundType = true;
					break;
				case "TEXT":
					culturalHeritageObjectLanguageNonAware.setType(TypeEnum.TEXT);
					foundType = true;
					break;
				default:
					culturalHeritageObjectLanguageNonAware.setType(TypeEnum.IMAGE);
					foundType = true;
					break;
				}
			} else {
				culturalHeritageObjectLanguageNonAware.setType(TypeEnum.IMAGE);
				foundType = true;
			}

			/*
			 * if(!foundType){ errorCount++; System.out.println(
			 * "ERROR: Cannot map Cultural heritage object, required field (type) is missing from language non aware field"
			 * ); errorMessages.add(
			 * "ERROR: Cannot map Cultural heritage object, required field (type) is missing from language non aware field"
			 * ); continue mainloop; }
			 */

			if (edmSource.getOreAggregation().getEdmRights() != null) {

				rights = edmSource.getOreAggregation().getEdmRights();
			}

			/*
			 * if (edmSource.getProvidedCHO().getIsRepresentationOf() != null) {
			 * 
			 * keyValuePair = new KeyValuePair(); keyValuePair.setKey(
			 * "Is representation of");
			 * keyValuePair.setValue(edmSource.getProvidedCHO().
			 * getIsRepresentationOf());
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); }
			 */

			if (edmSource.getOreAggregation().getIsShownAt() != null) {

				WebLink wl = new WebLink();
				wl.setType(eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink.TypeEnum.LANDING_PAGE);
				wl.setLink(edmSource.getOreAggregation().getIsShownAt());
				if (rights == null || rights.length() < 1) {
					wl.setRights("http://rightsstatements.org/page/CNE/1.0/");
				} else {
					wl.setRights(rights);
				}
				if (!weblinksList.stream().anyMatch(w -> w.getLink().equals(wl.getLink()))) {
					weblinksList.add(wl);
				}
			}

			if (edmSource.getOreAggregation().getIsShownBy() != null) {

				WebLink wl = new WebLink();
				wl.setLink(edmSource.getOreAggregation().getIsShownBy());

				wl.setType(eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink.TypeEnum.DIRECT_MEDIA);

				if (rights.length() > 0) {
					wl.setRights(rights);
				} else {
					wl.setRights("http://rightsstatements.org/vocab/CNE/1.0/");
				}
				if (!weblinksList.stream().anyMatch(w -> w.getLink().equals(wl.getLink()))) {
					weblinksList.add(wl);
				}
			}

			if (edmSource.getOreAggregation().getAggregatedCHO() != null) {

				/*
				 * WebLink wl = new WebLink();
				 * wl.setType(eu.europeana.direct.rest.gen.java.io.swagger.model
				 * .WebLink.TypeEnum.LANDING_PAGE);
				 * wl.setLink(edmSource.getOreAggregation().getAggregatedCHO());
				 * wl.setRights(rights); weblinksList.add(wl);
				 */
			}

			/*
			 * if (edmSource.getOreAggregation().getObject() != null) {
			 * 
			 * keyValuePair = new KeyValuePair(); keyValuePair.setKey("object");
			 * keyValuePair.setValue(edmSource.getOreAggregation().getObject());
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); }
			 */

			/*
			 * if (edmSource.getOreAggregation().getUgc() != null) {
			 * 
			 * keyValuePair = new KeyValuePair(); keyValuePair.setKey("Ugc");
			 * keyValuePair.setValue(edmSource.getOreAggregation().getUgc());
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); }
			 */

			/*
			 * if (edmSource.getOreAggregation().getAbout() != null) {
			 * 
			 * keyValuePair = new KeyValuePair(); keyValuePair.setKey("About");
			 * keyValuePair.setValue(edmSource.getOreAggregation().getAbout());
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); }
			 */

			if (edmSource.getProvidedCHO().getCurrentLocation() != null) {

				keyValuePair = new KeyValuePair();
				keyValuePair.setKey("Current location");
				keyValuePair.setValue(edmSource.getProvidedCHO().getCurrentLocation());
				culturalHeritageObjectLanguageNonAware.getCustomFields().add(keyValuePair);
			}

			/*
			 * if(!edmSource.getProvidedCHO().getHasMet().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getHasMet().size(); index++){ if
			 * (interrupted) { break mainloop; }
			 * 
			 * keyValuePair = new KeyValuePair(); keyValuePair.setKey("Has met "
			 * +(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getHasMet().get(
			 * index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */

			/*
			 * if(!edmSource.getProvidedCHO().getHasType().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getHasType().size(); index++){ if
			 * (interrupted) { break mainloop; }
			 * 
			 * keyValuePair = new KeyValuePair(); keyValuePair.setKey(
			 * "Has type "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getHasType().get
			 * (index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * 
			 * if(!edmSource.getProvidedCHO().getHasPart().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getHasPart().size(); index++){ if
			 * (interrupted) { break mainloop; }
			 * 
			 * keyValuePair = new KeyValuePair(); keyValuePair.setKey(
			 * "Has part "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getHasPart().get
			 * (index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * 
			 * if(!edmSource.getProvidedCHO().getHasFormat().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getHasFormat().size(); index++){ if
			 * (interrupted) { break mainloop; }
			 * 
			 * keyValuePair = new KeyValuePair(); keyValuePair.setKey(
			 * "Has format "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getHasFormat().
			 * get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * 
			 * if(!edmSource.getProvidedCHO().getConformsTo().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getConformsTo().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Conforms to "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getConformsTo().
			 * get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * 
			 * if(!edmSource.getProvidedCHO().getHasVersion().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getHasVersion().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Has version "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getHasVersion().
			 * get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getIsFormatOf().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIsFormatOf().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Is format of "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getIsFormatOf().
			 * get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getIsPartOf().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIsPartOf().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Is part of "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getIsPartOf().
			 * get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */

			/*
			 * if(!edmSource.getProvidedCHO().getIsReferencedBy().isEmpty()){
			 * int counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIsReferencedBy().size(); index++){
			 * if (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("isReferencedBy "
			 * +(++counter)); keyValuePair.setValue(edmSource.getProvidedCHO().
			 * getIsReferencedBy().get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */

			/*
			 * if(!edmSource.getProvidedCHO().getIsReplacedBy().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIsReplacedBy().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Is replaced by "
			 * +(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getIsReplacedBy(
			 * ).get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getIsRequiredBy().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIsRequiredBy().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Is required by "
			 * +(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getIsRequiredBy(
			 * ).get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getIsVersionOf().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIsVersionOf().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Is version of "
			 * +(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getIsVersionOf()
			 * .get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getReferences().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getReferences().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("references "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getReferences().
			 * get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getReplaces().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getReplaces().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("replaces "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getReplaces().
			 * get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getRequires().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getRequires().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("requires "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getRequires().
			 * get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */
			/*
			 * if(!edmSource.getProvidedCHO().getTableOfContents().isEmpty()){
			 * int counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getTableOfContents().size(); index++){
			 * if (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Table of contents "
			 * +(++counter)); keyValuePair.setValue(edmSource.getProvidedCHO().
			 * getTableOfContents().get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */

			/*
			 * if(!edmSource.getProvidedCHO().getIncorporates().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIncorporates().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Incorporates "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getIncorporates(
			 * ).get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getIsDerivativeOf().isEmpty()){
			 * int counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIsDerivativeOf().size(); index++){
			 * if (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Is derative of "
			 * +(++counter)); keyValuePair.setValue(edmSource.getProvidedCHO().
			 * getIsDerivativeOf().get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getIsNextInSequence().isEmpty()){
			 * int counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIsNextInSequence().size();
			 * index++){ if (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Is next in sequence "
			 * +(++counter)); keyValuePair.setValue(edmSource.getProvidedCHO().
			 * getIsNextInSequence().get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getIsRelatedTo().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIsRelatedTo().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Is related to "
			 * +(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getIsRelatedTo()
			 * .get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getIsSimilarTo().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIsSimilarTo().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Is similar to "
			 * +(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getIsSimilarTo()
			 * .get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getIsSuccessorOf().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getIsSuccessorOf().size(); index++){
			 * if (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Is successor of "
			 * +(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getIsSuccessorOf
			 * ().get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 * if(!edmSource.getProvidedCHO().getRealizes().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getRealizes().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Realizes "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getRealizes().
			 * get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */
			/*
			 * if(!edmSource.getProvidedCHO().getSameAs().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getSameAs().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Same as "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getSameAs().get(
			 * index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */
			/*
			 * if(!edmSource.getProvidedCHO().getOaiDc().getAudience().isEmpty()
			 * ){ int counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getOaiDc().getAudience().size();
			 * index++){ if (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Audience "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getOaiDc().
			 * getAudience().get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */

			/*
			 * if(!edmSource.getProvidedCHO().getOaiDc().getContributor().
			 * isEmpty()){ int counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getOaiDc().getContributor().size();
			 * index++){ if (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Contributor "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getOaiDc().
			 * getContributor().get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */
			/*
			 * if(!edmSource.getProvidedCHO().getOaiDc().getCoverage().isEmpty()
			 * ){ int counter = 0; for(int index = 0; index <
			 * edmSource.getProvidedCHO().getOaiDc().getCoverage().size();
			 * index++){ if (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Coverage "+(++counter));
			 * keyValuePair.setValue(edmSource.getProvidedCHO().getOaiDc().
			 * getCoverage().get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */
			/*
			 * if(!edmSource.getOreAggregation().getHasView().isEmpty()){ int
			 * counter = 0; for(int index = 0; index <
			 * edmSource.getOreAggregation().getHasView().size(); index++){ if
			 * (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Has view "+(++counter));
			 * keyValuePair.setValue(edmSource.getOreAggregation().getHasView().
			 * get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */
			/*
			 * if(!edmSource.getOreAggregation().getIntermediateProvider().
			 * isEmpty()){ int counter = 0; for(int index = 0; index <
			 * edmSource.getOreAggregation().getIntermediateProvider().size();
			 * index++){ if (interrupted) { break mainloop; } keyValuePair = new
			 * KeyValuePair(); keyValuePair.setKey("Intermediate provider "
			 * +(++counter));
			 * keyValuePair.setValue(edmSource.getOreAggregation().
			 * getIntermediateProvider().get(index));
			 * culturalHeritageObjectLanguageNonAware.getCustomFields().add(
			 * keyValuePair); } }
			 */

			// CONCEPT MAPPING
			Concept concept;
			List<Concept> conceptsList = new ArrayList<Concept>();

			ConceptLanguageAware conceptLanguageAware;
			ConceptLanguageNonAware conceptLanguageNonAware;

			List<ConceptLanguageAware> listConceptLanguageAware;

			if (edmSource.getProvidedCHO().getOaiDc().getType().size() > 0) {
				for (Map.Entry<String, List<String>> typeMap : edmSource.getProvidedCHO().getOaiDc().getType().entrySet()) {
									
					String lang = null;
					
					// if title doesn't have xml:lang attribute neither does
					// type
					if (typeMap.getKey().length() == 2) {
						lang = typeMap.getKey();												
					} else {						
						if (language != null) {
							lang = language;
						} else {
							lang = "en";
						}																								
					}
					
					for (String prefL : typeMap.getValue()) {	
						listConceptLanguageAware = new ArrayList<ConceptLanguageAware>();
						concept = new Concept();
						conceptLanguageNonAware = new ConceptLanguageNonAware();
						conceptLanguageAware = new ConceptLanguageAware();												
						conceptLanguageAware.setPreferredLabel(prefL);
						
						if(conceptLanguageAware.getPreferredLabel() != null){	
							conceptLanguageAware.setLanguage(lang);
							conceptLanguageNonAware.setRole("dc:Type");
							concept.setLanguageNonAwareFields(conceptLanguageNonAware);
							listConceptLanguageAware.add(conceptLanguageAware);
							concept.setLanguageAwareFields(listConceptLanguageAware);
							conceptsList.add(concept);
							
							conceptLanguageAware = null;
							conceptLanguageNonAware = null;
							concept = null;
							listConceptLanguageAware = null;
						}						
					}											
				}
			}			

			if (!edmSource.getProvidedCHO().getOaiDc().getSubject().isEmpty()) {
				for (Entry<String, List<String>> entry : edmSource.getProvidedCHO().getOaiDc().getSubject().entrySet()) {

					String lang = null;
					
					// if title doesn't have xml:lang attribute neither does
					// type
					if (entry.getKey().length() == 2) {
						lang = entry.getKey();												
					} else {						
						if (language != null) {
							lang = language;
						} else {
							lang = "en";
						}																								
					}
					
					for (String prefL : entry.getValue()) {	
						listConceptLanguageAware = new ArrayList<ConceptLanguageAware>();
						concept = new Concept();
						conceptLanguageNonAware = new ConceptLanguageNonAware();
						conceptLanguageAware = new ConceptLanguageAware();						
						conceptLanguageAware.setPreferredLabel(prefL);
						
						if(conceptLanguageAware.getPreferredLabel() != null){	
							conceptLanguageAware.setLanguage(lang);
							conceptLanguageNonAware.setRole("dc:Subject");
							concept.setLanguageNonAwareFields(conceptLanguageNonAware);
							listConceptLanguageAware.add(conceptLanguageAware);
							concept.setLanguageAwareFields(listConceptLanguageAware);
							conceptsList.add(concept);
							
							conceptLanguageAware = null;
							conceptLanguageNonAware = null;
							concept = null;
							listConceptLanguageAware = null;
						}						
					}										
				}
			}
			
			if (!edmSource.getEdmConcept().isEmpty()) {
				for (EdmConcept edmConcept : edmSource.getEdmConcept()) {

					if (edmConcept.getPrefLabel().size() > 0) {
						conceptLanguageNonAware = new ConceptLanguageNonAware();
						concept = new Concept();
						listConceptLanguageAware = new ArrayList<ConceptLanguageAware>();

						for (Map.Entry<String, List<String>> map : edmConcept.getPrefLabel().entrySet()) {							
							String conceptLang = null;
							// check if key is 2 letter iso code for language -
							// attribute xml:lang
							if (map.getKey().length() == 2) {
								conceptLang = map.getKey();
							} else {
								if (language == null) {
									/*
									 * warningCount++; warningMessages.add(
									 * "WARNING: No language for Concept");
									 * continue conceptloop;
									 */

									// default language
									conceptLang = "en";
								} else {
									conceptLang = language;
								}
							}
							if (map.getValue().size() > 0) {
								for (String prefLabel : map.getValue()) {
									if(prefLabel != null){
										conceptLanguageAware = new ConceptLanguageAware();
										conceptLanguageAware.setLanguage(conceptLang);									
										conceptLanguageAware.setPreferredLabel(prefLabel);

										if (edmConcept.getAltLabel().size() > 0) {
											for (Map.Entry<String, List<String>> altLabelMap : edmConcept.getAltLabel()
													.entrySet()) {

												// find alternative label for same
												// language (xml:lang attribute)
												if (altLabelMap.getKey().equals(map.getKey())) {

													for (String alt : altLabelMap.getValue()) {
														conceptLanguageAware.getAlternativeLabel().add(alt);
													}
												}
											}

											// if no xml:lang attribute
											if (conceptLanguageAware.getAlternativeLabel().size() < 1) {
												for (Map.Entry<String, List<String>> alt : edmConcept.getAltLabel()
														.entrySet()) {
													for (String a : alt.getValue()) {
														conceptLanguageAware.getAlternativeLabel().add(a);
													}
												}
											}
										}
										listConceptLanguageAware.add(conceptLanguageAware);
									}																	
								}
							}

							// ConceptLanguageAware custom field
							/*
							 * if(!edmConcept.getBroader().isEmpty()){ for(int
							 * index = 0; index <
							 * edmConcept.getBroader().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Broader "+index);
							 * keyValuePair.setValue(edmConcept.getBroader().get
							 * (index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // ConceptLanguageAware custom field
							 * if(!edmConcept.getNarrower().isEmpty()){ for(int
							 * index = 0; index <
							 * edmConcept.getNarrower().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Narrower "+index);
							 * keyValuePair.setValue(edmConcept.getNarrower().
							 * get(index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // ConceptLanguageAware custom field
							 * if(!edmConcept.getRelated().isEmpty()){ for(int
							 * index = 0; index <
							 * edmConcept.getRelated().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Related "+index);
							 * keyValuePair.setValue(edmConcept.getRelated().get
							 * (index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // ConceptLanguageAware custom field
							 * if(!edmConcept.getBroadMatch().isEmpty()){
							 * for(int index = 0; index <
							 * edmConcept.getBroadMatch().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Broad match "+index);
							 * keyValuePair.setValue(edmConcept.getBroadMatch().
							 * get(index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // ConceptLanguageAware custom field
							 * if(!edmConcept.getNarrowMatch().isEmpty()){
							 * for(int index = 0; index <
							 * edmConcept.getNarrowMatch().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Narrow match "+index);
							 * keyValuePair.setValue(edmConcept.getNarrowMatch()
							 * .get(index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // ConceptLanguageAware custom field
							 * if(!edmConcept.getRelatedMatch().isEmpty()){
							 * for(int index = 0; index <
							 * edmConcept.getRelatedMatch().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Related match "+index);
							 * keyValuePair.setValue(edmConcept.getRelatedMatch(
							 * ).get(index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // ConceptLanguageAware custom field
							 * if(!edmConcept.getExactMatch().isEmpty()){
							 * for(int index = 0; index <
							 * edmConcept.getExactMatch().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Exact match "+index);
							 * keyValuePair.setValue(edmConcept.getExactMatch().
							 * get(index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // ConceptLanguageAware custom field
							 * if(!edmConcept.getCloseMatch().isEmpty()){
							 * for(int index = 0; index <
							 * edmConcept.getCloseMatch().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Close match "+index);
							 * keyValuePair.setValue(edmConcept.getCloseMatch().
							 * get(index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // ConceptLanguageAware custom field
							 * if(!edmConcept.getNote().isEmpty()){ for(int
							 * index = 0; index < edmConcept.getNote().size();
							 * index++){ if (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Note "+index);
							 * keyValuePair.setValue(edmConcept.getNote().get(
							 * index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 */

							/*
							 * if (edmConcept.getNotation().size() > 0) {
							 * List<String> notationLiteralValues = new
							 * ArrayList<String>(); List<String>
							 * notationDatatypeValues = new ArrayList<String>();
							 * 
							 * notationLiteralValues.addAll(edmConcept.
							 * getNotation().values()); for (String datatype :
							 * edmConcept.getNotation().keySet()) { if
							 * (interrupted) { break mainloop; }
							 * notationDatatypeValues.add(datatype); }
							 * 
							 * // ConceptLanguageAware custom field for(int
							 * index = 0; index < notationLiteralValues.size();
							 * index++){ if (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Notation literal value "
							 * +index);
							 * keyValuePair.setValue(notationLiteralValues.get(
							 * index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); }
							 * 
							 * // ConceptLanguageAware custom field for(int
							 * index = 0; index < notationDatatypeValues.size();
							 * index++){ if (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Notation datatype value "
							 * +index);
							 * keyValuePair.setValue(notationDatatypeValues.get(
							 * index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); }
							 * 
							 * }
							 * 
							 * if(!edmConcept.getInScheme().isEmpty()){ for(int
							 * index = 0; index <
							 * edmConcept.getInScheme().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("inScheme "+index);
							 * keyValuePair.setValue(edmConcept.getInScheme().
							 * get(index));
							 * conceptLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 */

						}

						if (edmConcept.getAbout() != null && edmConcept.getAbout().length() > 0) {
							conceptLanguageNonAware.getCustomFields()
									.add(new KeyValuePair("about", edmConcept.getAbout()));
						}

						if(!listConceptLanguageAware.isEmpty()){
							concept.setLanguageNonAwareFields(conceptLanguageNonAware);
							concept.setLanguageAwareFields(listConceptLanguageAware);
							conceptsList.add(concept);

							conceptLanguageAware = null;
							conceptLanguageNonAware = null;
							concept = null;
							listConceptLanguageAware = null;

						}												
					} else {
						// warning no pref label
						warningCount++;
						warningMessages
								.add("WARNING: No preferredLabel field for ConceptLanguageAware, which is required.");
					}

				}

			}

			// TIMESPAN MAPPING
			TimeSpan timespan;
			List<TimeSpan> timespansList = new ArrayList<TimeSpan>();
			TimeSpanLangaugeAware timespanLanguageAware;
			TimeSpanLanguageNonAware timespanLanguageNonAware;
			List<TimeSpanLangaugeAware> listTimeSpanLanguageAware;

			if (edmSource.getProvidedCHO().getOaiDc().getDate().size() > 0) {
				for (Map.Entry<String, List<String>> createdMap : edmSource.getProvidedCHO().getOaiDc()
						.getDate().entrySet()) {

					
					String lang = null;
					
					// if title doesn't have xml:lang attribute neither does
					// type
					if (createdMap.getKey().length() == 2) {
						lang = createdMap.getKey();												
					} else {						
						if (language != null) {
							lang = language;
						} else {
							lang = "en";
						}																								
					}
					
					
					if (createdMap.getValue().size() > 0) {
						for (String prefL : createdMap.getValue()) {

							listTimeSpanLanguageAware = new ArrayList<TimeSpanLangaugeAware>();
							timespan = new TimeSpan();
							timespanLanguageNonAware = new TimeSpanLanguageNonAware();
							timespanLanguageAware = new TimeSpanLangaugeAware();								
							
							if (prefL != null && prefL.length() > 0) {
								timespanLanguageAware.setPreferredLabel(prefL);
								timespanLanguageAware.setLanguage(lang);

								timespan.setLanguageNonAwareFields(timespanLanguageNonAware);
								listTimeSpanLanguageAware.add(timespanLanguageAware);
								timespan.setLanguageAwareFields(listTimeSpanLanguageAware);
								timespansList.add(timespan);
							}
						}
					}																		
				}
			}

			if (!edmSource.getProvidedCHO().getTemporal().isEmpty()) {
				for (int index = 0; index < edmSource.getProvidedCHO().getTemporal().size(); index++) {

					listTimeSpanLanguageAware = new ArrayList<TimeSpanLangaugeAware>();
					timespan = new TimeSpan();
					timespanLanguageNonAware = new TimeSpanLanguageNonAware();
					timespanLanguageAware = new TimeSpanLangaugeAware();

					if (language != null) {
						timespanLanguageAware.setLanguage(language);
					} else {
						timespanLanguageAware.setLanguage("en");
					}
					timespanLanguageAware.setPreferredLabel(edmSource.getProvidedCHO().getTemporal().get(index));
					timespan.setLanguageNonAwareFields(timespanLanguageNonAware);
					listTimeSpanLanguageAware.add(timespanLanguageAware);
					timespan.setLanguageAwareFields(listTimeSpanLanguageAware);

					timespansList.add(timespan);

					timespanLanguageAware = null;
					timespanLanguageNonAware = null;
					timespan = null;
					listTimeSpanLanguageAware = null;

				}
			}

			if (!edmSource.getEdmTimespan().isEmpty()) {
				for (EdmTimespan edmTimeSpan : edmSource.getEdmTimespan()) {
					// check for TimeSpanLanguageAware required fields
					// prefLabel,language
					if (edmTimeSpan.getPrefLabel().size() > 0) {
						timespanLanguageNonAware = new TimeSpanLanguageNonAware();
						timespan = new TimeSpan();
						listTimeSpanLanguageAware = new ArrayList<TimeSpanLangaugeAware>();

						for (Map.Entry<String, List<String>> prefLabelMap : edmTimeSpan.getPrefLabel().entrySet()) {

							String timespanLang = null;
							
							if (prefLabelMap.getValue().size() > 0) {

								// check if key is 2 letter iso code for
								// language - attribute xml:lang
								if (prefLabelMap.getKey().length() == 2) {
									timespanLang = prefLabelMap.getKey();
								} else {
									if (language == null) {
										/*
										 * warningCount++; warningMessages.add(
										 * "WARNING: No language for Timespan");
										 * continue timespanloop;
										 */

										// default langhuage
										timespanLang = "en";
									} else {
										timespanLang = language;
									}
								}

								for (String prefLabel : prefLabelMap.getValue()) {
									timespanLanguageAware = new TimeSpanLangaugeAware();
									timespanLanguageAware.setLanguage(timespanLang);
									timespanLanguageAware.setPreferredLabel(prefLabel);

									if (edmTimeSpan.getAltLabel().size() > 0) {
										for (Map.Entry<String, List<String>> altLabelMap : edmTimeSpan.getAltLabel()
												.entrySet()) {

											// find alternative label for same
											// language (xml:lang attribute)
											if (altLabelMap.getKey().equals(prefLabelMap.getKey())) {
												for (String alt : altLabelMap.getValue()) {
													timespanLanguageAware.getAlternativeLabel().add(alt);
												}
											}
										}

										// if no xml:lang attribute
										if (timespanLanguageAware.getAlternativeLabel().size() < 1) {
											for (Map.Entry<String, List<String>> alt : edmTimeSpan.getAltLabel()
													.entrySet()) {
												for (String a : alt.getValue()) {
													timespanLanguageAware.getAlternativeLabel().add(a);
												}
											}
										}
									}
									listTimeSpanLanguageAware.add(timespanLanguageAware);									
								}
								
							}
							
							// TimeSpanLanguageAware custom field
							/*
							 * if(!edmTimeSpan.getNote().isEmpty()){ for(int
							 * index = 0; index < edmTimeSpan.getNote().size();
							 * index++){ if (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Note "+index);
							 * keyValuePair.setValue(edmTimeSpan.getNote().get(
							 * index));
							 * timespanLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // TimeSpanLanguageAware custom field
							 * if(!edmTimeSpan.getHasPart().isEmpty()){ for(int
							 * index = 0; index <
							 * edmTimeSpan.getHasPart().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Has part "+index);
							 * keyValuePair.setValue(edmTimeSpan.getHasPart().
							 * get(index));
							 * timespanLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // TimeSpanLanguageAware custom field
							 * if(!edmTimeSpan.getIsPartOf().isEmpty()){ for(int
							 * index = 0; index <
							 * edmTimeSpan.getHasPart().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Has part "+index);
							 * keyValuePair.setValue(edmTimeSpan.getHasPart().
							 * get(index));
							 * timespanLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // TimeSpanLanguageAware custom field
							 * if(!edmTimeSpan.getIsNextInSequence().isEmpty()){
							 * for(int index = 0; index <
							 * edmTimeSpan.getIsNextInSequence().size();
							 * index++){ if (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Is next in sequence "
							 * +index); keyValuePair.setValue(edmTimeSpan.
							 * getIsNextInSequence().get(index));
							 * timespanLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // TimeSpanLanguageAware custom field
							 * if(!edmTimeSpan.getSameAs().isEmpty()){ for(int
							 * index = 0; index <
							 * edmTimeSpan.getSameAs().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Same as "+index);
							 * keyValuePair.setValue(edmTimeSpan.getSameAs().get
							 * (index));
							 * timespanLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 */

						}
						// check for TimeSpanLanguageNonAware required fields
						// edm:Begin, edm:End
						if (edmTimeSpan.getBegin() != null && edmTimeSpan.getEnd() != null) {
							timespanLanguageNonAware.setBegin(edmTimeSpan.getBegin());
							timespanLanguageNonAware.setEnd(edmTimeSpan.getEnd());

							if (edmTimeSpan.getAbout() != null && edmTimeSpan.getAbout().length() > 0) {
								timespanLanguageNonAware.getCustomFields()
										.add(new KeyValuePair("about", edmTimeSpan.getAbout()));
							}

							timespan.setLanguageNonAwareFields(timespanLanguageNonAware);
							timespan.setLanguageAwareFields(listTimeSpanLanguageAware);
							timespansList.add(timespan);
							timespanLanguageAware = null;
							timespanLanguageNonAware = null;
							timespan = null;
							listTimeSpanLanguageAware = null;

						} else {
							// warning no begin,end for timespan language non
							// aware (required fields)
							warningCount++;
							warningMessages.add(
									"WARNING: No begin,end fields for TimeSpanLanguageNonAware, which are required fields.");
						}
					} else {
						// warning no pref label
						warningCount++;
						warningMessages.add(
								"WARNING: Required field is missing for TimeSpanLanguageAware object. Either preferredLabel or language");
					}
				}
			}

			// AGENT MAPPING
			Agent agent;
			List<Agent> agentsList = new ArrayList<Agent>();
			AgentLanguageAware agentLanguageAware;
			AgentLanguageNonAware agentLanguageNonAware;
			List<AgentLanguageAware> listAgentLanguageAware;

			// dcCreators are agent with role: dc:Creator
			if (!edmSource.getProvidedCHO().getOaiDc().getCreator().isEmpty()) {
				for (Entry<String, List<String>> entry : edmSource.getProvidedCHO().getOaiDc().getCreator()
						.entrySet()) {
					String lang = null;
					
					if (entry.getKey().length() == 2) {
						lang = entry.getKey();
					} else {
						if (language != null) {
							lang = language;
						} else {
							lang = "en";
						}
					}
					
					for (String prefLabel : entry.getValue()) {
						if(prefLabel != null){
							listAgentLanguageAware = new ArrayList<AgentLanguageAware>();
							agent = new Agent();
							agentLanguageNonAware = new AgentLanguageNonAware();
							agentLanguageAware = new AgentLanguageAware();

							agentLanguageAware.setLanguage(lang);
							agentLanguageAware.setPreferredLabel(prefLabel);
							
							agentLanguageNonAware.setRole("dc:Creator");
							agent.setLanguageNonAwareFields(agentLanguageNonAware);
							listAgentLanguageAware.add(agentLanguageAware);
							agent.setLanguageAwareFields(listAgentLanguageAware);
							agentsList.add(agent);
	
						}						
					}
				}
			}

			if (!edmSource.getEdmAgent().isEmpty()) {
				for (EdmAgent edmAgent : edmSource.getEdmAgent()) {
					
					agentLanguageNonAware = new AgentLanguageNonAware();

					if (edmAgent.getRole() != null) {
						if (edmAgent.getRole().equals("dc:Creator")) {
							agentLanguageNonAware.setRole("dc:Creator");
						}
					}

					// check forAgentLanguageAware required fields
					// prefLabel,language
					if (edmAgent.getPrefLabel().size() > 0) {
						agent = new Agent();
						listAgentLanguageAware = new ArrayList<AgentLanguageAware>();

						for (Map.Entry<String, List<String>> agentprefLabelMap : edmAgent.getPrefLabel().entrySet()) {

							String agentLang = null;
							// check if key is 2 letter iso code for language -
							// attribute xml:lang
							if (agentprefLabelMap.getKey().length() == 2) {
								agentLang = agentprefLabelMap.getKey();
							} else {
								if (language == null) {
									/*
									 * warningCount++; warningMessages.add(
									 * "WARNING: No language for Agent");
									 * continue agentloop;
									 */

									// default language
									agentLang = "en";									
								} else {
									agentLang = language;									
								}
							}

							if (agentprefLabelMap.getValue().size() > 0) {
								for (String prefLabel : agentprefLabelMap.getValue()) {
									if(prefLabel != null){
										
										agentLanguageAware = new AgentLanguageAware();
										
										agentLanguageAware.setLanguage(agentLang);
										agentLanguageAware.setPreferredLabel(prefLabel);

										if (edmAgent.getAltLabel().size() > 0) {
											for (Map.Entry<String, List<String>> agentaltLabelMap : edmAgent.getAltLabel()
													.entrySet()) {

												// find alternative label for same
												// language (xml:lang attribute)
												if (agentaltLabelMap.getKey().equals(agentprefLabelMap.getKey())) {
													for (String alt : agentaltLabelMap.getValue()) {
														agentLanguageAware.getAlternativeLabel().add(alt);
													}
												}
											}
											// if no xml:lang attribute
											if (agentLanguageAware.getAlternativeLabel().size() < 1) {
												for (Map.Entry<String, List<String>> alt : edmAgent.getAltLabel()
														.entrySet()) {
													for (String a : alt.getValue()) {
														agentLanguageAware.getAlternativeLabel().add(a);
													}
												}
											}
										}

										if (!edmAgent.getIdentifier().isEmpty()) {
											agentLanguageAware.setIdentifier(edmAgent.getIdentifier());
										}

										if (!edmAgent.getBiographicalInformation().isEmpty()) {
											agentLanguageAware.setBiographicalInformation(
													edmAgent.getBiographicalInformation().get(0));
										}

										if (edmAgent.getGender() != null) {
											agentLanguageAware.setGender(edmAgent.getGender());
										}

										if (!edmAgent.getProfessionOrOccupation().isEmpty()) {
											agentLanguageAware
													.setProfessionOrOccupation(edmAgent.getProfessionOrOccupation().get(0));
										}

										if (edmAgent.getPlaceOfBirth() != null) {
											agentLanguageAware.setPlaceOfBirth(edmAgent.getPlaceOfBirth());
										}

										if (edmAgent.getPlaceOfDeath() != null) {
											agentLanguageAware.setPlaceOfDeath(edmAgent.getPlaceOfBirth());
										}

										if (!edmAgent.getSameAs().isEmpty()) {
											agentLanguageAware.setSameAs(edmAgent.getSameAs());
										}
										listAgentLanguageAware.add(agentLanguageAware);
									}
								}
							}

							// AgentLanguageAware custom field
							/*
							 * if(!edmAgent.getNote().isEmpty()){ for(int index
							 * = 0; index < edmAgent.getNote().size(); index++){
							 * if (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Note "+index);
							 * keyValuePair.setValue(edmAgent.getNote().get(
							 * index));
							 * agentLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // AgentLanguageAware custom field
							 * if(!edmAgent.getDate().isEmpty()){ for(int index
							 * = 0; index < edmAgent.getDate().size(); index++){
							 * if (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Date "+index);
							 * keyValuePair.setValue(edmAgent.getDate().get(
							 * index));
							 * agentLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // AgentLanguageAware custom field
							 * if(!edmAgent.getHasPart().isEmpty()){ for(int
							 * index = 0; index < edmAgent.getHasPart().size();
							 * index++){ if (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Has part "+index);
							 * keyValuePair.setValue(edmAgent.getHasPart().get(
							 * index));
							 * agentLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // AgentLanguageAware custom field
							 * if(!edmAgent.getIsPartOf().isEmpty()){ for(int
							 * index = 0; index < edmAgent.getIsPartOf().size();
							 * index++){ if (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Is part of "+index);
							 * keyValuePair.setValue(edmAgent.getIsPartOf().get(
							 * index));
							 * agentLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // AgentLanguageAware custom field
							 * if(!edmAgent.getName().isEmpty()){ for(int index
							 * = 0; index < edmAgent.getName().size(); index++){
							 * if (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Name "+index);
							 * keyValuePair.setValue(edmAgent.getName().get(
							 * index));
							 * agentLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // AgentLanguageAware custom field
							 * if(!edmAgent.getHasMet().isEmpty()){ for(int
							 * index = 0; index < edmAgent.getHasMet().size();
							 * index++){ if (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Has met "+index);
							 * keyValuePair.setValue(edmAgent.getHasMet().get(
							 * index));
							 * agentLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 * 
							 * // AgentLanguageAware custom field
							 * if(!edmAgent.getIsRelatedTo().isEmpty()){ for(int
							 * index = 0; index <
							 * edmAgent.getIsRelatedTo().size(); index++){ if
							 * (interrupted) { break mainloop; }
							 * 
							 * keyValuePair = new KeyValuePair();
							 * keyValuePair.setKey("Is related to "+index);
							 * keyValuePair.setValue(edmAgent.getIsRelatedTo().
							 * get(index));
							 * agentLanguageAware.getCustomFields().add(
							 * keyValuePair); } }
							 */

						}

						if (edmAgent.getDateOfTermination() != null) {
							agentLanguageNonAware.setDateOfTermination(edmAgent.getDateOfTermination());
						}

						if (edmAgent.getDateOfEstablishment() != null) {
							agentLanguageNonAware.setDateOfEstablishment(edmAgent.getDateOfEstablishment());
						}

						if (edmAgent.getDateOfBirth() != null) {
							agentLanguageNonAware.setDateOfBirth(edmAgent.getDateOfBirth());
						} else if (edmAgent.getBegin() != null) {
							agentLanguageNonAware.setDateOfBirth(edmAgent.getBegin());
						}

						if (edmAgent.getDateOfDeath() != null) {
							agentLanguageNonAware.setDateOfDeath(edmAgent.getDateOfDeath());
						} else if (edmAgent.getEnd() != null) {
							agentLanguageNonAware.setDateOfDeath(edmAgent.getEnd());
						}

						if (edmAgent.getAbout() != null && edmAgent.getAbout().length() > 0) {
							agentLanguageNonAware.getCustomFields().add(new KeyValuePair("about", edmAgent.getAbout()));
						}

						if(!listAgentLanguageAware.isEmpty()){
							agent.setLanguageNonAwareFields(agentLanguageNonAware);
							agent.setLanguageAwareFields(listAgentLanguageAware);
							agentsList.add(agent);

							agentLanguageAware = null;
							agentLanguageNonAware = null;
							agent = null;
							listAgentLanguageAware = null;
	
						}												
					} else {
						// warning no pref label
						warningCount++;
						warningMessages.add(
								"WARNING: Required field is missing for TimeSpanLanguageAware object. Either preferredLabel or language");
					}
				}
			}

			// PLACE MAPPING
			Place place;
			List<Place> placeList = new ArrayList<Place>();
			PlaceLanguageAware placeLanguageAware;
			PlaceLanguageNonAware placeLanguageNonAware;
			List<PlaceLanguageAware> listPlaceLanguageAware;

			if (!edmSource.getProvidedCHO().getSpatial().isEmpty()) {
				for (int index = 0; index < edmSource.getProvidedCHO().getSpatial().size(); index++) {

					String prefLabel = edmSource.getProvidedCHO().getSpatial().get(index);
					
					if(prefLabel != null){
						placeLanguageNonAware = new PlaceLanguageNonAware();
						listPlaceLanguageAware = new ArrayList<PlaceLanguageAware>();
						place = new Place();
						placeLanguageAware = new PlaceLanguageAware();

						if (language != null) {
							placeLanguageAware.setLanguage(language);
						} else {
							placeLanguageAware.setLanguage("en");
						}
						
						placeLanguageAware.setPreferredLabel(prefLabel);
						place.setLanguageNonAwareFields(placeLanguageNonAware);
						listPlaceLanguageAware.add(placeLanguageAware);
						place.setLanguageAwareFields(listPlaceLanguageAware);
						placeList.add(place);

						placeLanguageAware = null;
						placeLanguageNonAware = null;
						place = null;
						listPlaceLanguageAware = null;	
					}										
				}
			}

			if (edmSource.getProvidedCHO().getCurrentLocation() != null) {

				placeLanguageNonAware = new PlaceLanguageNonAware();
				listPlaceLanguageAware = new ArrayList<PlaceLanguageAware>();
				place = new Place();
				placeLanguageAware = new PlaceLanguageAware();

				if (language != null) {
					placeLanguageAware.setLanguage(language);
				} else {
					placeLanguageAware.setLanguage("en");
				}
				try{
				
					GeoLocationServiceHelper geoLocationServiceHelper = new GeoLocationServiceHelper();
					Map<String, String> locationDetails = geoLocationServiceHelper
							.getLocationDetails(edmSource.getProvidedCHO().getCurrentLocation(), language);

					if (locationDetails.containsKey("locationName")) {
						placeLanguageAware.setPreferredLabel(locationDetails.get("locationName"));
						listPlaceLanguageAware.add(placeLanguageAware);
						place.setLanguageAwareFields(listPlaceLanguageAware);

						if (locationDetails.containsKey("latitude") && locationDetails.containsKey("longitude")) {
							placeLanguageNonAware.setLatitude(Double.parseDouble(locationDetails.get("latitude")));
							placeLanguageNonAware.setLongitude(Double.parseDouble(locationDetails.get("longitude")));
							place.setLanguageNonAwareFields(placeLanguageNonAware);
							placeList.add(place);
						}
					}
					
				}catch(Exception e){
					
				}
			}

			if (!edmSource.getEdmPlace().isEmpty()) {
				for (EdmPlace edmPlace : edmSource.getEdmPlace()) {

					// check for PlaceLanguageAware required fields
					// prefLabel,language
					if (edmPlace.getPrefLabel().size() > 0) {
						placeLanguageNonAware = new PlaceLanguageNonAware();
						place = new Place();
						listPlaceLanguageAware = new ArrayList<PlaceLanguageAware>();

						for (Map.Entry<String, List<String>> placeprefLabelMap : edmPlace.getPrefLabel().entrySet()) {

							String placeLang = null;
							
							// check if key is 2 letter iso code for language -
							// attribute xml:lang
							if (placeprefLabelMap.getKey().length() == 2) {
								placeLang = placeprefLabelMap.getKey();
							} else {
								if (language == null) {
									/*
									 * warningCount++; warningMessages.add(
									 * "WARNING: No language for Place");
									 * continue placeloop;
									 */

									// default language
									placeLang = "en";
								} else {
									placeLang = language;									
								}
							}

							if (placeprefLabelMap.getValue().size() > 0) {
								for (String prefL : placeprefLabelMap.getValue()) {
									placeLanguageAware = new PlaceLanguageAware();
									
									placeLanguageAware.setLanguage(placeLang);
									placeLanguageAware.setPreferredLabel(prefL);

									if (edmPlace.getAltLabel().size() > 0) {
										for (Map.Entry<String, List<String>> placealtLabelMap : edmPlace.getAltLabel()
												.entrySet()) {

											// find alternative label for same
											// language (xml:lang attribute)
											if (placealtLabelMap.getKey().equals(placeprefLabelMap.getKey())) {
												for (String placeAlt : placealtLabelMap.getValue()) {
													placeLanguageAware.getAlternativeLabel().add(placeAlt);
												}
											}
										}

										// if no xml:lang attribute
										if (placeLanguageAware.getAlternativeLabel().size() < 1) {
											for (Map.Entry<String, List<String>> alt : edmPlace.getAltLabel()
													.entrySet()) {
												for (String a : alt.getValue()) {
													placeLanguageAware.getAlternativeLabel().add(a);
												}
											}
										}
									}									
									listPlaceLanguageAware.add(placeLanguageAware);
								}
							}							
//							// PlaceLanguageAware custom field
//							/*
//							 * if(!edmPlace.getHasPart().isEmpty()){ for(int
//							 * index = 0; index < edmPlace.getHasPart().size();
//							 * index++){ if (interrupted) { break mainloop; }
//							 * 
//							 * keyValuePair = new KeyValuePair();
//							 * keyValuePair.setKey("Has part "+index);
//							 * keyValuePair.setValue(edmPlace.getHasPart().get(
//							 * index));
//							 * placeLanguageAware.getCustomFields().add(
//							 * keyValuePair); } }
//							 * 
//							 * 
//							 * // PlaceLanguageAware custom field
//							 * if(!edmPlace.getIsPartOf().isEmpty()){ for(int
//							 * index = 0; index < edmPlace.getIsPartOf().size();
//							 * index++){ if (interrupted) { break mainloop; }
//							 * 
//							 * keyValuePair = new KeyValuePair();
//							 * keyValuePair.setKey("Is part of "+index);
//							 * keyValuePair.setValue(edmPlace.getIsPartOf().get(
//							 * index));
//							 * placeLanguageAware.getCustomFields().add(
//							 * keyValuePair); } }
//							 * 
//							 * // PlaceLanguageAware custom field
//							 * if(!edmPlace.getIsNextInSequence().isEmpty()){
//							 * for(int index = 0; index <
//							 * edmPlace.getIsNextInSequence().size(); index++){
//							 * if (interrupted) { break mainloop; }
//							 * 
//							 * keyValuePair = new KeyValuePair();
//							 * keyValuePair.setKey("Is next in sequence "
//							 * +index); keyValuePair.setValue(edmPlace.
//							 * getIsNextInSequence().get(index));
//							 * placeLanguageAware.getCustomFields().add(
//							 * keyValuePair); } }
//							 * 
//							 * // PlaceLanguageAware custom field
//							 * if(!edmPlace.getSameAs().isEmpty()){ for(int
//							 * index = 0; index < edmPlace.getSameAs().size();
//							 * index++){ if (interrupted) { break mainloop; }
//							 * 
//							 * keyValuePair = new KeyValuePair();
//							 * keyValuePair.setKey("Same as "+index);
//							 * keyValuePair.setValue(edmPlace.getSameAs().get(
//							 * index));
//							 * placeLanguageAware.getCustomFields().add(
//							 * keyValuePair); } }
//							 * 
//							 * if (edmPlace.getNote().size() > 0) { for
//							 * (Map.Entry<String, String> placeNoteMap :
//							 * edmPlace.getNote() .entrySet()) { if
//							 * (interrupted) { break mainloop; }
//							 * 
//							 * //find note for same language (xml:lang
//							 * attribute) if(placeNoteMap.getKey() ==
//							 * placeprefLabelMap.getKey()){ keyValuePair = new
//							 * KeyValuePair(); keyValuePair.setKey("Note");
//							 * keyValuePair.setValue(placeNoteMap.getValue());
//							 * placeLanguageAware.getCustomFields().add(
//							 * keyValuePair); } } }
//							 */
						}

						if (edmPlace.getAltitude() != null)
							placeLanguageNonAware.setAltitude((double) edmPlace.getAltitude());
						if (edmPlace.getLatitude() != null)
							placeLanguageNonAware.setLatitude((double) edmPlace.getLatitude());
						if (edmPlace.getLongitude() != null)
							placeLanguageNonAware.setLongitude((double) edmPlace.getLongitude());

						if (edmPlace.getAbout() != null && edmPlace.getAbout().length() > 0) {
							placeLanguageNonAware.getCustomFields().add(new KeyValuePair("about", edmPlace.getAbout()));
						}

						place.setLanguageNonAwareFields(placeLanguageNonAware);
						place.setLanguageAwareFields(listPlaceLanguageAware);
						placeList.add(place);

						placeLanguageAware = null;
						placeLanguageNonAware = null;
						place = null;
						listPlaceLanguageAware = null;

					} else {
						// warning no pref label
						warningCount++;
						warningMessages.add(
								"WARNING: Required field is missing for PlaceLanguageAware object. Either preferredLabel or language");
					}
				}
			}
			
			// WEBLINK MAPPING
			WebLink weblink;
			if (!edmSource.getEdmWebResource().isEmpty()) {
				for (EdmWebResource edmWebResource : edmSource.getEdmWebResource()) {

					weblink = new WebLink();

					if (edmWebResource.getLink() != null
							&& (edmWebResource.getEdmRights() != null || edmWebResource.getRights().size() > 0)
							&& edmWebResource.getType().size() > 0) {

						// if weblinks list already contains that link
						if (weblinksList.stream().anyMatch(w -> w.getLink().equals(edmWebResource.getLink()))) {
							continue;
						}

						weblink.setLink(edmWebResource.getLink());
						String webResType = edmWebResource.getType().get(0);
						switch (webResType) {

						case "webpage":
							weblink.setType(
									eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink.TypeEnum.LANDING_PAGE);
							break;
						case "direct":
							weblink.setType(
									eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink.TypeEnum.DIRECT_MEDIA);
							break;
						case "preview":
							weblink.setType(
									eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink.TypeEnum.PREVIEW_SOURCE);
							break;
						case "other":
							weblink.setType(eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink.TypeEnum.OTHER);
							break;
						default:
							weblink.setType(
									eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink.TypeEnum.LANDING_PAGE);
							break;
						}

						if (edmWebResource.getEdmRights() != null) {
							weblink.setRights(edmWebResource.getEdmRights());
						}

						if (weblink.getRights() == null) {
							if (edmWebResource.getRights().size() > 0) {
								weblink.setRights(edmWebResource.getRights().get(0));
							}
						}
						/*
						 * if(!edmWebResource.getSameAs().isEmpty()){ for(int
						 * index = 0; index < edmWebResource.getSameAs().size();
						 * index++){ if (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Same as "+index);
						 * keyValuePair.setValue(edmWebResource.getSameAs().get(
						 * index)); weblink.getCustomFields().add(keyValuePair);
						 * } }
						 * 
						 * if(!edmWebResource.getIsNextInSequence().isEmpty()){
						 * for(int index = 0; index <
						 * edmWebResource.getIsNextInSequence().size();
						 * index++){ if (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Is next in sequence "+index);
						 * keyValuePair.setValue(edmWebResource.
						 * getIsNextInSequence().get(index));
						 * weblink.getCustomFields().add(keyValuePair); } }
						 * 
						 * if(!edmWebResource.getIssued().isEmpty()){ for(int
						 * index = 0; index < edmWebResource.getIssued().size();
						 * index++){ if (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Issued "+index);
						 * keyValuePair.setValue(edmWebResource.getIssued().get(
						 * index)); weblink.getCustomFields().add(keyValuePair);
						 * } }
						 */

						if (!edmWebResource.getIsReferencedBy().isEmpty()) {
							for (int index = 0; index < edmWebResource.getIsReferencedBy().size(); index++) {

								keyValuePair = new KeyValuePair();
								keyValuePair.setKey("isReferencedBy");
								keyValuePair.setValue(edmWebResource.getIsReferencedBy().get(index));
								weblink.getCustomFields().add(keyValuePair);
							}
						}

						/*
						 * if(!edmWebResource.getIsPartOf().isEmpty()){ for(int
						 * index = 0; index <
						 * edmWebResource.getIsPartOf().size(); index++){ if
						 * (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Is part of "+index);
						 * keyValuePair.setValue(edmWebResource.getIsPartOf().
						 * get(index));
						 * weblink.getCustomFields().add(keyValuePair); } }
						 * 
						 * if(!edmWebResource.getIsFormatOf().isEmpty()){
						 * for(int index = 0; index <
						 * edmWebResource.getIsFormatOf().size(); index++){ if
						 * (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Is format of "+index);
						 * keyValuePair.setValue(edmWebResource.getIsFormatOf().
						 * get(index));
						 * weblink.getCustomFields().add(keyValuePair); } }
						 * 
						 * if(!edmWebResource.getHasPart().isEmpty()){ for(int
						 * index = 0; index <
						 * edmWebResource.getHasPart().size(); index++){ if
						 * (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Has part "+index);
						 * keyValuePair.setValue(edmWebResource.getHasPart().get
						 * (index));
						 * weblink.getCustomFields().add(keyValuePair); } }
						 * 
						 * if(!edmWebResource.getExtent().isEmpty()){ for(int
						 * index = 0; index < edmWebResource.getExtent().size();
						 * index++){ if (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Extent "+index);
						 * keyValuePair.setValue(edmWebResource.getExtent().get(
						 * index)); weblink.getCustomFields().add(keyValuePair);
						 * } }
						 * 
						 * if(!edmWebResource.getCreated().isEmpty()){ for(int
						 * index = 0; index <
						 * edmWebResource.getCreated().size(); index++){ if
						 * (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Created "+index);
						 * keyValuePair.setValue(edmWebResource.getCreated().get
						 * (index));
						 * weblink.getCustomFields().add(keyValuePair); } }
						 * 
						 * if(!edmWebResource.getConformsTo().isEmpty()){
						 * for(int index = 0; index <
						 * edmWebResource.getConformsTo().size(); index++){ if
						 * (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Conforms to "+index);
						 * keyValuePair.setValue(edmWebResource.getConformsTo().
						 * get(index));
						 * weblink.getCustomFields().add(keyValuePair); } }
						 * 
						 * if(!edmWebResource.getSource().isEmpty()){ for(int
						 * index = 0; index < edmWebResource.getSource().size();
						 * index++){ if (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Source "+index);
						 * keyValuePair.setValue(edmWebResource.getSource().get(
						 * index)); weblink.getCustomFields().add(keyValuePair);
						 * } }
						 * 
						 * if (edmWebResource.getFormat().size() > 0) { int
						 * counter = 0; for (Map.Entry<String, String> formatMap
						 * : edmWebResource.getFormat() .entrySet()) { if
						 * (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Format "+(counter++));
						 * keyValuePair.setValue(formatMap.getValue());
						 * weblink.getCustomFields().add(keyValuePair); } }
						 * 
						 * if (edmWebResource.getDescription().size() > 0) { int
						 * counter = 0; for (Map.Entry<String, String> descMap :
						 * edmWebResource.getDescription() .entrySet()) { if
						 * (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Description "+(counter++));
						 * keyValuePair.setValue(descMap.getValue());
						 * weblink.getCustomFields().add(keyValuePair); } }
						 * 
						 * if (edmWebResource.getCreator().size() > 0) { int
						 * counter = 0; for (Map.Entry<String, String>
						 * creatorMap : edmWebResource.getCreator() .entrySet())
						 * { if (interrupted) { break mainloop; }
						 * 
						 * keyValuePair = new KeyValuePair();
						 * keyValuePair.setKey("Creator "+(counter++));
						 * keyValuePair.setValue(creatorMap.getValue());
						 * weblink.getCustomFields().add(keyValuePair); } }
						 */

						weblinksList.add(weblink);
						weblink = null;
					} else {
						warningCount++;
						warningMessages
								.add("WARNING: One of the required fields(link,rights,type) for WebLink is missing.");
					}
				}
			}

			culturalHeritageObject.setLanguageAwareFields(culturalHeritageObjectLanguageAwareList);
			culturalHeritageObject.setLanguageNonAwareFields(culturalHeritageObjectLanguageNonAware);

			if (conceptsList.size() > 0) {
				culturalHeritageObject.setConcepts(conceptsList);
			}
			if (timespansList.size() > 0) {
				culturalHeritageObject.setTemporal(timespansList);
			}
			if (agentsList.size() > 0) {
				culturalHeritageObject.setAgents(agentsList);
			}
			if (placeList.size() > 0) {				
				culturalHeritageObject.setSpatial(placeList);
			}
			if (weblinksList.size() > 0) {
				culturalHeritageObject.setWebLinks(weblinksList);
			}

		} catch (Exception e) {
			logger.error("Edm mapping exception: " + e.getMessage(), e);
		}

		return culturalHeritageObject;
	}

	@Override
	public int getErrors() {
		return errorCount;
	}

	@Override
	public void setInterrupt(boolean interrupt) {
		interrupted = interrupt;
	}

	@Override
	public int getSuccesses() {
		return culturalHeritageObjects.size();
	}

	@Override
	public int getWarnings() {
		return warningCount;
	}

	@Override
	public List<String> getErrorMessages() {
		return errorMessages;
	}

	@Override
	public List<String> getWarningMessages() {
		return warningMessages;
	}

}
