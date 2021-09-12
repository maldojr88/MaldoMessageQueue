package queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Queue implements Serializable {
    private static final Logger log = LogManager.getLogger(Queue.class);

    private String name;
    private Set<InetSocketAddress> publishers;
    private Set<InetSocketAddress> consumers;
    private transient Path path;
    private String pathStr;

    public Queue(Path queuesDir, String queueName) throws IOException {
        this.name = queueName;
        this.path = queuesDir.resolve(queueName);
        this.pathStr = path.toString();
        Files.createDirectory(path);
        publishers = new HashSet<>();
        consumers = new HashSet<>();
    }

    public void publish(InetSocketAddress address, String msg){
        validatePublish(address);
        log.info("Publishing " + msg + " to [" + name + "]");
    }

    private void validatePublish(InetSocketAddress address) {
        if(!publishers.contains(address)){
            String errorMsg = "Remote address not registered to publish " + address;
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    public void addPublisher(InetSocketAddress address){
        log.info("Adding " + address + " to publishers for " + name);
        publishers.add(address);
    }

    public void addConsumer(InetSocketAddress address){
        consumers.add(address);
    }

    @Serial
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.path = Paths.get(this.pathStr);
    }
}
