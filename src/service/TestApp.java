package service;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by Sonhs on 23/03/2016.
 */
public class TestApp {

    public TestApp() {
    }

    public static void main(String args[]) throws RemoteException {
        String ap=null,subProtocol=null, opnd1=null,opnd2=null;
        RMIInterface stub=null;

        if(args.length ==4) {
            ap=args[0];

            if(args[1].matches("(BACKUP|RESTORE|RECLAIM|DELETE)")){
                subProtocol=args[1];
            }else{
                System.err.println("Error in Sub Protocol");
                System.exit(-1);
            }
            opnd1=args[2];
            opnd2=args[3];

        }else{
            System.err.println("Error in number of arguments");
        }

        try {
            Registry registry = LocateRegistry.getRegistry();
            stub = (RMIInterface) registry.lookup(args[0]);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

        switch(subProtocol){
            case "BACKUP":
                stub.backupFile(opnd1,Integer.parseInt(opnd2));
                break;
            case "RESTORE":
                stub.restoreFile(opnd1);
                break;
            case "DELETE":
                stub.deleteFile(opnd1);
                break;
            case "RECLAIM":
                stub.spaceReclaim(Integer.parseInt(opnd1));
        }
    }
}
