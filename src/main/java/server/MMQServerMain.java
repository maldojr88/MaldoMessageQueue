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
    bootstrapQueues(mmqServer);
    mmqServer.acceptConnections();
  }

  private static void bootstrapQueues(MMQServer mmqServer) {
    mmqServer.createQueue("Q1");
  }

  private static MMQConfig loadProperties() throws Exception {
    Properties prop = new Properties();
    try {
      prop.load(MMQServerMain.class.getClassLoader().getResourceAsStream(PROP_FILENAME));
    } catch (IOException e) {
      throw new Exception("Failed to load MMQ Server properties");
    }
    return new MMQConfig(Path.of(prop.getProperty("catalog.dir")),
            Integer.parseInt(prop.getProperty("port")));
  }
}