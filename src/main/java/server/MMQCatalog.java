package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MMQCatalog {
    private static final Logger log = LogManager.getLogger(MMQCatalog.class);
    private static final String FILE_NAME = "mmqcatalog.dat";
    private Map<String, String> catalog;
    private final Path catalogPath;

    public MMQCatalog(Path catalogDir) {
        this.catalogPath = Paths.get(catalogDir.toAbsolutePath() + "/" + FILE_NAME);
        loadCatalog();
    }

    public void createQueue(String queueName) {
        log.info("Creating queue " + queueName);
        catalog.put(queueName, "blah");
        log.info("Created " + queueName + " in memory");
        saveCatalog();
    }

    private void saveCatalog() {
        try {
            log.info("Saving catalog");
            OutputStream fos = Files.newOutputStream(catalogPath);;
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(catalog);
            oos.close();
            fos.close();
            log.info("Saved catalog");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCatalog() {
        try
        {
            InputStream fis = Files.newInputStream(catalogPath);
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
