import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;


public class HashString {

    public static String decrypt(String str,String k) throws Exception {
        byte[] key = k.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        Cipher dcipher = Cipher.getInstance("AES");
        SecretKey aesKey = new SecretKeySpec(key, "AES");
        dcipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] dec = Base64.getDecoder().decode(str);
        byte[] utf8 = dcipher.doFinal(dec);

        return new String(utf8, StandardCharsets.UTF_8);
    }


    public static byte[] encrypt(String str,String k) throws Exception {
        byte[] key = k.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        Cipher ecipher = Cipher.getInstance("AES");
        SecretKey aeskey = new SecretKeySpec(key,"AES");
        byte[] utf8 = str.getBytes(StandardCharsets.UTF_8);
        ecipher.init(Cipher.ENCRYPT_MODE, aeskey);

        byte[] enc = ecipher.doFinal(utf8);

        return Base64.getEncoder().encode(enc);

    }
}
