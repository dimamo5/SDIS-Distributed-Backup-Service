package service;

import java.rmi.Remote;

/**
 * Created by diogo on 24/03/2016.
 */
public interface RMIInterface extends Remote {
    public void backupFile(String filename, int replicationDegree);

    public void restoreFile(String filename);

    public void deleteFile(String filename);

    public void spaceReclaim(int amount);



}
