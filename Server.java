import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server extends Node {
    private static final int DEFAULT_SRC_PORT = 50000; // server port
    private static final int DEFAULT_DST_PORT = 50001; // broker port

    /**
     * Constructor
     *
     * Attempts to create a socket at a constant port
     * Initialises queue of packets
     */
    Server() {
        timer.addPort(DEFAULT_DST_PORT);
        try {
            socket= new DatagramSocket(DEFAULT_SRC_PORT);
            listener.go();
        }
        catch(java.lang.Exception e) {e.printStackTrace();}
    }

    /**
     * Process incoming packet
     * @param packet A packet that has been received
     */
    public void onReceipt(DatagramPacket packet) {
        try {

            PacketContent content = PacketContent.fromDatagramPacket(packet);
            System.out.println("Received packet: " + content.toString());

            switch (content.getType()) {
                case PacketContent.FILEFUNC:
                    if (packet.getPort() != -1) {//~
                        sendAck(packet);
                    }

                    String fname = ((FileFuncContent) content).getFileName();
                    String ports = ((FileFuncContent) content).getPorts();
                    String func = ((FileFuncContent) content).getFunc();

                    switch (func) {
                        case ASSIGNMENT:
                            FileFuncContent sent = sendFileFunc(fname, DEFAULT_DST_PORT, ports, func);
                            timer.add(sent, DEFAULT_DST_PORT);
                            break;
                        default:
                            System.out.println("Invalid func request: " + func);
                    }
                    break;
                case PacketContent.ACKPACKET:
                    System.out.println(packet.getPort());
                    timer.remove(packet.getPort());
                    //TODO what if workers given were invalid?
                    break;
                default:
                    sendAck(packet, "Received invalid packet type");
                    System.out.println("Received invalid packet type");
            }
        } catch(Exception e) {e.printStackTrace(); }
    }

    /**
     * Listen for incoming packets
     * @throws Exception
     */
    public synchronized void start() throws Exception {
        System.out.println("Waiting for contact");
        this.wait();
    }

    public static void main(String[] args) {
        try {
            (new Server()).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }
}
