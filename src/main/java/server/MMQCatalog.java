package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import queue.Queue;
import queue.QueueFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MMQCatalog {
    private static final Logger log = LogManager.getLogger(MMQCatalog.class);

    private Map<String, Queue> catalog;
    private final Path queuesDir;

    public MMQCatalog(Path queuesPath) throws IOException {
        this.queuesDir = queuesPath;
        this.catalog = new HashMap<>();
        loadFromFile();
    }

    public void createQueue(String queueName) throws IOException {
        if(catalog.containsKey(queueName)){
            log.info("Not creating Queue " + queueName + " as it already exists");
            return;
        }
        createQueue(queueName, false);
    }

    private void createQueue(String queueName, boolean loadFromDisk) throws IOException {
        log.info("Creating queue " + queueName);
        Queue queue = QueueFactory.newQueue(queuesDir, queueName, loadFromDisk);

        catalog.put(queueName, queue);
        log.info("Created " + queueName + " in memory");
    }

    public Queue getQueue(String queueName){
        validateQueueExists(queueName);
        return catalog.get(queueName);
    }

    private void validateQueueExists(String queueName) {
        if(!catalog.containsKey(queueName)){
            String errorMsg = queueName + " does not exist";
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    private void loadFromFile() throws IOException {
        log.info("Loading queues from Disk");
        File[] directories = queuesDir.toFile().listFiles(File::isDirectory);
        for (File directory : directories) {
            createQueue(directory.toPath().getFileName().toString(), true);
        }
    }
}
