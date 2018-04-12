package eu.europeana.direct.helpers;

import java.math.BigInteger;
import java.security.SecureRandom;

public class APIKeyGenerator {
	private static SecureRandom random = new SecureRandom();

	public static String generateAPIKey() {		
		return new BigInteger(130, random).toString(32);
	}
}
