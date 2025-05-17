package server;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;

public class ServerHandler implements Runnable {

    private final Socket clientSocket;
    private final KeyManager keyManager;

    public ServerHandler(Socket clientSocket, KeyManager keyManager) {
        this.clientSocket = clientSocket;
        this.keyManager = keyManager;
    }

    @Override
    public void run() {
        try {
            System.out.println("🔗 Klienti u lidh me sukses!");

            // 1️⃣ Prano kyçin publik të klientit
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            PublicKey clientPublicKey = (PublicKey) inputStream.readObject();
            System.out.println("✅ Kyçi publik i klientit u pranua.");

            // 2️⃣ Dërgo kyçin publik të serverit te klienti
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.writeObject(keyManager.getPublicKey());
            outputStream.flush();
            System.out.println("✅ Kyçi publik i serverit u dërgua te klienti.");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Gabim gjatë shkëmbimit të kyçeve: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("❌ Nuk u mbyll socket-i: " + e.getMessage());
            }
        }
    }
}
