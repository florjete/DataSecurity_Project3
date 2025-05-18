import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class AESFileManager {

    // Gjenerimi i çelësit AES
    public static SecretKey generateAESKey(int keySize) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize); // 128, 192, 256
        return keyGen.generateKey();
    }

    // Enkriptimi i file-it
    public static void encryptFile(File inputFile, File outputFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] iv = new byte[16];
        SecureRandom.getInstanceStrong().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            fos.write(iv); // ruajmë IV në fillim

            byte[] buffer = new byte[1024];
            byte[] encrypted;
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                encrypted = cipher.update(buffer, 0, bytesRead);
                if (encrypted != null) fos.write(encrypted);
            }

            encrypted = cipher.doFinal();
            if (encrypted != null) fos.write(encrypted);
        }
    }

    // Dekriptimi i file-it
    public static void decryptFile(File inputFile, File outputFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        try (FileInputStream fis = new FileInputStream(inputFile)) {
            byte[] iv = new byte[16];
            fis.read(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                byte[] decrypted;
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    decrypted = cipher.update(buffer, 0, bytesRead);
                    if (decrypted != null) fos.write(decrypted);
                }

                decrypted = cipher.doFinal();
                if (decrypted != null) fos.write(decrypted);
            }
        }
    }

    // Fragmentimi i file-it në chunks
    public static void splitFile(File file, int chunkSize) throws IOException {
        int partCounter = 1;
        byte[] buffer = new byte[chunkSize];

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int bytesAmount;
            while ((bytesAmount = bis.read(buffer)) > 0) {
                String filePartName = file.getName() + ".part" + partCounter++;
                try (FileOutputStream out = new FileOutputStream(new File(file.getParent(), filePartName))) {
                    out.write(buffer, 0, bytesAmount);
                }
            }
        }
    }

    // Verifikimi i integritetit me SHA-256
    public static String getSHA256Checksum(File file) throws Exception {
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) > 0) {
                shaDigest.update(buffer, 0, n);
            }
        }

        byte[] hash = shaDigest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    // Main për demonstrim të plotë
    public static void main(String[] args) {
        try {
            // 1. Gjenero çelësin AES
            SecretKey key = generateAESKey(256);

            // 2. File-at
            File originalFile = new File("files/input.txt");
            File encryptedFile = new File("files/encrypted.aes");
            File decryptedFile = new File("files/decrypted.txt");

            // 3. Enkriptim
            encryptFile(originalFile, encryptedFile, key);
            System.out.println("✅ Enkriptimi përfundoi.");

            // 4. Fragmentim
            splitFile(encryptedFile, 1024 * 1024); // 1MB
            System.out.println("✅ Fragmentimi përfundoi.");

            // 5. Hash i origjinalit
            String originalHash = getSHA256Checksum(originalFile);
            System.out.println("🔍 Hash origjinal: " + originalHash);

            // 6. Dekriptim
            decryptFile(encryptedFile, decryptedFile, key);
            System.out.println("✅ Dekriptimi përfundoi.");

            // 7. Hash i dekriptuarit
            String decryptedHash = getSHA256Checksum(decryptedFile);
            System.out.println("🔍 Hash dekriptuar: " + decryptedHash);

            // 8. Krahasimi
            if (originalHash.equals(decryptedHash)) {
                System.out.println("✅ File i dekriptuar është identik.");
            } else {
                System.out.println("❌ Hash mismatch: File-i është ndryshuar.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
