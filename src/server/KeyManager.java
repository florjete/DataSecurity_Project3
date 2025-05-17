package server;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.spec.OAEPParameterSpec;


public class KeyManager {

    private KeyPair rsaKeyPair;

    public KeyManager() {
        try {
            // 🔑 Gjenerimi i çifteve të kyçeve RSA (2048-bit)
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            this.rsaKeyPair = keyGen.generateKeyPair();
            System.out.println("✅ Çiftet e kyçeve RSA u gjeneruan me sukses!");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("❌ Gabim gjatë gjenerimit të kyçeve: " + e.getMessage());
        }
    }

    // ➡️ Kyçi publik i serverit
    public PublicKey getPublicKey() {
        return rsaKeyPair.getPublic();
    }

    // ➡️ Kyçi privat i serverit
    public PrivateKey getPrivateKey() {
        return rsaKeyPair.getPrivate();
    }

    public byte[] decryptWithPrivateKey(byte[] data) throws Exception {
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsa.init(Cipher.DECRYPT_MODE, rsaKeyPair.getPrivate());
        return rsa.doFinal(data);
    }
}
