package eu.europeana.direct.backend.userTypes;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom jsonb usertype for postgresql
 * @author nino.berke
 *
 */
public class JSONBType extends MutableUserType implements ParameterizedType {

    public static final String MAP_TYPE = "MAP";
    public static final String DOUBLE_MAP_TYPE = "DOUBLE_MAP";    
    public static final String MAP_LIST_TYPE = "MAP_LIST";    
    private static final int[] SQL_TYPES = new int[]{Types.JAVA_OBJECT};
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();    
    private static final String JSONB_TYPE = "jsonb";    
    
    private JavaType valueType = null;
    private Class<?> classType = null;

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
    public Class<?> returnedClass() {
        return classType;
    }            	
    
    @Override
	public Object deepCopy(Object value) throws HibernateException {
    	if (value == null) {
            return null;
        } else if (valueType.isMapLikeType()) {      
			return deepCopyMap(value);                	               
        } 
    	
		return value;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {		
		try {
			final String json = rs.getString(names[0]);						
        	return json == null ? null : OBJECT_MAPPER.readValue(json,valueType);			
		} catch (IOException ex) {			
			throw new HibernateException(ex);
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException{
		try {									
			if(value != null){
				final String json = value == null ? null : OBJECT_MAPPER.writeValueAsString(value);
				// otherwise PostgreSQL won't recognize the type			
				PGobject pgo = new PGobject();
				pgo.setType(JSONB_TYPE);
				pgo.setValue(json);
				st.setObject(index, pgo, Types.OTHER);				
			}else{				
				st.setNull(index, Types.OTHER);
			}
		} catch (JsonProcessingException ex) {	
			throw new HibernateException(ex);
		} catch(Exception e){			
			e.printStackTrace();
		}
	}

	@Override
	public void setParameterValues(Properties parameters) {
		String type = parameters.getProperty("type");
		if(type != null){
			if (type.equals(MAP_TYPE)) {
				if (parameters.getProperty("value") != null) {
					try {
						// construct java type from Map<Key,Class> property
						valueType = OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class, String.class, Class.forName(parameters.getProperty("value")));
					} catch (ClassNotFoundException e) {
						throw new IllegalArgumentException("Type " + type + " is not a valid type.");
					}
				}				
			} else if(type.equals(DOUBLE_MAP_TYPE)){
				try {
					// create java type for Map<String,String>
					JavaType mapType = OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class, String.class, String.class);
					// create java type for String
					JavaType stringType = OBJECT_MAPPER.getTypeFactory().uncheckedSimpleType(String.class);					
					// construct java type from mapType and stringType so it will become JavaType -> Map<String,Map<String,String>>					
					valueType = OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class, stringType, mapType);
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}else if(type.equals(MAP_LIST_TYPE)){				
				try {
					// create java type for List<object from parameter>
					JavaType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Class.forName(parameters.getProperty("value")));
					// create java type for String
					JavaType stringType = OBJECT_MAPPER.getTypeFactory().uncheckedSimpleType(String.class);					
					// construct java type from mapType and stringType so it will become JavaType -> Map<String,List<object>>					
					valueType = OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class, stringType, listType);
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
			classType = HashMap.class;
		} else {
			// construct java type from simple property (AgentLangNonAware, ConceptLangNonAware,...)
			valueType = OBJECT_MAPPER.getTypeFactory().uncheckedSimpleType(new ClassLoaderServiceImpl().classForName(parameters.getProperty("value")));
		}		
	}
	
	public static <K, V> Map<K, V> deepCopyMap(Object object) {

		Map<K, V> original = (Map<K, V>) object;
		Map<K, V> copy = new HashMap<K, V>();
		copy.putAll(original);
		return copy;
	}
	
}

