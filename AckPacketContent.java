import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents acknowledgements
 *
 */
public class AckPacketContent extends PacketContent {

    private String info;

    /**
     * Constructor
     *
     * @param info Message to be sent
     */
    AckPacketContent(String info) {
        type= ACKPACKET;
        this.info = info;
    }

    /**
     * Constructor
     *
     */
    AckPacketContent() {
        type= ACKPACKET;
        this.info = "OK - Received this";
    }

    /**
     * Constructs an object out of a datagram packet.
     * @param oin Packet that contains acknowledgment
     */
    protected AckPacketContent(ObjectInputStream oin) {
        try {
            type = ACKPACKET;
            info = oin.readUTF();
        }
        catch(Exception e) {e.printStackTrace();}
    }

    /**
     * Writes the content into an ObjectOutputStream
     *
     */
    protected void toObjectOutputStream(ObjectOutputStream oout) {
        try {
            oout.writeUTF(info);
        }
        catch(Exception e) {e.printStackTrace();}
    }



    /**
     * Returns the content of the packet as String.
     *
     * @return Content of the packet as String.
     */
    public String toString() {
        return "ACK:" + info;
    }

    /**
     * Returns the info contained in the packet.
     *
     * @return Info contained in the packet.
     */
    public String getPacketInfo() {
        return info;
    }
}
