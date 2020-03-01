import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

public class User extends Node {

    private static final int DEFAULT_SRC_PORT = 50002;

    User() {
        try {
            socket= new DatagramSocket(DEFAULT_SRC_PORT);
            listener.go();
        }
        catch(java.lang.Exception e) {e.printStackTrace();}
    }

    public void onReceipt(DatagramPacket packet) {
    }

    public void start() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("input: ");
            String input = scan.next();
            switch (input) {
                case "Worker":
                    System.out.print("Port: ");
                    int port = scan.nextInt();
                    boolean cont = true;
                    while (cont) {
                        System.out.print("What do: ");
                        input = scan.next();
                        switch (input) {
                            case "volunteer":
                            case "results":
                            case "withdraw":
                                sendFileFunc(input + ".txt", port, NULL, input);//TODO func shouldn't just be input?
                            case "q":
                                cont = false;
                                break;
                            case "?":
                                System.out.println("volunteer, results, withdraw");
                        }
                    }
                    break;
                case "Server":
                    cont = true;
                    while (cont) {
                        System.out.print("What do: ");
                        input = scan.next();
                        switch (input) {
                            case "assignment":
                                System.out.print("To who? ");
                                input = scan.next();
                                sendFileFunc("assignment.txt", 50000, input, ASSIGNMENT);//TODO func shouldn't just be input?
                            case "q":
                                cont = false;
                                break;
                            case "?":
                                System.out.println("assignment");
                        }
                    }
                    break;
                case "q":
                    System.out.print("Bye");
                    System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        try {
            (new User()).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }
}
