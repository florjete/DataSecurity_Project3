package client;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;

public class KeyExchange {

    private final String serverAddress;
    private final int serverPort;
    private PublicKey serverPublicKey;

    public KeyExchange(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    public void exchangeKeys(PublicKey clientPublicKey) {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            System.out.println("🔗 Lidhu me serverin në: " + serverAddress + ":" + serverPort);

            // 1️⃣ Dërgo kyçin publik të klientit te serveri
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(clientPublicKey);
            outputStream.flush();
            System.out.println("✅ Kyçi publik i klientit u dërgua te serveri.");

            // 2️⃣ Prano kyçin publik të serverit
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            serverPublicKey = (PublicKey) inputStream.readObject();
            System.out.println("✅ Kyçi publik i serverit u pranua me sukses.");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Dështoi shkëmbimi i kyçeve: " + e.getMessage());
        }
    }
}
