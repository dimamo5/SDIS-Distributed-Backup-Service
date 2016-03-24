package service;

import channel.MCChannel;
import channel.MDBChannel;
import channel.MDRChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by Sonhs on 12/03/2016.
 */
public class Peer {

    public enum Channels{
        MC, MDB, MDR;
    }

    private String id;

    //valores por defeito
    private String MC_ip ="224.0.0.1", MDB_ip="224.0.1.0", MDR_ip="224.1.0.0";
    private int MC_port = 10001, MDB_port = 10002, MDR_port = 10003;

    //multicast channels
    private static MCChannel MC_channel;
    private static MDBChannel MDB_channel;
    private static MDRChannel MDR_channel;

    private static InetAddress ip;

    private static int port;

    private static MulticastSocket comunication_socket;

    Peer(String params[]){
        initAttr(params);
    }

    /* METHODS */

    private void initAttr(String args[]){

        this.id = args[0];

        if(args.length > 1) { //reconfigure MCchannel's ip/port
            MC_ip = args[1];
            MC_port = Integer.parseInt(args[2]);
            MDB_ip = args[3];
            MDB_port = Integer.parseInt(args[4]);
            MDR_ip = args[5];
            MDR_port = Integer.parseInt(args[6]);
        }

        try {
            MC_channel = new MCChannel(InetAddress.getByName(MC_ip),MC_port);
            MDB_channel = new MDBChannel(InetAddress.getByName(MDB_ip),MDB_port);
            MDR_channel = new MDRChannel(InetAddress.getByName(MDR_ip),MDR_port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Error initializing MC channel(s)");
        }

        try {
            comunication_socket = new MulticastSocket();
            comunication_socket.setTimeToLive(1);
            comunication_socket.joinGroup(InetAddress.getByName(this.MC_ip));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing service.Peer.MulticastSocket / joining group");
        }

    }

    public static MCChannel getMC_channel() {
        return MC_channel;
    }

    public static MDBChannel getMDB_channel() {
        return MDB_channel;
    }

    public static MDRChannel getMDR_channel() {
        return MDR_channel;
    }

    public static MulticastSocket getComunication_socket() {
        return comunication_socket;
    }


    public static int getPort() {
        return port;
    }

    public static InetAddress getIp() {
        return ip;
    }


}
