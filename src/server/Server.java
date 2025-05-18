package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class Server {
    private static final int PORT = 5555;

    public static void main(String[] args) {
        try {
            System.out.println("🌐 Server po starton...");

            // 1️⃣ Inicializimi i File Storage
            FileStorage storage = new FileStorage(Path.of("server_storage"));

            // 2️⃣ Inicializimi i Key Manager
            KeyManager keyManager = new KeyManager();

            // 3️⃣ Startimi i serverit në portin e caktuar
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("✅ Serveri po pret lidhje në portin " + PORT);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("🔗 Klient i ri u lidh!");
                    new Thread(new ServerHandler(clientSocket, keyManager, storage)).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
