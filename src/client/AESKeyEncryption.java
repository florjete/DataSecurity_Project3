package client;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.Base64;

public class AESKeyEncryption {

    private final SecretKey aesKey;
    private final PublicKey serverPublicKey;
    private String encryptedAESKey;

    public AESKeyEncryption(SecretKey aesKey, PublicKey serverPublicKey) {
        this.aesKey = aesKey;
        this.serverPublicKey = serverPublicKey;
        this.encryptedAESKey = encryptAESKey();
    }

    // Enkriptimi i AES Key me RSA Public Key të serverit
    private String encryptAESKey() {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
            byte[] encryptedBytes = cipher.doFinal(aesKey.getEncoded());
            System.out.println("🔒 AES Key u enkriptua me sukses!");
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("❌ Gabim gjatë enkriptimit të AES Key: " + e.getMessage());
            return null;
        }
    }

    // Kthen AES Key të enkriptuar në formë të koduar Base64 për dërgim
    public String getEncryptedAESKey() {
        return encryptedAESKey;
    }
}
