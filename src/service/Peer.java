package service;

import channel.*;
import database.Disk;
import protocol.Backup;
import protocol.Delete;
import protocol.Reclaim;
import protocol.Restore;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by Sonhs on 12/03/2016.
 */
public class Peer implements RMIInterface{

    public enum Channels{
        MC, MDB, MDR;
    }

    private static boolean enhancements_ON = false;

    private static Disk disk = new Disk();

    private static String id;
    private static String ip;
    private static int port;

    //valores por defeito
    public String MC_ip ="224.0.0.1", MDB_ip="224.0.1.0", MDR_ip="224.1.0.0";
    private int MC_port = 10001, MDB_port = 10002, MDR_port = 10003;

    //multicast channels
    private static MCChannel MC_channel;
    private static MDBChannel MDB_channel;
    private static MDRChannel MDR_channel;

    private static MulticastSocket comunication_socket;

    public Peer(String params[]){
        initAttr(params);
        loadDisk();
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


        new Thread(MC_channel).start();
        new Thread(MDB_channel).start();
        new Thread(MDR_channel).start();

        if(args.length==8) {
            p.backupFile("texto1.txt", 1);
            //p.setEnhancements_ON(true);
            //p.restoreFile("texto.txt");
            // p.deleteFile("texto.txt");
        }

        //================= 3.4 ENHANCEMENT =================== */
        if(enhancements_ON){
            p.spaceReclaim(disk.getSpaceUsage());
        }
        //========================================================
    }

    /* METHODS */

    private void initAttr(String args[]){

        if(args.length == 0){
            System.out.println("Incorrect number of args");
        }

        id = args[0];

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
            ip=getIPv4().getHostAddress();
            port=comunication_socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
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

    public static String getIp() {
        return ip;
    }

    public static String getId() {
        return id;
    }

    public static Disk getDisk() {
        return disk;
    }

    public static void loadDisk(){

        try {
            FileInputStream fileInputStream = new FileInputStream("db"+id+".data");

            ObjectInputStream objectInputStream = new ObjectInputStream(
                    fileInputStream);

            disk = (Disk) objectInputStream.readObject();
            System.out.println("Disk loaded");

            objectInputStream.close();
        } catch (FileNotFoundException e) {
            System.err.println("Database not found.");

            disk=new Disk();
            saveDisk();
            System.out.println("New disk created.");

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveDisk(){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("db"+id+".data");

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    fileOutputStream);

            objectOutputStream.writeObject(disk);

            System.out.println("Disk saved");
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            System.err.println("Database not found");

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
        Restore r=new Restore(filename);
        MDR_channel.addObserver(r);
        new Thread(r).start();
    }

    @Override
    public void deleteFile(String filename) {
        System.out.println("Starting Deteling file: "+filename);
        new Thread(new Delete(filename)).start();
    }

    @Override
    public void spaceReclaim(long amount) {
        System.out.println("Starting Reclaiming: "+amount);
        Reclaim r= new Reclaim(amount);
        MDR_channel.addObserver(r);
        new Thread(r).start();
    }

    //Funcao retirada de Henrique Ferrolho
    public static InetAddress getIPv4() throws IOException {
        MulticastSocket socket = new MulticastSocket();
        socket.setTimeToLive(0);

        InetAddress addr = InetAddress.getByName("225.0.0.0");
        socket.joinGroup(addr);

        byte[] bytes = new byte[0];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, addr,
                socket.getLocalPort());

        socket.send(packet);
        socket.receive(packet);

        socket.close();

        return packet.getAddress();
    }

    public static boolean isEnhancements_ON() {
        return enhancements_ON;
    }

    public static void setEnhancements_ON(boolean enhancements_ON) {
        Peer.enhancements_ON = enhancements_ON;
    }

    public static void setDisk(Disk disk) {
        Peer.disk = disk;
    }
}
