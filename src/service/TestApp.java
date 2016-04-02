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
        boolean enhancement_ON = false;

        if(args.length >=3) {
            ap=args[0];

            if(args[1].matches("(BACKUP|RESTORE|RECLAIM|DELETE)")){
                subProtocol=args[1];
            }else if(args[1].matches("(BACKUPENH|DELETEENH)")){  //enhancement
                if(args[1].equals("BACKUPENH")){
                    subProtocol = "BACKUP";
                }
                else{
                    subProtocol = "DELETE";
                }
                enhancement_ON = true;
            }
            else{
                System.err.println("Error in Sub Protocol");
                System.exit(-1);
            }
            opnd1=args[2];

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
                opnd2=args[3];
                stub.backupFile(opnd1,Integer.parseInt(opnd2),enhancement_ON);
                break;
            case "RESTORE":
                stub.restoreFile(opnd1);
                break;
            case "DELETE":
                stub.deleteFile(opnd1,enhancement_ON);
                break;
            case "RECLAIM":
                stub.spaceReclaim(Integer.parseInt(opnd1));
        }
    }
}
