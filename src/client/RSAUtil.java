package client;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class RSAUtil {
    private static final String RSA_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final int MAX_ENCRYPT_BLOCK = 245;  // 245 bajt për 2048-bit çelës
    private static final int MAX_DECRYPT_BLOCK = 256;  // 256 bajt për 2048-bit çelës

    // ✅ Gjeneron një çift kyçesh (publik dhe privat)
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyGen.initialize(KEY_SIZE);
        return keyGen.generateKeyPair();
    }

    // ✅ Enkripton të dhënat me RSA dhe çelësin publik
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // 🔹 Ndarja në blloqe për të shmangur gabimet
        int inputLen = data.length;
        int offset = 0;
        byte[] encryptedData = new byte[0];

        while (inputLen - offset > 0) {
            int blockSize = Math.min(inputLen - offset, MAX_ENCRYPT_BLOCK);
            byte[] encryptedBlock = cipher.doFinal(data, offset, blockSize);
            encryptedData = concatenate(encryptedData, encryptedBlock);
            offset += blockSize;
        }

        System.out.println("🔐 Të dhënat u enkriptuan me sukses.");
        return encryptedData;
    }

    // ✅ Dekripton të dhënat me RSA dhe çelësin privat
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // 🔹 Ndarja në blloqe për të shmangur gabimet
        int inputLen = data.length;
        int offset = 0;
        byte[] decryptedData = new byte[0];

        while (inputLen - offset > 0) {
            int blockSize = Math.min(inputLen - offset, MAX_DECRYPT_BLOCK);
            byte[] decryptedBlock = cipher.doFinal(data, offset, blockSize);
            decryptedData = concatenate(decryptedData, decryptedBlock);
            offset += blockSize;
        }

        System.out.println("🔓 Të dhënat u dekriptuan me sukses.");
        return decryptedData;
    }

    // ✅ Dekriptimi i AES Key që ka qenë i enkriptuar me RSA
    public static SecretKey decryptAESKey(byte[] encryptedAESKey, PrivateKey privateKey) throws Exception {
        byte[] decryptedKey = decrypt(encryptedAESKey, privateKey);
        return new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
    }

    // ✅ Metoda për bashkimin e blloqeve të të dhënave
    private static byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    // ✅ Për ta testuar
    public static void main(String[] args) throws Exception {
        KeyPair keyPair = generateKeyPair();

        String originalData = "Ky është një mesazh testimi për RSA!";
        byte[] encryptedData = encrypt(originalData.getBytes(), keyPair.getPublic());
        System.out.println("🔐 Teksti i enkriptuar: " + Base64.getEncoder().encodeToString(encryptedData));

        byte[] decryptedData = decrypt(encryptedData, keyPair.getPrivate());
        System.out.println("🔓 Teksti i dekriptuar: " + new String(decryptedData));
    }
}
