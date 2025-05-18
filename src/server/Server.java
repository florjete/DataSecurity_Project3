package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class Server {
    // Shto mundësinë për të specifikuar portin si argument
    private static final int DEFAULT_PORT = 5050;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        // Përpunimi i argumenteve të command-line
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                System.out.println("ℹ️ Përdorimi i portit të specifikuar: " + port);
            } catch (NumberFormatException e) {
                System.err.println("❌ Porti duhet të jetë numër. Duke përdorur portin default: " + DEFAULT_PORT);
            }
        }

        try {
            System.out.println("🌐 Serveri po starton...");
            FileStorage storage = new FileStorage(Path.of("server_storage"));
            KeyManager keyManager = new KeyManager();

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("✅ Serveri po pret lidhje në portin " + port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("🔗 Klient i ri u lidh!");
                    new Thread(new ServerHandler(clientSocket, keyManager, storage)).start();
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Gabim në server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}