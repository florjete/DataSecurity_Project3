package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class Server {
    private static final int PORT = 5555;

    public static void main(String[] args) throws Exception {
        // 1) Përgatit storage
        FileStorage storage = new FileStorage(Path.of("server_storage"));
        // 2) Gjenero çifte RSA
        KeyManager keyManager = new KeyManager();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server po pret në portin " + PORT);

            while (true) {
                Socket clientSock = serverSocket.accept();
                // 3) Shërbe klienët në thread të veçantë
                new Thread(new ServerHandler(clientSock, keyManager, storage)).start();
            }
        }
    }
}
