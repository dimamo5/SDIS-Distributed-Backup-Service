import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by diogo on 17/03/2016.
 */
public class MDRChannel implements Runnable{
    public final int MAX_SIZE = 645000;

    public MulticastSocket socket;

    private boolean running=true;

    public InetAddress address;
    public int port;

    MDRChannel(InetAddress address, int port){
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
    }


    @Override
    public void run() {
        byte buf[]= new byte[MAX_SIZE];

        while(this.running){
            DatagramPacket packet = new DatagramPacket(buf,buf.length);

            try {
                socket.receive(packet);
                //TODO HANDLE PACKET

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}
