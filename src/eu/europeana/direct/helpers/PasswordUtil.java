package eu.europeana.direct.helpers;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

	public static Map<String, String> generatePasswordValues(String password) {
		Map<String,String> passwordInformation = new HashMap<String,String>();
		
		try{
			byte[] salt = generateSalt();
		    
			String saltString = new String(salt);
			String saltedHashPassword = new String(getEncryptedPassword(password, salt));
			if(saltString != null && saltString.length() > 0 && saltedHashPassword != null && saltedHashPassword.length() > 0){
				passwordInformation.put("salt", saltString);
				passwordInformation.put("saltedhashPassword", saltedHashPassword);	
			}				
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return passwordInformation;
	}
	
	public static byte[] getEncryptedPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		if(password != null && password.length() > 0){
			String algorithm = "PBKDF2WithHmacSHA256";
			// Pick an iteration count that works for you. The NIST recommends at
			// least 1,000 iterations:
			// http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
			// iOS 4.x reportedly uses 10,000:
			// http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
			int iterations = 20000;
			
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);

			SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

			return f.generateSecret(spec).getEncoded();	
		} else {
			return null;
		}
	}

	private static byte[] generateSalt() throws NoSuchAlgorithmException {
		// VERY important to use SecureRandom instead of just Random
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

		// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
		byte[] salt = new byte[8];

		random.nextBytes(salt);

		return salt;
	}

}
