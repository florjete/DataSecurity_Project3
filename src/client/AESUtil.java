package client;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Arrays;

public class AESUtil {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    // Gjeneron çelës AES 256-bit
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // 256-bit key
        return keyGen.generateKey();
    }
    // ✅ Metoda për dekriptimin e AES Key me RSA Private Key
    public static SecretKey decryptAESKey(byte[] encryptedAESKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // Dekriptojmë AES Key
        byte[] decryptedKey = cipher.doFinal(encryptedAESKey);

        // Krijojmë një objekt SecretKey nga të dhënat e dekriptuara
        return new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
    }

    // Enkriptimi i të dhënave me AES/GCM
    public static byte[] encrypt(byte[] key, byte[] data) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        byte[] encryptedData = cipher.doFinal(data);

        // Kombinimi i IV dhe të dhënave të enkriptuara
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

        System.out.println("IV gjatë enkriptimit: " + Arrays.toString(iv));

        return combined;
    }

    // Dekriptimi i të dhënave me AES/GCM
    public static byte[] decrypt(byte[] key, byte[] encryptedData) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(encryptedData, 0, iv, 0, iv.length);

        byte[] encryptedBytes = new byte[encryptedData.length - iv.length];
        System.arraycopy(encryptedData, iv.length, encryptedBytes, 0, encryptedBytes.length);

        // Shto debug për të parë IV-në gjatë dekriptimit
        System.out.println("IV gjatë dekriptimit: " + Arrays.toString(iv));
        System.out.println("Gjatësia e të dhënave të enkriptuara: " + encryptedBytes.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        return cipher.doFinal(encryptedBytes);
    }
}
