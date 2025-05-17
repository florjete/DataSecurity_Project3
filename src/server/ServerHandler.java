package server;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;

public class ServerHandler implements Runnable {
    private final Socket socket;
    private final KeyManager keyManager;
    private final FileStorage storage;

    public ServerHandler(Socket socket, KeyManager keyManager, FileStorage storage) {
        this.socket = socket;
        this.keyManager = keyManager;
        this.storage = storage;
    }

    @Override
    public void run() {
        try (ObjectInputStream objIn  = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
             DataInputStream dataIn    = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOut  = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("🔗 Klienti i ri u lidh.");

            // 1️⃣ Prano PublicKey të klientit
            PublicKey clientPub = (PublicKey) objIn.readObject();
            System.out.println("✅ Pranuam PublicKey të klientit.");

            // 2️⃣ Dërgo PublicKey të serverit
            objOut.writeObject(keyManager.getPublicKey());
            objOut.flush();
            System.out.println("✅ Dërguam PublicKey të serverit.");

            // 3️⃣ Prano AES session-key të enkriptuar (me RSA)
            int keyLen = dataIn.readInt();
            byte[] encAesKey = new byte[keyLen];
            dataIn.readFully(encAesKey);

            byte[] aesKeyBytes = keyManager.decryptWithPrivateKey(encAesKey);
            SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");
            System.out.println("🔐 AES session key dekriptuar.");

            // 4️⃣ Protokolli: UPLOAD / DOWNLOAD
            String cmd = dataIn.readUTF();
            if ("UPLOAD".equals(cmd)) {
                handleUpload(dataIn, dataOut, aesKey);
            } else if ("DOWNLOAD".equals(cmd)) {
                handleDownload(dataIn, dataOut, aesKey);
            } else {
                dataOut.writeUTF("ERROR: Unknown command");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println("🔌 Lidhja u mbyll.");
        }
    }

    private void handleUpload(DataInputStream in, DataOutputStream out, SecretKeySpec aesKey) throws Exception {
        String filename = in.readUTF();
        long length = in.readLong();
        byte[] encrypted = new byte[(int) length];
        in.readFully(encrypted);

        byte[] plaintext = AESFileEncryption.decrypt(aesKey.getEncoded(), encrypted);
        storage.saveFile(filename, plaintext);

        out.writeUTF("UPLOAD_OK");
        System.out.println("💾 Skedari u ruajt: " + filename);
    }

    private void handleDownload(DataInputStream in, DataOutputStream out, SecretKeySpec aesKey) throws Exception {
        String filename = in.readUTF();
        if (!storage.exists(filename)) {
            out.writeUTF("ERROR: File not found");
            return;
        }

        byte[] plaintext = storage.readFile(filename);
        byte[] encrypted = AESFileEncryption.encrypt(aesKey.getEncoded(), plaintext);

        out.writeUTF("DOWNLOAD_OK");
        out.writeLong(encrypted.length);
        out.write(encrypted);
        System.out.println("📤 Skedari u dërgua: " + filename);
    }
}
