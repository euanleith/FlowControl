import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.Scanner;

/**
 *
 * Client class
 *
 * An instance accepts user input
 *
 */
public class Worker extends Node {
    private static final int DEFAULT_DST_PORT = 50001;
    private String name;//~

    //~map.compute() {} computeIfAbsent()
    //map<port[], queue[]>
    //queue is packet and timer thing
    //map  is a single thing
    //each port has a queue of packets,
    //each packet has a  timer thing, check if it  is


    /**
     * Constructor
     *
     * Attempts to create socket at given port
     */
    Worker(int srcPort) {
        timer.addPort(DEFAULT_DST_PORT);
        try {
            //TODO port should be random
            socket= new DatagramSocket(srcPort);
            listener.go();
        }
        catch(java.lang.Exception e) {e.printStackTrace();}
    }

    /**
     * Process incoming packet
     * @param packet A packet that has been received
     */
    public synchronized void onReceipt(DatagramPacket packet) {
        try {
            PacketContent content = PacketContent.fromDatagramPacket(packet);
            System.out.println("Received packet: " + content.toString());

            switch (content.getType()) {
                case PacketContent.FILEFUNC:

                    if (packet.getPort() != -1) {//~
                        sendAck(packet);
                    }

                    String fname = ((FileFuncContent) content).getFileName();
                    String func = ((FileFuncContent) content).getFunc();

                    switch (func) {
                        case ASSIGNMENT:
                            System.out.print("Work assignment: ");
                            Main.printFile(fname);
                            break;
                        case VOLUNTEER:
                        case WITHDRAW:
                        case RESULTS:
                            FileFuncContent sent = sendFileFunc(fname, DEFAULT_DST_PORT, NULL, func);
                            timer.add(sent, DEFAULT_DST_PORT);
                            break;
                        default:
                            System.out.println("Invalid func request: " + func);
                    }
                    break;
                case PacketContent.ACKPACKET:
                    timer.remove(packet.getPort());
                    break;
                default:
                    sendAck(packet, "Received invalid packet type");
                    System.out.println("Received invalid packet type");
            }
        } catch(Exception e) {e.printStackTrace();}
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
        Scanner scan = new Scanner(System.in);
        System.out.print("port: ");
        try {
            (new Worker(scan.nextInt())).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }
}
