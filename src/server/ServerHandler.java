package server;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Arrays;

import client.*;

public class ServerHandler implements Runnable {
    private final Socket socket;
    private final KeyManager keyManager;
    private final FileStorage storage;

    public ServerHandler(Socket socket, KeyManager keyManager, FileStorage storage) {
        this.socket = socket;
        this.keyManager = keyManager;
        this.storage = storage;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            // Pranojmë kërkesën nga klienti (UPLOAD / DOWNLOAD)
            String requestType = (String) in.readObject();
            System.out.println("📥 Kërkesa: " + requestType);

            if ("UPLOAD_REQUEST".equals(requestType)) {
                handleUpload(in, out);
            } else if ("DOWNLOAD_REQUEST".equals(requestType)) {
                handleDownload(in, out);
            } else {
                out.writeObject("ERROR: Unknown command");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleUpload(ObjectInputStream in, ObjectOutputStream out) throws Exception {
        System.out.println("🔄 Filloi handleUpload...");

        // 1️⃣ Dërgo çelësin publik të serverit
        out.writeObject(keyManager.getPublicKey());
        out.flush();
        System.out.println("✅ Kyçi publik i serverit u dërgua.");

        // 2️⃣ Pranojmë çelësin publik të klientit
        PublicKey clientPub = (PublicKey) in.readObject();
        System.out.println("✅ Kyçi publik i klientit u pranua.");

        // 3️⃣ Pranojmë AES Key
        System.out.println("⏳ Duke pritur çelësin AES të enkriptuar...");
        byte[] encryptedAESKey = (byte[]) in.readObject();
        System.out.println("🔓 Çelësi AES u pranua.");

        // 4️⃣ Pranojmë të dhënat e skedarit
        byte[] encryptedFileData = (byte[]) in.readObject();
        System.out.println("📦 Të dhënat e skedarit u pranuan.");

        // 5️⃣ Pranojmë nënshkrimin
        byte[] signature = (byte[]) in.readObject();
        System.out.println("✍️ Nënshkrimi digjital u pranua.");

        // Dekriptimi i AES Key me çelësin privat të serverit
        byte[] aesKeyBytes = RSAUtil.decrypt(encryptedAESKey, keyManager.getPrivateKey());
        SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");
        System.out.println("🔓 Çelësi AES u dekriptua me sukses.");

        // Dekriptimi i skedarit
        byte[] fileData = AESUtil.decrypt(aesKey.getEncoded(), encryptedFileData);
        System.out.println("📂 Skedari u dekriptua me sukses.");

        // Verifikimi i hash-it dhe nënshkrimit
        byte[] hash = HashUtil.generateSHA256(fileData);
        boolean verified = DigitalSignature.verify(hash, signature, clientPub);

        if (verified) {
            storage.saveFile("uploaded_file.txt", fileData);
            out.writeObject("UPLOAD_OK");
            System.out.println("✅ Skedari i verifikuar u ruajt me sukses.");
        } else {
            out.writeObject("ERROR: Signature verification failed");
            System.err.println("❌ Nënshkrimi nuk u verifikua!");
        }
    }

    private void handleDownload(ObjectInputStream in, ObjectOutputStream out) throws Exception {
        String filename = (String) in.readObject(); // Marrim emrin e skedarit nga klienti
        System.out.println("📥 Kërkesë për download për skedarin: " + filename);

        if (storage.exists(filename)) {
            byte[] fileData = storage.readFile(filename);
            System.out.println("✅ Skedari u gjet. Madhësia: " + fileData.length + " bytes");

            // 🔐 Këtu duhet të kemi AES Key-n që është përdorur gjatë upload
            // Zakonisht ky key duhet të ruhet në sesion ose të rikuperohet
            // Për këtë shembull, po e marrim nga klienti në vend që ta gjenerojmë vetë
            byte[] aesKeyBytes = new byte[16]; // Kjo duhet të jetë nga sesioni
            Arrays.fill(aesKeyBytes, (byte) 1); // Vetëm për testim, duhet marrë nga sesioni real

            // Enkriptimi me AES
            byte[] encryptedFileData = AESUtil.encrypt(aesKeyBytes, fileData);

            // ✅ Dërgojmë skedarin e enkriptuar
            out.writeObject(encryptedFileData);
            out.flush();
            System.out.println("📤 Skedari u dërgua me sukses.");
        } else {
            System.err.println("❌ Skedari nuk u gjet në server.");
            out.writeObject(null);
            out.flush();
        }
    }


}
