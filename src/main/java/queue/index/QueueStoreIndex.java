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
    private final Path path;
    private TreeSet<IndexTreeNode> tree;

    public QueueStoreIndex(Path path) throws IOException {
        this.path = path;
        if(Files.notExists(path)){
            Files.createFile(this.path);
        }
        setTree();
    }

    private void setTree() {
        if(Files.exists(path)){
            this.tree = loadTreeFromFile();
        }else{
            this.tree = new TreeSet<>();
        }
    }

    private TreeSet<IndexTreeNode> loadTreeFromFile() {
        log.info("Loading Index from file");
        TreeSet<IndexTreeNode> tree = new TreeSet<>();
        int nodeSize = IndexTreeNode.getNodeLength();
        //TODO - finish implementation
        return tree;
    }

    public void append(IndexTreeNode treeNode) throws IOException {
        tree.add(treeNode);
        write();
    }

    public void write() throws IOException {
        byte[] serializedTree = new byte[tree.size() * IndexTreeNode.getNodeLength()];
        int idx = 0;
        Iterator<IndexTreeNode> it = tree.iterator();
        while (it.hasNext()){
            IndexTreeNode next = it.next();
            byte[] serialize = next.serialize();
            System.arraycopy(serialize, 0, serializedTree, idx, IndexTreeNode.getNodeLength());
            idx += IndexTreeNode.getNodeLength();
        }
        log.info("Writing index file of size " + serializedTree.length);
        Files.write(path, serializedTree, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
