package client;

import models.TransferSession;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.PublicKey;
import java.util.Arrays;

public class ClientHandler {

    public static void upload(TransferSession session) throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(session.getSocket().getOutputStream());
        ObjectInputStream in = new ObjectInputStream(session.getSocket().getInputStream());

        System.out.println("📤 Kërkesa për upload po dërgohet...");
        out.writeObject("UPLOAD_REQUEST");
        out.flush();

        System.out.println("⏳ Duke pritur kyçin publik të serverit...");
        PublicKey serverPublicKey = (PublicKey) in.readObject();
        System.out.println("🔑 Kyçi publik i serverit u pranua.");

        // 🔴 Dërgojmë çelësin publik të klientit
        out.writeObject(session.getKeyPair().getPublic());
        out.flush();
        System.out.println("✅ Kyçi publik i klientit u dërgua te serveri.");

        // 2️⃣ Leximi i skedarit dhe enkriptimi
        File file = session.getFile();
        byte[] fileData = readFile(file);

        if (fileData.length == 0) {
            System.err.println("❌ Skedari është bosh.");
            return;
        }
        System.out.println("📂 Leximi i skedarit u krye me sukses. Madhësia: " + fileData.length + " bytes");

        // 3️⃣ Enkriptimi i të dhënave dhe hashimi
        byte[] encryptedAESKey = RSAUtil.encrypt(session.getAesKey().getEncoded(), serverPublicKey);
        out.writeObject(encryptedAESKey);
        out.flush();
        System.out.println("🔒 AES Key u dërgua.");

        byte[] encryptedFileData = AESUtil.encrypt(session.getAesKey().getEncoded(), fileData);
        out.writeObject(encryptedFileData);
        out.flush();
        System.out.println("🔒 Skedari i enkriptuar u dërgua.");

        byte[] hash = HashUtil.generateSHA256(fileData);
        byte[] signature = DigitalSignature.sign(hash, session.getKeyPair().getPrivate());
        out.writeObject(signature);
        out.flush();
        System.out.println("✅ Nënshkrimi digjital u dërgua.");
    }

    public static void download(TransferSession session, String outputFilePath) throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(session.getSocket().getOutputStream());
        ObjectInputStream in = new ObjectInputStream(session.getSocket().getInputStream());

        // 1️⃣ Kërkesa për download
        System.out.println("📥 Duke kërkuar skedarin: " + outputFilePath);
        out.writeObject("DOWNLOAD_REQUEST");

        // Mos i jep path-in lokal, vetëm emrin e skedarit
        File file = new File(outputFilePath);
        out.writeObject(file.getName());
        out.flush();

        // 2️⃣ Pranojmë skedarin e enkriptuar nga serveri
        System.out.println("⏳ Duke pritur të dhënat nga serveri...");
        byte[] encryptedFileData = (byte[]) in.readObject();

        if (encryptedFileData == null) {
            System.err.println("❌ Skedari nuk u gjet në server.");
            return;
        }

        System.out.println("📥 Të dhënat e skedarit të enkriptuar u pranuan.");

        // 3️⃣ Dekriptimi i skedarit
        byte[] decryptedFileData = AESUtil.decrypt(session.getAesKey().getEncoded(), encryptedFileData);

        // 4️⃣ Ruajtja në disk
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            fos.write(decryptedFileData);
            System.out.println("📦 Skedari u ruajt në disk: " + outputFilePath);
        }
    }


    private static byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        }
    }
}
