package queue;

public class QueueFactory {
    public static Queue newQueue(String queueName){
        return new Queue(queueName);
    }
}
