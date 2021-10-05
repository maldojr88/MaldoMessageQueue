package queue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

/**
 * TODO
 *  1. Add metadata?
 *  2. Optimize Buf and readable bytes
 */
public class QueueEntry {
    private static final Logger log = LogManager.getLogger(QueueEntry.class);
    private long instant;
    private final byte[] bytes;
    private final String msg;
    private long checksum;

    public QueueEntry(long instant, String msg){
        this.instant = instant;
        this.bytes = msg.getBytes(StandardCharsets.UTF_8);;
        this.msg = msg;
        setChecksum();
    }

    public long getInstant(){
        return instant;
    }

    public int getByteSize(){
        return Integer.BYTES + Long.BYTES + Integer.BYTES + bytes.length + Long.BYTES;
    }

    public byte[] pack(){
        ByteBuf buf = Unpooled.copyInt(getByteSize());
        buf.writeLong(instant);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        buf.writeLong(checksum);
        byte[] ret = new byte[buf.readableBytes()];
        buf.readBytes(ret);
        return ret;
    }

    public static QueueEntry unpack(ByteBuffer buf) throws IOException {
        int entryNumBytes = buf.getInt();
        long instant = buf.getLong();
        int msgNumBytes = buf.getInt();
        byte[] msgBytes = new byte[msgNumBytes];
        buf.get(msgBytes);
        String msg = new String(msgBytes);
        long crc = buf.getLong();
        if(crc != getChecksum(msgBytes)){
            throw new IOException("Corrupted data file");
        }
        return new QueueEntry(instant, msg);
    }

    private void setChecksum() {
        CRC32 crc = new CRC32();
        crc.update(this.bytes);
        long checksum = crc.getValue();
        log.info("checksum computed =>" + checksum);
        this.checksum = checksum;
    }

    private static long getChecksum(byte[] bytes){
        CRC32 crc = new CRC32();
        crc.update(bytes);
        return crc.getValue();
    }

    @Override
    public String toString(){
        return String.format("%d - Instant, Msg={%s}", instant, msg);
    }
}
