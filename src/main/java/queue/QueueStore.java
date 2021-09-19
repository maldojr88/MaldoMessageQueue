package queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.MMQServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class QueueStore {
    private static final Logger log = LogManager.getLogger(QueueStore.class);
    private static final String DATA_FILENAME = "mmq.data";

    private Path dir;
    private Path data;
    public QueueStore(Path dir) throws IOException {
        this.dir = dir;
        this.data = dir.resolve(DATA_FILENAME);
        Files.createDirectory(dir);
        if(Files.notExists(data)){
            Files.createFile(this.data);
        }
    }

    public void append(byte[] bytes) throws IOException {
        log.info("Writing " + bytes.length + " bytes to " + data);
        Files.write(data, bytes, StandardOpenOption.APPEND);
    }
}
