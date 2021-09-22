package queue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private long checksum;

    public QueueEntry(long instant, byte[] bytes){
        this.instant = instant;
        this.bytes = bytes;
        setChecksum();
    }

    public long getInstant(){
        return instant;
    }

    public int getByteSize(){
        return Long.BYTES + bytes.length + Long.BYTES;
    }

    public byte[] pack(){
        ByteBuf buf = Unpooled.copyLong(instant);
        buf.writeBytes(bytes);
        buf.writeLong(checksum);
        byte[] ret = new byte[buf.readableBytes()];
        buf.readBytes(ret);
        return ret;
    }

    private void setChecksum() {
        CRC32 crc = new CRC32();
        crc.update(this.bytes);
        long checksum = crc.getValue();
        log.info("checksum computed =>" + checksum);
        this.checksum = checksum;
    }
}
