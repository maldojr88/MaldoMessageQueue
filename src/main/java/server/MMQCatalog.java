package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MMQCatalog {
    private static final Logger log = LogManager.getLogger(MMQCatalog.class);
    private Map<String, String> catalog;

    public MMQCatalog(){
        loadCatalog();
    }

    private void saveCatalog() {
        try
        {
            log.info("Saving catalog");
            FileOutputStream fos = new FileOutputStream("hashmap.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(catalog);
            oos.close();
            fos.close();
            log.info("Serialized HashMap data is saved in hashmap.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createQueue(String queueName) {
        log.info("Creating queue " + queueName);
        catalog.put(queueName, "blah");
        log.info("Created " + queueName + " in memory");
        saveCatalog();
    }

    /*
     * 1. load state
     * 2. Print table
     */
    private void loadCatalog() {
        try
        {
            FileInputStream fis = new FileInputStream("hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            catalog = (HashMap) ois.readObject();
            ois.close();
            fis.close();
            log.info("Successfully loaded catalog " + catalog.toString());
        }catch (IOException | ClassNotFoundException e) {
            log.error("Failed to open catalog", e);
            log.info("Creating empty catalog");
            catalog = new HashMap<>();
        }
    }
}
