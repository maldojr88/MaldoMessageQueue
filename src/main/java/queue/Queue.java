package queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class Queue implements Serializable {
    private static final Logger log = LogManager.getLogger(Queue.class);

    private final String name;
    private Set<InetSocketAddress> publishers;
    private Map<InetSocketAddress, Long> consumers;//remote adder, to instant id
    private final QueueStore queueStore;
    private List<QueueEntry> entries;

    public Queue(String queueName, QueueStore queueStore) {
        this.name = queueName;
        this.queueStore = queueStore;
        this.publishers = new HashSet<>();
        this.consumers = new HashMap<>();
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
        //TODO - now notify all the consumers that need this message
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
        consumers.put(address, 0L);
    }

    public void addConsumer(InetSocketAddress address, long instant) throws IOException {
        consumers.put(address, instant);
        //TODO change to make the below more dynamic ==> currently just sending as we get
        queueStore.getOffset(instant);
    }

    /*@Serial
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.path = Paths.get(this.pathStr);
    }*/
}
