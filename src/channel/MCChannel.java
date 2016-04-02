package channel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Observable;
import java.util.Observer;

import service.*;

/**
 * Created by diogo on 17/03/2016.
 */
public class MCChannel extends Observable implements Runnable{

    public final int MAX_SIZE = 64500;

    public MulticastSocket socket;

    private boolean running=true;

    private InetAddress address;
    private int port;

    public MCChannel(InetAddress address, int port){
        this.address = address;
        this.port = port;

        //open connection
        try {
            socket = new MulticastSocket(port);

            socket.setTimeToLive(1);

            socket.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("MC channel open!");
    }


    @Override
    public void run() {
        byte buf[]= new byte[MAX_SIZE];

        while(this.running){
            DatagramPacket packet = new DatagramPacket(buf,buf.length);

            try {
                socket.receive(packet);

                String senderIP = packet.getAddress().getHostAddress();

                if (!(senderIP.equals(Peer.getIp())&& Peer.getPort()==packet.getPort())) {
                    new Thread(new MessageHandler(packet)).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    @Override
    public void notifyObservers(Object arg) {
        this.setChanged();
        super.notifyObservers(arg);
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
