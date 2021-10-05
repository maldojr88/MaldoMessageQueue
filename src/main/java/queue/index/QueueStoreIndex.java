package queue.index;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

public class QueueStoreIndex {
    private static final Logger log = LogManager.getLogger(QueueStoreIndex.class);
    private final Path path;
    private HashMap<Long, IndexTreeNode> hash;//TODO - change to treeMap to ensure deterministic order

    public QueueStoreIndex(Path path) throws IOException {
        this.path = path;
        if(Files.notExists(path)){
            Files.createFile(this.path);
        }
        setTree();
    }

    private void setTree() throws IOException {
        if(Files.exists(path)){
            this.hash = loadTreeFromFile();
        }else{
            this.hash = new HashMap<>();
        }
    }

    private HashMap<Long, IndexTreeNode> loadTreeFromFile() throws IOException {
        log.info("Loading Index from file");
        HashMap<Long, IndexTreeNode> tree = new HashMap<>();
        int nodeSize = IndexTreeNode.getNodeNumBytes();

        byte[] bytes = Files.readAllBytes(path);//TODO change this to read larger files
        if(bytes.length % nodeSize != 0){
            throw new IOException("Corrupted index file - treeNodeSize= " + nodeSize + " bytes=" + bytes.length);
        }

        for(int i=0; i < bytes.length; i += nodeSize){
            IndexTreeNode node = IndexTreeNode.from(bytes, i);
            log.info("Read - " + node.getId());
            tree.put(node.getId(), node);
        }

        return tree;
    }

    /**
     * TODO
     * Very suboptimal to write the entire tree on every append. Need to find an algorithm
     * to efficiently and lazily append to a tree. This part can be scoped for future work
     */
    public void append(IndexTreeNode treeNode) throws IOException {
        hash.put(treeNode.getId(), treeNode);
        write();
    }

    public void write() throws IOException {
        byte[] serializedTree = new byte[hash.size() * IndexTreeNode.getNodeNumBytes()];
        int idx = 0;
        for (IndexTreeNode next : hash.values()) {
            log.info(next.getId());
            byte[] serialize = next.serialize();
            System.arraycopy(serialize, 0, serializedTree, idx, IndexTreeNode.getNodeNumBytes());
            idx += IndexTreeNode.getNodeNumBytes();
        }
        /*Iterator<IndexTreeNode> it = hash.iterator();
        while (it.hasNext()){
            IndexTreeNode next = it.next();
            log.info(next.getId());
            byte[] serialize = next.serialize();
            System.arraycopy(serialize, 0, serializedTree, idx, IndexTreeNode.getNodeNumBytes());
            idx += IndexTreeNode.getNodeNumBytes();
        }*/
        log.info("Writing index file of size " + serializedTree.length);
        Files.write(path, serializedTree, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public long searchOffset(long instant) throws IOException {
        validate(instant);
        IndexTreeNode indexTreeNode = hash.get(instant);
        log.info("Index file points to " + indexTreeNode.getOffset() + " offset in the data file");
        return indexTreeNode.getOffset();
    }

    public IndexTreeNode getEntryFromInstant(long instant) throws IOException {
        validate(instant);
        return hash.get(instant);
    }

    private void validate(long instant) throws IOException {
        if(!hash.containsKey(instant)){
            throw new IOException("Instant " + instant + " not found in index file");
        }
    }
}
