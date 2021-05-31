import java.io.*;
import java.net.*;
import java.util.*;


class SendMessage implements Runnable {
    
    // Port for the connection.
    DatagramSocket clientSendSocket;
    String username;

    Scanner myScanner = new Scanner(System.in);

    InetAddress initialAddress;
    DatagramPacket initialPacket;
    
    // Construction to set the socket.
    SendMessage(DatagramSocket socket) throws Exception {

        clientSendSocket = socket;

    	System.out.println("Username: ");
    	username = myScanner.nextLine();

    	System.out.println("/about to see what this project is about.\n/exit to exit the chat server.");

    	String connectedString = "User (" + username + ") has connected the chat server.";

    	byte initialConnection[] = connectedString.getBytes();

    	initialAddress = InetAddress.getByName("10.244.128.37");
    	initialPacket = new DatagramPacket(initialConnection, initialConnection.length, initialAddress, 1234);
            
        clientSendSocket.send(initialPacket);
    }
    
    // Function to send message to other clients.
    private void sendMessage(String str) throws Exception {
        
        String sendStr = "(" + username + "): " + str;
        // Byte array to save the string as bytes.

    	if (str == "/exit\n") {
    	    System.exit(0);
    	}

        byte buffer[] = sendStr.getBytes();
        //Saving the address.
        InetAddress address = InetAddress.getByName("10.244.128.37");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 1234);
        
        // Sending the saved byteArray with the address and the port.
        clientSendSocket.send(packet);
    }
    
    public void run() {
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        while (true) {
            try {
                while (!in.ready()) {
                
                    Thread.sleep(100);
                }
		String readLine = in.readLine();
		if (readLine == "/exit") {
		    System.exit(0);
		} else {
		    sendMessage(readLine);
		}
            } catch(Exception e) {
            
                System.err.println(e);
            }
        }
    }
}

class ReceiveMessage implements Runnable {
    
    // Create the sockets
    DatagramSocket clientReceiveSocket;
    // Byte array to receive
    byte buffer[];
    
    // Constructor to set the socket and the byte arrays length.
    ReceiveMessage(DatagramSocket socket) {
    
        clientReceiveSocket = socket;
        buffer = new byte[1024];
    }
    
    public void run() {
    
        while (true) {
        
            try {
                
                // New packet is created with the length and type of the buffer.
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                // Received the packet from other clients.
                clientReceiveSocket.receive(packet);
                // Received packet to a new string with the contents of the packet.
                String received = new String(packet.getData(), 0, packet.getLength());
                // Printing the newly created string.
                System.out.println(received);
            } catch(Exception e) {
            
                System.err.println(e);
            }
        }
    }
}

public class UDPClient {

    public static void main(String args[]) throws Exception {
    
        DatagramSocket socket = new DatagramSocket();
        ReceiveMessage receiver = new ReceiveMessage(socket);
        SendMessage sender = new SendMessage(socket);
        Thread receiverThread = new Thread(receiver);
        Thread senderThread = new Thread(sender);
        // We don't know who will send or who will receive first. So we use threads in order 
        // to make it usefull more like a regular chat application.
        receiverThread.start();
        senderThread.start();
    }
}
