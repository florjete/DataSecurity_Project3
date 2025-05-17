
import client.ClientHandler;
import models.TransferSession;
import models.*;

import javax.crypto.SecretKey;
import java.io.File;
import java.net.Socket;
import java.security.KeyPair;

public class Main {
    public static void main(String[] args) {
        try {
            KeyPair keyPair = RSAUtil.generateKeyPair();
            SecretKey aesKey = AESUtil.generateAESKey();
            File file = new File("file_to_send.txt");

            Socket socket = new Socket("localhost", 8080);
            TransferSession session = new TransferSession(keyPair, aesKey, socket, file);

            boolean doUpload = true; // Change to false to test download

            if (doUpload) {
                ClientHandler.upload(session);
            } else {
                ClientHandler.download(session, "downloaded_file.txt");
            }

            socket.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}