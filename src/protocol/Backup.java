package protocol;

import database.Chunk;
import database.StoredChunk;

import java.io.*;
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

            int numChuncks= (int) new File(System.getProperty("user.dir") + "/files/" + filename).length()/StoredChunk.MAX_SIZE +1;

            byte buffer[] = new byte[StoredChunk.MAX_SIZE];

            for(int i =0;i<numChuncks;i++){
                System.out.println("Launching Thread "+i);
                fs.read(buffer, 0, StoredChunk.MAX_SIZE);
                byte[] bufferCloned = buffer.clone();
                StoredChunk chunk = new StoredChunk(filehash,i,replicationDegree,buffer);
                new Thread(new BackupChunk(chunk)).start();
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }

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

        /*System.out.println("owner: " + owner.getName());
        System.out.println("size: " + attr.size());
        System.out.println("lastModifiedTime: " + attr.lastModifiedTime());*/

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
