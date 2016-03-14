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
    private ArrayList<Chunck> chunks = new ArrayList<Chunck>();
    private static final int MAX_NO_CHUNKS = 10000;

    File(String filename) {
        this.filename = filename;
        getHashFile();
    }

    public static void main(String args[]) {
        new File("texto.txt");
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

    ArrayList<Chunck> splitFile() {

        try {
            FileInputStream fs = new FileInputStream(System.getProperty("user.dir") + "/files/" + filename);

            byte buffer[] = new byte[Chunck.MAX_SIZE];

            int count = 0;
            while (true) {
                int i = fs.read(buffer, 0, Chunck.MAX_SIZE);
                if (i == -1)
                    break;

                String filenameOut = "files/" + filename + "_Chunck" + count;
                FileOutputStream fos = new FileOutputStream(filenameOut);
                fos.write(buffer, 0, i);
                fos.flush();
                fos.close();

                System.out.println("Created Chunck " + count);
                ++count;
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return new ArrayList<>();
    }
}
