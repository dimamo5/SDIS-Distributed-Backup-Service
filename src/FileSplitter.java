import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/**
 * Created by diogo on 08/03/2016.
 */
public class FileSplitter {

    private int maxBufferSize=64000;

    public static  void main(String args[]){
        new FileSplitter().splitFile("texto3.txt");
    }

    FileSplitter(){

    }

    ArrayList<Chunck> splitFile(String filename){
        Path filepath= Paths.get(System.getProperty("user.dir")+"/files/"+filename);

        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(filepath, BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("size: " + attr.size());
        System.out.println("lastModifiedTime: " + attr.lastModifiedTime());

        try {
            FileInputStream fs = new FileInputStream(System.getProperty("user.dir")+"/files/"+filename);



            byte buffer[] = new byte[maxBufferSize];

            int count = 0;
            while (true) {
                int i = fs.read(buffer, 0, maxBufferSize);
                if (i == -1)
                    break;

                String filenameOut = "files/"+filename+"_Chunck" + count;
                FileOutputStream fos = new FileOutputStream(filenameOut);
                fos.write(buffer, 0, i);
                fos.flush();
                fos.close();

                System.out.println("Created Chunck "+count);
                ++count;
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return new ArrayList<>();
    }
}
