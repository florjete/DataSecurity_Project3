package client;

import java.security.MessageDigest;

public class HashUtil {
    public static byte[] generateSHA256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }
}
