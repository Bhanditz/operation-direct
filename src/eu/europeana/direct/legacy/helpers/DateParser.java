package eu.europeana.direct.legacy.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 
 * Util class for Date objects
 *
 */
public class DateParser {
	final static Logger logger = Logger.getLogger(DateParser.class);

	String[] formatStrings = {"yyyy-MM-dd","dd-MM-yyyy","MM-dd-yyyy","M/y", "M/d/y","MM/dd/yyyy","MM-dd-yyyy","M-d-y","d/M/y","dd/MM/yyyy","yyyy","dd/MM","MM/dd","yyyy-MM-dd'T'HH:mm:ssZ"};

	
	/**
	 * Method checks if date can be parsed with one of the specified formats
	 * @param dateString Date
	 * @return Date date or null if can't parse with one of the specified formats
	 */
	public Date tryParse(String dateString){
		
		if(dateString.matches("\\d+")){
			long milis = Long.parseLong(dateString);
			return new Date(milis);			
		}else{
			for (String formatString : formatStrings)
		    {
		        try
		        {
		            return new SimpleDateFormat(formatString).parse(dateString);
		        }
		        catch (ParseException e) {
		        }
		    }
	
		}		
	    return null;
	}
	
	// check if date is specified only as year
	/**
	 * Method tries to parse date with specified format for year (yyyy)
	 * @param dateString Date
	 * @return Date date or null if can't parse with year (yyyy) format
	 */
	public Date tryParseYear(String dateString){
		for (String formatString : formatStrings)
	    {
	        try
	        {
	        	if(formatString != "yyyy"){
	        		return new SimpleDateFormat(formatString).parse(dateString);
	        	}
	        }
	        catch (ParseException e) {
	        }
	    }		
	    return null;
	}
}
