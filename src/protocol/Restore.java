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
public class Restore implements Runnable, Observer {
    private File file;
    private String filehash;
    private MessageSender messageSender = new MessageSender();

    public Restore(String filename) {
        this.file = new File(filename);
        this.filehash = Peer.getDisk().files.get(filename).getFilehash();
    }


    @Override
    public void run() {
        System.out.println("Starting Restoring file: " + this.file.getName());

        if (Peer.getDisk().hasFile(this.file.getName())) {
            System.out.println("tem o ficheiro");
            database.File file = Peer.getDisk().files.get(this.file.getName());
            for (int i = 0; i < file.getNumChuncks(); i++) {
                String iterator = Integer.toString(i);
                messageSender.getChunkMessage(Peer.getId(), file.getFilehash(), iterator);
            }
        } else {
            System.out.println("No such file has been backed up ");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            RandomAccessFile raf = new RandomAccessFile(this.file + "cenas", "rw");
            if (o instanceof MDRChannel && arg instanceof Message) {
                Message message = (Message) arg;
                if (message.getHeader().getFile_id().equals(filehash)) {
                    int chunkNo = Integer.parseInt(message.getHeader().getChunk_no());
                    System.out.println("Restoring chunk:" + chunkNo);
                    byte[] data = message.getBody();
                    raf.seek(chunkNo * 64000);
                    raf.write(data);
                    raf.close();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
