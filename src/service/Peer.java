package service;

import channel.*;
import database.Disk;
import protocol.Backup;
import protocol.Delete;
import protocol.Restore;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Sonhs on 12/03/2016.
 */
public class Peer implements RMIInterface{

    public enum Channels{
        MC, MDB, MDR;
    }

    private static Disk disk =new Disk();

    private static String id;
    private static InetAddress ip;
    private static int port;

    //valores por defeito
    private String MC_ip ="224.0.0.1", MDB_ip="224.0.1.0", MDR_ip="224.1.0.0";
    private int MC_port = 10001, MDB_port = 10002, MDR_port = 10003;

    //multicast channels
    private static MCChannel MC_channel;
    private static MDBChannel MDB_channel;
    private static MDRChannel MDR_channel;


    private static MulticastSocket comunication_socket;

    public Peer(String params[]){
        initAttr(params);
    }

    public static void main(String[] args){
        Peer p=new Peer(args);

        /*try {

            RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(p, 0);

            // Bind the remote object's stub in the registry
            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(p.id, stub);

            System.err.println("Peer ready");
        }catch(Exception e){
            System.err.println("Peer exception: " + e.toString());
            e.printStackTrace();
        }*/

        try {
            comunication_socket = new MulticastSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(MC_channel).start();
        new Thread(MDB_channel).start();
        new Thread(MDR_channel).start();

        if(args.length==8)
            p.backupFile("texto.txt",1);
    }

    /* METHODS */

    private void initAttr(String args[]){

        if(args.length == 0){
            System.out.println("Incorrect number of args");
        }

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

            this.ip=comunication_socket.getInetAddress();
            this.port=comunication_socket.getPort();
            //TODO JUNTAR-SE AOS OUTROS GRUPOS ?

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

    public static String getId() {
        return id;
    }

    public static Disk getDisk() {
        return disk;
    }

    public static void loadDisk(){

    }

    public static void saveDisk(){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("db.data");

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    fileOutputStream);

            objectOutputStream.writeObject(disk);

            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            System.err.println("Database not found");

            disk=new Disk();

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void backupFile(String filename, int replicationDegree) {
        System.out.println("Starting Backing file: "+filename);
        new Thread(new Backup(filename,replicationDegree)).start();
    }

    @Override
    public void restoreFile(String filename) {
        System.out.println("Starting Restoring file: "+filename);
        Restore r=new Restore(filename);
        this.MDR_channel.addObserver(r);
        new Thread(r).start();
    }

    @Override
    public void deleteFile(String filename) {
        System.out.println("Starting Deteling file: "+filename);
        new Thread(new Delete(filename)).start();
    }

    @Override
    public void spaceReclaim(int amount) {
        //TODO Call Initiator
        System.out.println("BACKUP");
    }

}
