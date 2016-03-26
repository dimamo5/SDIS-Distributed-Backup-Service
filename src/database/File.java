package database;

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
 * Created by diogo on 12/03/2016.
 */
public class File {
    private String filename;
    private String filehash;
    private int numChuncks;
    private static final int MAX_NO_CHUNKS = 999999;  // PODE TER ATÉ 1.000.000 CHUNKS

    public File(String filename, int numchuncks) {
        this.filename = filename;
        this.numChuncks=numchuncks;
    }

    //=============METHODS===========

    public String getFilename() {
        return filename;
    }

    public String getFilehash() {
        return filehash;
    }


    public int getNumChuncks() {
        return numChuncks;
    }

    void getHashFile() {
        Path filepath = Paths.get(System.getProperty("user.dir") + "/files/" + filename);

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

        this.filehash =
                String.format("%064x", new java.math.BigInteger(1, digest));

        System.out.println("filehash: " + this.filehash);
    }


    //TODO generate threads!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //TODO NEM TODOS OS CHUNKS PRECISAM DE TER 64 KB
    ArrayList<Chunk> splitFile() {

        try {
            FileInputStream fs = new FileInputStream(System.getProperty("user.dir") + "/files/" + filename);
            java.io.File fileDir =new java.io.File("files/"+filehash);
            fileDir.mkdir();

            byte buffer[] = new byte[StoredChunk.MAX_SIZE];

            int count = 0;
            while (true) {
                int i = fs.read(buffer, 0, StoredChunk.MAX_SIZE);
                if (i == -1)
                    break;

                String filenameOut = "files/" + filehash+"/" + count;
                FileOutputStream fos = new FileOutputStream(filenameOut);
                fos.write(buffer, 0, i);
                fos.flush();
                fos.close();

                System.out.println("Created database.Chunk " + count);
                ++count;
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return new ArrayList<>();
    }
}
