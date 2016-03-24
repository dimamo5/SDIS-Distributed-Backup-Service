package service;

/**
 * Created by Sonhs on 22/03/2016.
 */

public class Service {

    private Peer peer;

    public Service(String args[]){
        peer = new Peer(args);
    }

    public static void main(String args[]){

        if(checkParams(args) == false){
            printUsage();
            System.exit(1);
        }

        Service s = new Service(args);

        //TODO looperino

    }

    private static boolean checkParams(String args[]){

        if(args.length != 1 && args.length !=7){
            return false;
        }

        return true;
    }

    private static void printUsage(){

        System.out.println("Invalid arguments!!!\n Usage(1): service.Peer <server_id> <MC Address> <MC Port> <MDB Address> <MDB Port> <MDR Address> <MDR Port>");
        System.out.println("Usage(2): service.Peer <server_id>");
    }
}
