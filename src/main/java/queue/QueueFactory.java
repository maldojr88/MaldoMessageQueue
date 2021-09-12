package queue;

import java.io.IOException;
import java.nio.file.Path;

public class QueueFactory {
    public static Queue newQueue(Path queuesDir, String queueName) throws IOException {
        QueueStore queueStore = new QueueStore(queuesDir.resolve(queueName));
        return new Queue(queueName, queueStore);
    }
}
