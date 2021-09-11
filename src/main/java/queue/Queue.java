package queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class Queue {
    private static final Logger log = LogManager.getLogger(Queue.class);

    private String name;
    private Set<InetSocketAddress> publishers;
    private Set<InetSocketAddress> consumers;

    public Queue(String queueName){
        this.name = queueName;
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
}
