import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public abstract class Node {
    private static final int PACKET_SIZE = 65536;

    // Packet functions
    final static String VOLUNTEER = "volunteer";
    final static String WITHDRAW = "withdraw";
    final static String RESULTS = "results";
    final static String ASSIGNMENT = "assignment";

    final static String NULL = "null"; //TODO name

    private final static int WAIT_TIME = 5 * (10 ^ 9);  //TODO name

    private static final String DEFAULT_DST_NODE = "localhost";

    DatagramSocket socket;

    // Listener
    Listener listener;
    private CountDownLatch latch;

    // AckTimer
    private Clock clock;
    private ConcurrentHashMap<Integer, Queue<PacketTimer>> queue;
    AckTimer timer;

    Node() {
        latch = new CountDownLatch(1);
        listener = new Listener();
        listener.setDaemon(true);
        listener.start();

        clock = Clock.systemDefaultZone();
        queue = new ConcurrentHashMap<>();
        timer = new AckTimer();
        timer.start();
    }


    /**
     * Process incoming packet
     * @param packet A packet that has been received
     */
    public abstract void onReceipt(DatagramPacket packet);

    /**
     * Send packet containing acknowledgement to ~whoever just send the packet
     * @param packet Packet to be acknowledged
     * @return TODO
     */
    AckPacketContent sendAck(DatagramPacket packet) {
        try {
            AckPacketContent ack = new AckPacketContent();
            DatagramPacket response = ack.toDatagramPacket();
            response.setSocketAddress(packet.getSocketAddress());
            socket.send(response);
            return ack;
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    /**
     * Send packet containing given acknowledgement to ~whoever just send the packet
     * @param packet Packet to be acknowledged
     * @param msg Acknowledgment message
     * @return TODO
     */
    AckPacketContent sendAck(DatagramPacket packet, String msg) {
        try {
            AckPacketContent ack = new AckPacketContent(msg);
            DatagramPacket response = ack.toDatagramPacket();
            response.setSocketAddress(packet.getSocketAddress());
            socket.send(response);
            return ack;
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    /**
     * Send packet containing file and function which is to be forwarded to given port
     * @param fname File name
     * @param port Destination port
     * @param ports Root ports~
     * @param func Function to be implemented at ~roots
     * @return TODO
     */
    FileFuncContent sendFileFunc(String fname, int port, String ports, String func) {
        try {
            File file = new File(fname);
            int size = (int) file.length();//TODO (int) is temp

            FileFuncContent content = new FileFuncContent(fname, size, ports, func);

            System.out.println("Sending packet w/ name & length");
            DatagramPacket packet = content.toDatagramPacket();
            InetSocketAddress dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, port);
            packet.setSocketAddress(dstAddress);
            socket.send(packet);
            System.out.println("Packet sent");
            return content;
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    ArrayList<Integer> parsePorts(String ports) {
        return ports.equals("all") ?
                timer.getPorts() :
                Main.delimitedStringToIntArray(ports, ",");
    }


    /**
     *
     * Listener thread
     *
     * Listens for incoming packets on a datagram socket and informs registered receivers about incoming packets.
     */
    class Listener extends Thread {

        /*
         *  Telling the listener that the socket has been initialized
         */
        public void go() {
            latch.countDown();
        }

        /*
         * Listen for incoming packets and inform receivers
         */
        public void run() {
            try {
                latch.await();
                // Endless loop: attempt to receive packet, notify receivers, etc
                while(true) {
                    DatagramPacket packet = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
                    socket.receive(packet);

                    onReceipt(packet);
                }
            } catch (SocketException e) {
            } catch (Exception e) {e.printStackTrace();}
        }
    }


    /**
     * Class containing a packet and when it was sent
     */
    private class PacketTimer{
        PacketContent content;
        Instant timeSent;

        PacketTimer(PacketContent content, Instant timeSent) {
            this.content = content;
            this.timeSent = timeSent;
        }
    }

    /**
     * Acknowledgement thread
     *
     * Checks if the sent packets have been acknowledged by a given time
     * If not, implements 'go back n' ack protocol
     */
    protected class AckTimer extends Thread {

        /**
         * Checks if the sent packets have been acknowledged by a given time
         * If not, implements 'go back n' ack protocol
         */
        public void run() {
            // Endless loop: checking packets have received acknowledgements by a given time
            while (true) {
                for (int port : queue.keySet()) {
                    queue.computeIfPresent(port, (k, packets)  -> {
                        if (!packets.isEmpty() &&
                                clock.instant().getNano() - packets.peek().timeSent.getNano() < WAIT_TIME) { // reached timeout
                            System.out.println("No ack received for " + packets.peek().content.toString());
                            for (PacketTimer packet : packets) { // resend packets
                                onReceipt(packet.content.toDatagramPacket());
                                packets.remove();
                            }
                        }
                        return packets;
                    });
                }
            }
        }

        void add(PacketContent packet, int dstPort) {
            queue.computeIfPresent(dstPort, (k, v) -> {
                v.add(new PacketTimer(packet,clock.instant()));
                return v;
            });
        }

        void remove(int dstPort) {
            queue.computeIfPresent(dstPort, (k, v) -> {
                if (!v.isEmpty()) {
                    v.remove();
                }
                return v;
            });
        }

        void addPort(int dstPort) {
            queue.computeIfAbsent(dstPort, v -> new LinkedList<>());
        }

        void removePort(int dstPort) {
            queue.remove(dstPort);
        }

        ArrayList<Integer> getPorts() {
            return new ArrayList<>(queue.keySet());
        }
    }
}