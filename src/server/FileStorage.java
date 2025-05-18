package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStorage {
    private final Path storageDir;

    public FileStorage(Path storageDir) throws IOException {
        this.storageDir = storageDir;
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
    }

    public void saveFile(String filename, byte[] data) throws IOException {
        Path target = storageDir.resolve(filename);
        Files.write(target, data);
        System.out.println("💾 Skedari u ruajt në: " + target);
    }

    public byte[] readFile(String filename) throws IOException {
        Path target = storageDir.resolve(filename);
        return Files.readAllBytes(target);
    }

    public boolean exists(String filename) {
        return Files.exists(storageDir.resolve(filename));
    }
}
