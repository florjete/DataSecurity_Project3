package client;

import models.TransferSession;

import java.io.*;
import java.security.PublicKey;

public class ClientHandler {
    public static void upload(TransferSession session, ObjectOutputStream out, ObjectInputStream in) throws Exception {
        out.writeObject("UPLOAD_REQUEST");
        out.writeObject(session.getFile().getName());
        out.flush();

        PublicKey serverPublicKey = (PublicKey) in.readObject();
        out.writeObject(session.getKeyPair().getPublic());
        out.flush();

        byte[] fileData = readFile(session.getFile());
        byte[] encryptedAESKey = RSAUtil.encrypt(session.getAesKey().getEncoded(), serverPublicKey);
        byte[] encryptedFileData = AESUtil.encrypt(session.getAesKey().getEncoded(), fileData);

        out.writeObject(encryptedAESKey);
        out.writeObject(encryptedFileData);
        out.flush();

        byte[] hash = HashUtil.generateSHA256(fileData);
        byte[] signature = DigitalSignature.sign(hash, session.getKeyPair().getPrivate());
        out.writeObject(signature);
        out.flush();

        String response = (String) in.readObject();
        System.out.println("✅ Përgjigja nga serveri: " + response);
    }

    public static void download(TransferSession session, ObjectOutputStream out, ObjectInputStream in, String outputPath) throws Exception {
        out.writeObject("DOWNLOAD_REQUEST");
        out.writeObject(session.getFile().getName());
        out.flush();

        String response = (String) in.readObject();
        if ("DOWNLOAD_OK".equals(response)) {
            byte[] fileData = (byte[]) in.readObject();
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(fileData);
            }
            System.out.println("✅ Skedari u shkarkua në: " + outputPath);
        } else {
            System.out.println("❌ Gabim: " + response);
        }
    }

    private static byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        }
    }
}