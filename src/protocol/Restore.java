package protocol;

import channel.MDRChannel;
import database.Chunk;
import message.Message;
import service.MessageSender;
import service.Peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by diogo on 25/03/2016.
 */
public class Restore implements Runnable,Observer {
    private File file;
    private MessageSender messageSender;

    public Restore(String filename) {
        this.file = new File(filename);
    }


    @Override
    public void run() {
        if(Peer.getDisk().hasFile(this.file.getName())){
            database.File file=Peer.getDisk().files.get(this.file.getName());
            for (int i = 0; i < file.getNumChuncks(); i++) {
                messageSender.getChunkMessage(Peer.getId(),file.getFilehash(),Integer.toString(i));
            }

        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            RandomAccessFile raf=new RandomAccessFile(this.file,"w");
            if(o instanceof MDRChannel && arg instanceof Message){
                Message message=(Message) arg;
                int chunkNo= Integer.parseInt(message.getHeader().getChunk_no());
                byte[] data = message.getBody();
                raf.write(data,(chunkNo)*64000,64000);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
