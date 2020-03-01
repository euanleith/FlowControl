import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileFuncContent extends PacketContent {

    private String filename;
    private int size;
    private String ports; // ports to send to, null at leaf
    private String func; // info on what the node is to do with the packet

    /**
     * Constructor
     *
     * @param filename File name
     * @param size Size of file
     * @param ports Ports at ~root
     * @param func Function to be implemented at receiving end
     */
    FileFuncContent(String filename, int size, String ports, String func) {
        type= FILEFUNC;
        this.filename = filename;
        this.size = size;
        this.ports = ports;
        this.func = func;
    }

    /**
     * Constructs an object out of a datagram packet.
     * @param oin Packet that contains information about a file.
     */
    FileFuncContent(ObjectInputStream oin) {
        try {
            type= FILEFUNC;
            filename = oin.readUTF();
            size = oin.readInt();
            ports = oin.readUTF();
            func = oin.readUTF();
        }
        catch(Exception e) {e.printStackTrace();}
    }

    /**
     * Writes the content into an ObjectOutputStream
     *
     */
    protected void toObjectOutputStream(ObjectOutputStream oout) {
        try {
            oout.writeUTF(filename);
            oout.writeInt(size);
            oout.writeUTF(ports);
            oout.writeUTF(func);
        }
        catch(Exception e) {e.printStackTrace();}
    }


    /**
     * Returns the content of the packet as String.
     *
     * @return Returns the content of the packet as String.
     */
    public String toString() {
        return "Filename: " + filename +
                " - Size: " + size +
                " - Ports: " + ports +
                " - Func: " + func;
    }

    /**
     * Returns the file name contained in the packet.
     *
     * @return Returns the file name contained in the packet.
     */
    public String getFileName() {
        return filename;
    }

    /**
     * Returns the file size contained in the packet.
     *
     * @return Returns the file size contained in the packet.
     */
    public int getFileSize() {
        return size;
    }

    /**
     * Returns the ports contained in the packet.
     *
     * @return Returns the ports contained in the packet.
     */
    public String getPorts() { return ports; }

    /**
     * Returns the function contained in the packet.
     *
     * @return Returns the function contained in the packet.
     */
    public String getFunc() {
        return func;
    }
}
