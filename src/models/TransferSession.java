// TransferSession.java
package models;

import javax.crypto.SecretKey;
import java.io.File;
import java.net.Socket;
import java.security.KeyPair;

public class TransferSession {
    private KeyPair keyPair;
    private SecretKey aesKey;
    private Socket socket;
    private File file;

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