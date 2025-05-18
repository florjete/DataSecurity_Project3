package server;

import java.security.*;

public class KeyManager {
    private final KeyPair keyPair;

    public KeyManager() {
        try {
            System.out.println("🔑 Gjenerimi i çifteve të kyçeve RSA për serverin...");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            this.keyPair = keyGen.generateKeyPair();
            System.out.println("✅ Çelësat RSA u gjeneruan me sukses!");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("❌ Gabim gjatë gjenerimit të çelësave RSA: " + e.getMessage());
        }
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }
}
