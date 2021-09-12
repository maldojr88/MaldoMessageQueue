package queue;

import java.io.IOException;
import java.nio.file.Path;

public class QueueFactory {
    public static Queue newQueue(Path queuesDir, String queueName) throws IOException {
        return new Queue(queuesDir, queueName);
    }
}
