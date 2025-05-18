package client;

import models.TransferSession;

import javax.crypto.SecretKey;
import java.io.File;
import java.net.Socket;
import java.security.KeyPair;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5555;

    public static void main(String[] args) {
        try {
            System.out.println("🔑 Gjenerimi i çifteve të kyçeve RSA...");
            KeyPair keyPair = RSAUtil.generateKeyPair();
            SecretKey aesKey = AESUtil.generateAESKey();

            System.out.println("🌐 Lidhja me serverin...");
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            Scanner scanner = new Scanner(System.in);

            System.out.println("✅ Lidhja me serverin u krye me sukses.");
            System.out.println("1️⃣ Zgjedhni opsionin: [1] Upload   [2] Download");
            int option = scanner.nextInt();
            scanner.nextLine(); // Pastrim i bufferit

            if (option == 1) {
                System.out.print("🗂 Shkruani path-in e skedarit për upload: ");
                String filePath = scanner.nextLine();
                File file = new File(filePath);

                if (!file.exists() || !file.isFile()) {
                    System.err.println("❌ Skedari nuk ekziston.");
                    return;
                }

                TransferSession session = new TransferSession(keyPair, aesKey, socket, file);
                ClientHandler.upload(session);

            } else if (option == 2) {
                System.out.print("📝 Shkruani emrin e skedarit për download: ");
                String fileName = scanner.nextLine();

                TransferSession session = new TransferSession(keyPair, aesKey, socket, new File(fileName));
                ClientHandler.download(session, fileName);

            } else {
                System.out.println("❌ Opsion i pavlefshëm!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
