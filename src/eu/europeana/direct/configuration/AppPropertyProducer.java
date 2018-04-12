package eu.europeana.direct.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Singelton object for reading properties from WildFly configuration file (application.properties)
 * 
 * @author nino.berke
 *
 */
@SuppressWarnings("rawtypes")
public class AppPropertyProducer {

	final static Logger logger = Logger.getLogger(AppPropertyProducer.class);
	private static AppPropertyProducer instance = null;
    private static Map<String, String> properties = new HashMap<>();

	
	private AppPropertyProducer(){}
	
	public static final synchronized AppPropertyProducer getInstance() {

		if (instance == null) {
			//read properties from wildfly properties file
			readProperties();
			instance = new AppPropertyProducer();
		}
		return instance;
	}
	
	/***
	 * Retrieves value of property name (key) from WildFly configuration file (application property)
	 * @param propertyName Name of property
	 * @return Value of property
	 */
	public synchronized String getPropertyByName(String propertyName){
		if(!properties.isEmpty()){
			if(properties.containsKey(propertyName)){
				return properties.get(propertyName);
			}
		}
		return null;
	}
	
	/***
	 * Reads all properties (key-value) from WildFly configuration file (application.properties)
	 */
	private static void readProperties(){
		
		 //matches the property name as defined in the system-properties element in WildFly
        String propertyFile = System.getProperty("application.properties");
        File file = new File(propertyFile);
        Properties props = new Properties();
          
        try {
        	props.load(new FileInputStream(file));
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        	e.printStackTrace();
        }
          
        HashMap hashMap = new HashMap<>(props);
        properties.putAll(hashMap);
	}
	
}
