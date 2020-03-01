import java.io.*;
import java.util.ArrayList;

public class Main {
    /*
    TODO
    worker creation should inc name, and assign port behind the scenes
    send how you actually send messages better; variably create files
    method descriptions
    */

    /*
    Potential errors;
    what if 2 packets are sent together
    what if 2 separate packets are received at the same time
    what if file is bigger than 1 packet (need to send multiple)
    worker leaves without withdrawing
    potential duplicates if ack isn't received
    */

    /*
Go back n: keep sending frames, not waiting for acknowledgements
If an acknowledgement isn't received within a timer, go back to that frame and resend all subsequent ones
-TODO cumulative acknowledgements

wireshark for report
add protocol~?

might only need fileinfo, and inc which port(s) to send to if applicable, otherwise null


Instant, Duration, Clock
-have one for all, with map from packet to time
have thread which checks each of them

go back n should be per port
     */

    public static void main(String[] args) {
    }

    public static ArrayList<String> readFile(String fname) throws Exception {
        File file = new File(fname);

        BufferedReader br = new BufferedReader(new FileReader(file));

        ArrayList<String> str = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null){
            str.add(line);
        }
        return str;
    }

    public static void writeToFile(String fname, ArrayList<String> contents) throws  Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
        for (String line : contents) {
            writer.write(line);
        }
        writer.close();
    }

    public static void printFile(String fname) {
        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO error cases
    public static ArrayList<Integer> delimitedStringToIntArray(String str, String delimiter) {
        String[] strArr = str.split(delimiter);
        ArrayList<Integer> ports = new ArrayList<>(strArr.length);
        for (String s : strArr) {
            ports.add(Integer.parseInt(s));
        }
        return ports;
    }
}
/*
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class Server extends Node {
    private static final int DEFAULT_SRC_PORT = 50000; // server port
    private static final int DEFAULT_DST_PORT = 50001; // broker port

    private boolean status; // is waiting for acknowledgement
    private Queue<DatagramPacket> pendingPackets;

    /**
     * Constructor
     *
     * Attempts to create a socket at a constant port
     * Initialises queue of packets

Server() {
    status = true;
    pendingPackets = new LinkedList<>();
    try {
        socket= new DatagramSocket(DEFAULT_SRC_PORT);
        listener.go();
    }
    catch(java.lang.Exception e) {e.printStackTrace();}
}

    /**
     * Process incoming packet
     * @param packet A packet that has been received

    public void onReceipt(DatagramPacket packet) {
        if (!status) {
            pendingPackets.add(packet);
        }

        try {

            PacketContent content = PacketContent.fromDatagramPacket(packet);
            System.out.println("Received packet: " + content.toString());

            switch (content.getType()) {
                case PacketContent.FILEFORWARD:
                    if (status) {
                        sendAck(packet);

                        status = false; // wait until an acknowledgement is received

                        String fname = ((FileForwardContent) content).getFileName();
                        String ports = ((FileForwardContent) content).getPorts();
                        String func = ((FileForwardContent) content).getFunc();

                        Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (!status) {
                                    System.out.println("sending " + System.currentTimeMillis());
                                    sendForwardFile(fname, DEFAULT_DST_PORT, ports, func);
                                } else {
                                    this.cancel();
                                }
                            }
                        }, 0, Main.WAIT_TIME);
                    }
                    break;
                case PacketContent.FILEINFO:
                    if (status) {
                        //TODO
                        status = false;
                    }
                    break;
                case PacketContent.ACKPACKET:
                    status = true; // continue as acknowledgement is received
                    //TODO what if workers given were invalid?
                    break;
                default:
                    if (status) {
                        sendAck(packet, "Received invalid packet type");
                        System.out.println("Received invalid packet type");
                    }
            }
        } catch(Exception e) {e.printStackTrace(); }

        // process next packet in queue
        if (status && !pendingPackets.isEmpty()) {
            onReceipt(pendingPackets.remove());
        }
    }

    /**
     * Listen for incoming packets
     * @throws Exception

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

 */

/*
Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!status.get(sent.getFrame())) { // ack not received
                                System.out.println("Ack not received for frame " + sent.getFrame());
                                // set global frame
                                frame = sent.getFrame();
                                // go back n
                                for (PacketContent queuedPacket : queue) {
                                    onReceipt(queuedPacket.toDatagramPacket());
                                    queue.remove(queuedPacket);//~?
                                }
                            } else { // ack received
                                status.remove(sent.getFrame());
                                queue.remove(sent);
                            }
                        }
                    }, Main.WAIT_TIME);
 */