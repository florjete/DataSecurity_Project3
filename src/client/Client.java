// Client.java
package client;

import models.*;
import models.TransferSession;

import javax.crypto.SecretKey;
import java.io.File;
import java.net.Socket;
import java.security.KeyPair;

public class Client {
    public static void start() throws Exception {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        SecretKey aesKey = AESUtil.generateAESKey();
        File file = new File("file_to_send.txt");

        try (Socket socket = new Socket("localhost", Constants.PORT)) {
            TransferSession session = new TransferSession(keyPair, aesKey, socket, file);

            boolean doUpload = true; // Change to false for download

            if (doUpload) {
                ClientHandler.upload(session);
            } else {
                ClientHandler.download(session, "downloaded_file.txt");
            }
        }
    }
}