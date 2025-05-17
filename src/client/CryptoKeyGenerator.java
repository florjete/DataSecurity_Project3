package client;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.security.PrivateKey;

public class CryptoKeyGenerator {

    private KeyPair rsaKeyPair;
    private SecretKey aesKey;

    public CryptoKeyGenerator() {
        try {
            // Gjenerimi i çifteve të kyçeve RSA (2048-bit)
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            this.rsaKeyPair = keyGen.generateKeyPair();

            // Gjenerimi i AES Key (256-bit)
            KeyGenerator aesGen = KeyGenerator.getInstance("AES");
            aesGen.init(256);
            this.aesKey = aesGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Getter për kyçet
    public PublicKey getPublicKey() {
        return rsaKeyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return rsaKeyPair.getPrivate();
    }

    public SecretKey getAESKey() {
        return aesKey;
    }
}
