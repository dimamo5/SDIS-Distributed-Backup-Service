package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {

    void backupFile(String filename, int replicationDegree, boolean enhancement_ON) throws RemoteException;

    void restoreFile(String filename) throws RemoteException;

    void deleteFile(String filename,boolean enhancement_ON) throws RemoteException;

    void spaceReclaim(long amount) throws RemoteException;



}
