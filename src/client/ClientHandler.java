package client;

import java.io.*;
import java.net.Socket;

public class ClientHandler {

    private final String serverAddress;
    private final int serverPort;
    private final String encryptedAESKey;

    public ClientHandler(String serverAddress, int serverPort, String encryptedAESKey) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.encryptedAESKey = encryptedAESKey;
    }

    public void sendEncryptedAESKey() {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            System.out.println("🔗 Lidhu me serverin për dërgim të AES Key...");
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            // 1️⃣ Dërgo kyçin AES të enkriptuar
            outputStream.writeObject(encryptedAESKey);
            outputStream.flush();
            System.out.println("✅ Kyçi AES i enkriptuar u dërgua me sukses!");

        } catch (IOException e) {
            System.err.println("❌ Gabim gjatë dërgimit të AES Key te serveri: " + e.getMessage());
        }
    }
}
