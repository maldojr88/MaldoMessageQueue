package queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import queue.data.QueueStoreData;
import queue.index.IndexTreeNode;
import queue.index.QueueStoreIndex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class QueueStore {
    private static final Logger log = LogManager.getLogger(QueueStore.class);
    private static final String DATA_FILENAME = "mmq.data";
    private static final String INDEX_FILENAME = "mmq.index";

    private final Path dir;
    private QueueStoreData data;
    private QueueStoreIndex index;

    public QueueStore(Path dir, boolean loadFromDisk) throws IOException {
        this.dir = dir;
        if(loadFromDisk) {
            Files.createDirectories(dir);
        }else{
            Files.createDirectory(dir);
        }
        this.data = new QueueStoreData(dir.resolve(DATA_FILENAME));
        this.index = new QueueStoreIndex(dir.resolve(INDEX_FILENAME));
    }

    public void append(QueueEntry entry) throws IOException {
        long offset = data.getOffset();
        data.append(entry.pack());
        index.append(new IndexTreeNode(entry.getInstant(), offset, entry.getByteSize()));
    }

    public long getOffset(long instant) throws IOException {
        return index.searchOffset(instant);
    }

    //For debugging purposes
    public void printMsgFromInstant(long instant) throws IOException {
        IndexTreeNode indexEntry = index.getEntryFromInstant(instant);
        QueueEntry entry = data.readFromOffset(indexEntry);
        log.info("Read from file: ");
        log.info(entry);
    }
}
