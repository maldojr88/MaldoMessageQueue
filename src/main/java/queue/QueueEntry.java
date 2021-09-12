package queue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.zip.CRC32;

/**
 * TODO
 *  1. Add metadata?
 */
public class QueueEntry {
    private static final Logger log = LogManager.getLogger(QueueEntry.class);
    private long id;
    private final byte[] bytes;
    private long checksum;

    public QueueEntry(long id, byte[] bytes){
        this.id = id;
        this.bytes = bytes;
        setChecksum();
    }

    public byte[] pack(){
        ByteBuf buf = Unpooled.copyLong(id);
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
