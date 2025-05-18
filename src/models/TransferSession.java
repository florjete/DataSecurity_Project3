package models;

import javax.crypto.SecretKey;
import java.io.File;
import java.net.Socket;
import java.security.KeyPair;

public class TransferSession {
    private final KeyPair keyPair;
    private final SecretKey aesKey;
    private final Socket socket;
    private final File file;

    public TransferSession(KeyPair keyPair, SecretKey aesKey, Socket socket, File file) {
        this.keyPair = keyPair;
        this.aesKey = aesKey;
        this.socket = socket;
        this.file = file;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public SecretKey getAesKey() {
        return aesKey;
    }

    public Socket getSocket() {
        return socket;
    }

    public File getFile() {
        return file;
    }
}
