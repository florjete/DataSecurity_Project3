package server;

import client.*;
import models.TransferSession;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;

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
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            String requestType = (String) in.readObject();
            System.out.println("🌐 Kërkesa e pranuar: " + requestType);

            if ("UPLOAD_REQUEST".equals(requestType)) {
                handleUpload(in, out);
            } else if ("DOWNLOAD_REQUEST".equals(requestType)) {
                handleDownload(in, out);
            }
        } catch (Exception e) {
            System.err.println("❌ Gabim në ServerHandler: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("🔌 Lidhja u mbyll.");
            } catch (IOException e) {
                System.err.println("❌ Dështoi mbyllja e socket-it.");
            }
        }
    }

    private void handleUpload(ObjectInputStream in, ObjectOutputStream out) throws Exception {
        String fileName = (String) in.readObject();
        out.writeObject(keyManager.getPublicKey());
        out.flush();

        PublicKey clientPub = (PublicKey) in.readObject();
        byte[] encryptedAESKey = (byte[]) in.readObject();
        byte[] encryptedFileData = (byte[]) in.readObject();
        byte[] signature = (byte[]) in.readObject();

        byte[] aesKeyBytes = RSAUtil.decrypt(encryptedAESKey, keyManager.getPrivateKey());
        SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");
        byte[] fileData = AESUtil.decrypt(aesKey.getEncoded(), encryptedFileData);

        byte[] hash = HashUtil.generateSHA256(fileData);
        boolean verified = DigitalSignature.verify(hash, signature, clientPub);

        if (verified) {
            storage.saveFile(fileName, fileData);
            out.writeObject("UPLOAD_OK");
        } else {
            out.writeObject("ERROR: Nënshkrimi i pavlefshëm");
        }
    }

    private void handleDownload(ObjectInputStream in, ObjectOutputStream out) throws Exception {
        String filename = (String) in.readObject();
        byte[] fileData = storage.readFile(filename);

        if (fileData != null) {
            out.writeObject("DOWNLOAD_OK");
            out.writeObject(fileData);
        } else {
            out.writeObject("ERROR: Skedari nuk u gjet");
        }
    }
}