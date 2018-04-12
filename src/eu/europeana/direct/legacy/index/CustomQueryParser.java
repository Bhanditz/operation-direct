package eu.europeana.direct.legacy.index;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

import eu.europeana.direct.legacy.helpers.DateParser;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

public class CustomQueryParser extends QueryParser {
	final static Logger logger = Logger.getLogger(CustomQueryParser.class);

	private CulturalHeritageObject culturalHeritageObject = new CulturalHeritageObject();
	private DateParser dateParser = new DateParser();

	public CustomQueryParser(String f, Analyzer a) {
		super(f, a);
	}

	@Override
	protected org.apache.lucene.search.Query getRangeQuery(String field, String startValue, String endValue,
			boolean arg3, boolean arg4) throws ParseException {
						
		String originalField = field;		
		// adding "_index" to field
		if(field.contains("index")){
			originalField = field.replace("_index", "");			
		}		
				
		// search field is date type
		if (culturalHeritageObject.getDateFields().contains(originalField)) {			
			// parse both dates from string to date
			Date start = dateParser.tryParse(startValue);
			Date end = dateParser.tryParse(endValue);					
			
			if (startValue.length() > 0 && startValue != null && endValue != null && endValue.length() > 0) {				
				return LongPoint.newRangeQuery(field,start.getTime(),end.getTime());
			} else {
				logger.info("Custom query parser range query wrong date values or null.");
				return LongPoint.newRangeQuery(field, null, null);
			}
			
		// search field is numeric
		} else if (culturalHeritageObject.getNumericFields().contains(field)) {
			return DoublePoint.newRangeQuery(field, Double.parseDouble(startValue), Double.parseDouble(endValue));
		}
		return super.getRangeQuery(field, startValue, endValue, arg3, arg4);
	}

	@Override
	protected org.apache.lucene.search.Query newTermQuery(org.apache.lucene.index.Term term) {
		
		String originalField = term.field();
		// adding "_index" to field
		if(field.contains("index")){
			originalField = term.field().replace("_index", "");
		}		
		
		// search field is date type
		if (culturalHeritageObject.getDateFields().contains(originalField)) {
			// parse string date to date
			Date date = dateParser.tryParse(term.text());
			if (term.text() != null && term.text().length() > 0) {
				try{
					return LongPoint.newExactQuery(term.field(), date.getTime());
				}catch(Exception e){
					logger.info("Custom query parser exact query wrong date values or null. Exception: "+e.getMessage(),e);
					return LongPoint.newExactQuery(term.field(), 0);
				}
			} else {
				logger.info("Custom query parser exact query wrong date values or null.");
				return LongPoint.newExactQuery(term.field(), 0);
			}
		// search field is numeric
		} else if (culturalHeritageObject.getNumericFields().contains(originalField)) {
			return DoublePoint.newExactQuery(term.field(), Double.parseDouble(term.text()));
		}
		return super.newTermQuery(term);
	}

	@Override
	protected org.apache.lucene.search.Query newFieldQuery(Analyzer analyzer, String field, String queryText,
			boolean quoted) throws ParseException {
	
		String originalField = field;
		// adding "_index" to field
		if(field.contains("index")){
			originalField = field.replace("_index", "");
		}		
		
		// search field is date type
		if (culturalHeritageObject.getDateFields().contains(originalField)) {
			if (queryText != null && queryText.length() > 0) {
				try{
					return LongPoint.newExactQuery(field, Long.parseLong(queryText));
				}catch(Exception e){
					logger.info("Custom query parser exact query wrong date values or null. Exception: "+e.getMessage(),e);
					return LongPoint.newExactQuery(field, 0);
				}
			} else {
				logger.info("Custom query parser exact query wrong date values or null.");
				return LongPoint.newExactQuery(field, 0);
			}
		// search field is numeric
		} else if (culturalHeritageObject.getNumericFields().contains(originalField)) {
			try {
				return DoublePoint.newExactQuery(field, Double.parseDouble(queryText));
			} catch (Exception e) {
				logger.info("Custom query parser exact query wrong numeric values or null.");
				return DoublePoint.newExactQuery(field, 0);
			}
		}
		return super.newFieldQuery(analyzer, field, queryText, quoted);
	}
}