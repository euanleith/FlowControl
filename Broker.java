import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class Broker extends Node {

    private static final int DEFAULT_SRC_PORT = 50001;

    /**
     * Constructor
     *
     * Attempts to create socket at a constant port
     * Initialises workers
     */
    Broker() {
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
                case PacketContent.FILEFUNC: // forward the file to the given ports

                    if (packet.getPort() != -1) {//~
                        sendAck(packet);
                    }

                    String fname = ((FileFuncContent) content).getFileName();
                    String ports = ((FileFuncContent) content).getPorts();
                    String func = ((FileFuncContent) content).getFunc();

                    switch (func) {

                        // To be forwarded
                        case ASSIGNMENT:
                            ArrayList<Integer> dstPorts = parsePorts(ports);

                            // send packet to given ports TODO make func
                            String invalidPorts = "";
                            for (int port : dstPorts) {
                                if (timer.getPorts().contains(port)) {
                                    FileFuncContent sent = sendFileFunc(fname, port, NULL, func);
                                    timer.add(sent, port);
                                } else {
                                    System.out.println("Received assignment request for invalid worker: " + port);
                                    invalidPorts += port + ",";
                                }
                            }

                            //only send ack once all sent have been acknowledged...
//                            if (invalidPorts.equals("")) {
//                                sendAck(packet);
//                            } else {
//                                sendAck(packet, "Received this with invalid workers; " + invalidPorts, content.getFrame());
//                            }
                            break;

                        // Not to be forwarded
                        case VOLUNTEER:
                            timer.addPort(packet.getPort());
                            System.out.println(packet.getPort() + " volunteered");
                            break;
                        case WITHDRAW:
                            timer.removePort(packet.getPort());
                            System.out.println(packet.getPort() + " withdrew");
                            break;
                        case RESULTS:
                            System.out.println("Received results from " + packet.getPort() + ":");
                            Main.printFile(((FileFuncContent) content).getFileName());
                            break;
                        default:
                            System.out.println("Invalid function request");
                            break;
                    }
                    break;
                case PacketContent.ACKPACKET:
                    timer.remove(packet.getPort());
                    break;
                default:
                    sendAck(packet, "Received invalid packet type");
                    System.out.println("Received invalid packet type");
            }

        } catch (Exception e) {e.printStackTrace();}
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
            (new Broker()).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }
}
