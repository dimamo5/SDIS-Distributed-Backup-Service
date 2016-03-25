package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by diogo on 24/03/2016.
 */
public interface RMIInterface extends Remote {

    public void backupFile(String filename, int replicationDegree) throws RemoteException;

    public void restoreFile(String filename) throws RemoteException;

    public void deleteFile(String filename) throws RemoteException;

    public void spaceReclaim(int amount) throws RemoteException;



}
