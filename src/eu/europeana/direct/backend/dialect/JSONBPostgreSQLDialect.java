package eu.europeana.direct.backend.dialect;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL94Dialect;

public class JSONBPostgreSQLDialect extends PostgreSQL94Dialect {
	public JSONBPostgreSQLDialect() {
	    super();
	    registerColumnType(Types.JAVA_OBJECT, "jsonb");
	  }
}
