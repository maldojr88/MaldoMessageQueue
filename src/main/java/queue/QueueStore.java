package queue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class QueueStore {

    private Path dir;
    public QueueStore(Path dir) throws IOException {
        this.dir = dir;
        Files.createDirectory(dir);
    }
}
