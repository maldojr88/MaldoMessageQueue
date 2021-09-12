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
    private static final String FILE_NAME = "mmqcatalog.dat";

    private Map<String, Queue> catalog;
    private final Path catalogDir;
    private final Path queuesDir;

    public MMQCatalog(Path catalogDir, Path queuesPath) {
        this.catalogDir = catalogDir.resolve(FILE_NAME);
        this.queuesDir = queuesPath;
        //this.queuesDirStr = queuesDir.toString();
        loadCatalog();
    }

    public void createQueue(String queueName) throws IOException {
        if(catalog.containsKey(queueName)){
            log.info("Not creating Queue " + queueName + " as it already exists");
            return;
        }
        log.info("Creating queue " + queueName);
        Queue queue = QueueFactory.newQueue(queuesDir, queueName);

        catalog.put(queueName, queue);
        log.info("Created " + queueName + " in memory");
        saveCatalog();
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

    private void saveCatalog() {
        try {
            log.info("Saving catalogDir");
            OutputStream fos = Files.newOutputStream(catalogDir);;
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(catalog);
            oos.close();
            fos.close();
            log.info("Saved catalogDir");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCatalog() {
        try {
            InputStream fis = Files.newInputStream(catalogDir);
            ObjectInputStream ois = new ObjectInputStream(fis);
            catalog = (HashMap) ois.readObject();
            ois.close();
            fis.close();
            log.info("Successfully loaded catalogDir " + catalog.toString());
        }catch (IOException | ClassNotFoundException e) {
            log.error("Failed to open catalogDir", e);
            log.info("Creating empty catalogDir");
            catalog = new HashMap<>();
        }
    }
}
