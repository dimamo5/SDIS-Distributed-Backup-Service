package protocol;

import database.Chunk;
import database.StoredChunk;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by diogo on 26/03/2016.
 */
public class Backup implements Runnable{

    String filename;
    int replicationDegree;


    public Backup(String filename, int replicationDegree) {
        this.filename=filename;
        this.replicationDegree=replicationDegree;
    }

    @Override
    public void run() {
        String filehash = getHashFile();
        try {
            FileInputStream fs = new FileInputStream(System.getProperty("user.dir") + "/files/" + filename);

            byte buffer[] = new byte[StoredChunk.MAX_SIZE];

            int count = 0;
            while (true) {
                int i = fs.read(buffer, 0, StoredChunk.MAX_SIZE);

                System.out.println("Created database.Chunk " + count);
                ++count;
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }

    }

    public void backupChunk() {
        Peer.getMcListener().startSavingStoredConfirmsFor(chunk.getID());

        long waitingTime = INITIAL_WAITING_TIME;
        int attempt = 0;

        boolean done = false;
        while (!done) {
            Peer.getMcListener().clearSavedStoredConfirmsFor(chunk.getID());

            Peer.getCommandForwarder().sendPUTCHUNK(chunk);

            try {
                System.out.println("Waiting for STOREDs for " + waitingTime
                        + "ms");
                Thread.sleep(waitingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int confirmedRepDeg = Peer.getMcListener().getNumStoredConfirmsFor(
                    chunk.getID());

            Log.info(confirmedRepDeg + " peers have backed up chunk no. "
                    + chunk.getID().getChunkNo() + ". (desired: "
                    + chunk.getReplicationDegree() + " )");

            if (confirmedRepDeg < chunk.getReplicationDegree()) {
                attempt++;

                if (attempt > MAX_ATTEMPTS) {
                    Log.info("Reached maximum number of attempts to backup chunk with desired replication degree.");
                    done = true;
                } else {
                    Log.info("Desired replication degree was not reached. Trying again...");
                    waitingTime *= 2;
                }
            } else {
                Log.info("Desired replication degree reached.");
                done = true;
            }
        }

        Peer.getMcListener().stopSavingStoredConfirmsFor(chunk.getID());
    }




    private String getHashFile() {
        Path filepath = Paths.get(System.getProperty("user.dir") + "/files/" + this.filename);

        UserPrincipal owner = null;

        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(filepath, BasicFileAttributes.class);
            FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(filepath, FileOwnerAttributeView.class);
            owner = ownerAttributeView.getOwner();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("owner: " + owner.getName());
        System.out.println("size: " + attr.size());
        System.out.println("lastModifiedTime: " + attr.lastModifiedTime());

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String text = owner.getName() + attr.size() + attr.lastModifiedTime();

        try {
            md.update(text.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] digest = md.digest();

        return String.format("%064x", new java.math.BigInteger(1, digest));

    }
}
