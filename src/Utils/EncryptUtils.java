package Utils;

import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils {
	public static String secretKeyToString(SecretKey sk) {
		String encodedKey = Base64.getEncoder().encodeToString(sk.getEncoded());
		return encodedKey;
	}
	
	public static SecretKey stringToSecretKey(String s) {
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(s);
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES"); 
		return originalKey;
	}
	
	public static void generateSecretKeyFile(Path path) {
		SecretKey sk = generateNewSecretKey();
		FileUtils.saveBytes(path, sk.getEncoded());
	}
	
	public static SecretKey readSecretKeyFile(Path path) {
		byte[] decodedKey = FileUtils.readBytes(path);
		
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES"); 
		return originalKey;
	}
	
	public static SecretKey generateNewSecretKey() {
		SecretKey secretKey = null;
		try {
			secretKey = KeyGenerator.getInstance("DES").generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return secretKey;		
	}
	
	public static byte[] encrypt(String s, SecretKey sk) {
		try{
            Cipher desCipher;
            desCipher = Cipher.getInstance("DES");


            byte[] text = s.getBytes("UTF8");


            desCipher.init(Cipher.ENCRYPT_MODE, sk);
            byte[] textEncrypted = desCipher.doFinal(text);

            return textEncrypted;
            //String encS = new String(textEncrypted);
            //return encS;

        }catch(Exception e)
        {
        	e.printStackTrace();
        }
		return null;
	}
	
	public static String decrypt(byte[] sBytes, SecretKey sk) {
        try{
        	Cipher desCipher;
            desCipher = Cipher.getInstance("DES");
            
            //byte[] sBytes = s.getBytes("UTF8");
            
            desCipher.init(Cipher.DECRYPT_MODE, sk);
            byte[] textDecrypted = desCipher.doFinal(sBytes);

            String decS = new String(textDecrypted);
            return decS;
        }catch(Exception e)
        {
        }
        return null;
	}
}
