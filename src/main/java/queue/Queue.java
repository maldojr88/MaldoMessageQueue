package queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Queue implements Serializable {
    private static final Logger log = LogManager.getLogger(Queue.class);

    private final String name;
    private Set<InetSocketAddress> publishers;
    private Set<InetSocketAddress> consumers;
    private final QueueStore queueStore;
    private List<QueueEntry> entries;

    public Queue(String queueName, QueueStore queueStore) {
        this.name = queueName;
        this.queueStore = queueStore;
        this.publishers = new HashSet<>();
        this.consumers = new HashSet<>();
        this.entries = new ArrayList<>();
    }

    public void publish(InetSocketAddress address, String msg) throws IOException {
        validatePublish(address);
        log.info("Publishing " + msg + " to this queue [" + name + "]");
        long id = Instant.now().toEpochMilli();
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        QueueEntry entry = new QueueEntry(id, msgBytes);
        //byte[] packedEntry = entry.pack();
        //log.info(packedEntry);
        queueStore.append(entry);
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

    /*@Serial
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.path = Paths.get(this.pathStr);
    }*/
}
