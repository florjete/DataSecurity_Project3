// ClientHandler.java
package client;

import models.*;
import models.TransferSession;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;

public class ClientHandler {

    public static void upload(TransferSession session) throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(session.getSocket().getOutputStream());
        ObjectInputStream in = new ObjectInputStream(session.getSocket().getInputStream());

        out.writeObject("UPLOAD_REQUEST");
        PublicKey serverPublicKey = (PublicKey) in.readObject();

        File file = session.getFile();
        byte[] fileData = readFile(file);

        //byte[] encryptedAESKey = RSAUtil.encrypt(session.getAesKey().getEncoded(), serverPublicKey);
        //byte[] encryptedFileData = AESUtil.encryptFile(fileData, session.getAesKey());
        //byte[] signature = RSAUtil.sign(fileData, session.getKeyPair().getPrivate());

//        out.writeObject(encryptedAESKey);
//        out.writeObject(encryptedFileData);
//        out.writeObject(signature);
        out.writeObject(session.getKeyPair().getPublic());

        System.out.println("Upload completed.");
    }

    public static void download(TransferSession session, String outputFilePath) throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(session.getSocket().getOutputStream());
        ObjectInputStream in = new ObjectInputStream(session.getSocket().getInputStream());

        out.writeObject("DOWNLOAD_REQUEST");
        out.writeObject(session.getKeyPair().getPublic());

        byte[] encryptedAESKey = (byte[]) in.readObject();
//        byte[] aesKeyBytes = RSAUtil.decrypt(encryptedAESKey, session.getKeyPair().getPrivate());
//        SecretKey aesKey = AESUtil.restoreAESKey(aesKeyBytes);

        byte[] encryptedFile = (byte[]) in.readObject();
        byte[] signature = (byte[]) in.readObject();
        PublicKey senderKey = (PublicKey) in.readObject();

//        byte[] fileData = AESUtil.decryptFile(encryptedFile, aesKey);

//        if (RSAUtil.verify(fileData, signature, senderKey)) {
//            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
//                fos.write(fileData);
//            }
//            System.out.println("Download completed and verified.");
//        } else {
//            System.out.println("File verification failed.");
//        }
    }

    private static byte[] readFile(File file) throws IOException {
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }
        return data;
    }
}