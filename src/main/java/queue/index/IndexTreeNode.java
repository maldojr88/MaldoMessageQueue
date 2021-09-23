package queue.index;

import java.nio.ByteBuffer;

public class IndexTreeNode implements Comparable{
    private long id;
    private long offset;
    private int length;

    public IndexTreeNode(long id, long offset, int length){
        this.id = id;
        this.offset = offset;
        this.length = length;
    }

    public static int getNodeNumBytes(){
        return Long.BYTES + Long.BYTES + Integer.BYTES;
    }

    public static IndexTreeNode from(byte[] bytes, int startIdx){
        ByteBuffer buf = ByteBuffer.allocate(getNodeNumBytes());
        buf.put(bytes, startIdx, getNodeNumBytes());
        buf.flip();
        long id = buf.getLong();
        long offset = buf.getLong();
        int length = buf.getInt();
        return new IndexTreeNode(id,offset,length);
    }

    public long getId(){
        return id;
    }

    public byte[] serialize(){
        ByteBuffer buf = ByteBuffer.allocate(getNodeNumBytes());
        buf.putLong(id);
        buf.putLong(offset);
        buf.putInt(length);
        return buf.array();
    }

    @Override
    public int compareTo(Object o) {
        IndexTreeNode other = (IndexTreeNode) o;
        return (int) (id - other.id);
    }
}
