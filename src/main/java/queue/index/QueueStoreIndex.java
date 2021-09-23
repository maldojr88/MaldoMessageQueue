package queue.index;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.TreeSet;

public class QueueStoreIndex {
    private static final Logger log = LogManager.getLogger(QueueStoreIndex.class);
    private static int dummyCounter = 0;
    private final Path path;
    private TreeSet<IndexTreeNode> tree;

    public QueueStoreIndex(Path path) throws IOException {
        this.path = path;
        if(Files.notExists(path)){
            Files.createFile(this.path);
        }
        setTree();
    }

    private void setTree() throws IOException {
        if(Files.exists(path)){
            this.tree = loadTreeFromFile();
        }else{
            this.tree = new TreeSet<>();
        }
    }

    private TreeSet<IndexTreeNode> loadTreeFromFile() throws IOException {
        log.info("Loading Index from file");
        TreeSet<IndexTreeNode> tree = new TreeSet<>();
        int nodeSize = IndexTreeNode.getNodeNumBytes();

        byte[] bytes = Files.readAllBytes(path);//change this to read larger files
        if(bytes.length % nodeSize != 0){
            throw new IOException("Corrupted index file - treeNodeSize= " + nodeSize + " bytes=" + bytes.length);
        }

        for(int i=0; i < bytes.length; i += nodeSize){
            IndexTreeNode node = IndexTreeNode.from(bytes, i);
            log.info("Read - " + node.getId());
            tree.add(node);
        }

        return tree;
    }

    public void append(IndexTreeNode treeNode) throws IOException {
        tree.add(treeNode);
        write();
        dummyCounter++;
        if(dummyCounter == 4){
            log.info("Recreating from file");
            loadTreeFromFile();
        }
    }

    public void write() throws IOException {
        byte[] serializedTree = new byte[tree.size() * IndexTreeNode.getNodeNumBytes()];
        int idx = 0;
        Iterator<IndexTreeNode> it = tree.iterator();
        while (it.hasNext()){
            IndexTreeNode next = it.next();
            log.info(next.getId());
            byte[] serialize = next.serialize();
            System.arraycopy(serialize, 0, serializedTree, idx, IndexTreeNode.getNodeNumBytes());
            idx += IndexTreeNode.getNodeNumBytes();
        }
        log.info("Writing index file of size " + serializedTree.length);
        Files.write(path, serializedTree, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
