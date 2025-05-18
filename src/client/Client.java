package client;

import models.TransferSession;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5555;

    public static void main(String[] args) {
        // Check for minimum arguments
        if (args.length < 2) {
            System.err.println("Përdorimi: java Client <command> <file>");
            System.err.println("Komandat:");
            System.err.println("  upload <file_path>    - Ngarko skedarin");
            System.err.println("  download <file_name>  - Shkarko skedarin");
            System.exit(1);
        }

        String command = args[0];
        String fileArg = args[1];

        try {
            System.out.println("🔑 Gjenerimi i çifteve të kyçeve RSA...");
            KeyPair keyPair = RSAUtil.generateKeyPair();
            SecretKey aesKey = AESUtil.generateAESKey();

            System.out.println("🌐 Lidhja me serverin...");
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                System.out.println("✅ Lidhja me serverin u krye me sukses.");

                if (command.equalsIgnoreCase("upload")) {
                    File file = new File(fileArg);
                    if (!file.exists() || !file.isFile()) {
                        System.err.println("❌ Skedari nuk ekziston: " + fileArg);
                        return;
                    }

                    TransferSession session = new TransferSession(keyPair, aesKey, socket, file);
                    ClientHandler.upload(session, out, in);
                    System.out.println("✅ Skedari u ngarkua me sukses: " + fileArg);

                } else if (command.equalsIgnoreCase("download")) {
                    String savePath = "client_storage/" + fileArg;
                    new File("client_storage").mkdirs();

                    TransferSession session = new TransferSession(keyPair, aesKey, socket, new File(fileArg));
                    ClientHandler.download(session, out, in, savePath);
                    System.out.println("✅ Skedari u shkarkua në: " + savePath);

                } else {
                    System.err.println("❌ Komandë e pavlefshme: " + command);
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Gabim në klient: " + e.getMessage());
            e.printStackTrace();
        }
    }
}