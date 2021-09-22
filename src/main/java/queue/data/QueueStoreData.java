package queue.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class QueueStoreData {
    private static final Logger log = LogManager.getLogger(QueueStoreData.class);
    private final Path path;
    private long offset;

    public QueueStoreData(Path path) throws IOException {
        this.path = path;
        if(Files.notExists(path)){
            Files.createFile(this.path);
            this.offset = 0;
        }else{
            this.offset = Files.size(this.path);
        }
    }

    public void append(byte[] bytes) throws IOException {
        log.info("Writing " + bytes.length + " bytes to " + path + " at offset=" + offset);
        Files.write(path, bytes, StandardOpenOption.APPEND);
        this.offset += bytes.length;
    }

    public long getOffset(){
        return offset;
    }
}
