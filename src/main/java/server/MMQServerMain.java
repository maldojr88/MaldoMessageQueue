package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class MMQServerMain {

  private static final Logger log = LogManager.getLogger(MMQServerMain.class);
  private static final String PROP_FILENAME = "mmq-server.properties";

  public static void main(String[] args) throws Exception {
    log.info("Initializing MMQServer");
    MMQConfig config = loadProperties();
    MMQServer mmqServer = new MMQServer(config);
    mmqServer.initialize();
  }


  private static MMQConfig loadProperties() throws Exception {
    Properties prop = new Properties();
    try {
      prop.load(MMQServerMain.class.getClassLoader().getResourceAsStream(PROP_FILENAME));
    } catch (IOException e) {
      throw new Exception("Failed to load MMQ Server properties");
    }
    Path rootDir = Path.of(prop.getProperty("root.dir"));
    Path serverDir = rootDir.resolve("mmq");
    Path catalog = serverDir.resolve("catalog");
    Path queues = catalog.resolve("queues");
    return new MMQConfig(serverDir, catalog, queues,
            Integer.parseInt(prop.getProperty("port")));
  }
}