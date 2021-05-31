import java.io.*;
import java.net.*;
import java.util.*;

public class UDPServer extends Thread {

    private DatagramSocket serverSocket;
    private ArrayList<InetAddress> clientAddresses;
    private ArrayList<Integer> clientPorts;
    private ArrayList<String> existingClients;

    private InetAddress clientToSendAddress;
    private int clientToSendPort;

    DatagramPacket receivePacket;
    String packetContent;
    InetAddress clientAddress;
    int clientPort;
    String addressAndPort;

    DatagramPacket sendPacket;
    
    public UDPServer() throws IOException {

        serverSocket = new DatagramSocket(1234);
        clientAddresses = new ArrayList();
        clientPorts = new ArrayList();
        existingClients = new ArrayList();
    }

    public void run() {
        
        byte[] buffer = new byte[1024];
        while (true) {
        
            try {

                Arrays.fill(buffer, (byte)0);
                
                // DGPacket to receive the incoming packets.
                receivePacket = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(receivePacket);
                
                //Saving the packetContents of the packets.
                packetContent = new String(buffer, buffer.length);
                
                // Getting the address of the client that sent the packet.
                clientAddress = receivePacket.getAddress();
                // Getting the port of the client that sent the packet.
                clientPort = receivePacket.getPort();
                
                // Saving the clients id to an array list.
                addressAndPort = clientAddress.toString() + ":" + clientPort;
                
                // If we already have the id, then don't save it.
                if (existingClients.contains(addressAndPort) == false) {

                    existingClients.add( addressAndPort );
                    clientPorts.add( clientPort );
                    clientAddresses.add(clientAddress);
                }
                
                // Printing everything to the server screen, kind of like an administrator.
                System.out.println(packetContent);

                byte[] data = (packetContent).getBytes();

                for (int i = 0; i < clientAddresses.size(); i++) {
                
                    // Sending the information to other clients.
                    clientToSendAddress = clientAddresses.get(i);
        		    clientToSendPort = clientPorts.get(i);

    		    	sendPacket = new DatagramPacket(data, data.length, clientToSendAddress, clientToSendPort);
                    serverSocket.send(sendPacket);
                }
                
            } catch(Exception exception) {
                    	
        		
            }
        }
    }

    public static void main(String args[]) throws Exception {

        UDPServer connection = new UDPServer();
        connection.start();
    }
}
