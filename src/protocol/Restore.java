package protocol;

import channel.MDRChannel;
import database.Chunk;
import database.StoredChunk;
import message.Message;
import service.MessageSender;
import service.Peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by diogo on 25/03/2016.
 */
public class Restore implements Runnable, Observer {
    private File file;
    private String filehash;
    private MessageSender messageSender = new MessageSender();
    private ArrayList<Integer> chunksReceived=new ArrayList<>();
    private int receivedChunks=0;
    private int numChunks;

    public Restore(String filename) {
        this.file = new File(filename);
        this.filehash = Peer.getDisk().files.get(filename).getFilehash();
        this.numChunks=Peer.getDisk().files.get(filename).getNumChuncks();
    }


    @Override
    public void run() {
        System.out.println("Starting Restoring file: " + this.file.getName());

        int counter=0;
        int MAX_TRIES = 3;
        while(counter!= MAX_TRIES) {
            if (Peer.getDisk().hasFile(this.file.getName())) {
                database.File file = Peer.getDisk().files.get(this.file.getName());
                for (int i = 0; i < file.getNumChuncks(); i++) {
                    String iterator = Integer.toString(i);
                    messageSender.getChunkMessage(Peer.getId(), file.getFilehash(), iterator);
                }
            } else {
                System.out.println("No such file has been backed up ");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter++;

            if(this.receivedChunks==this.numChunks){
                System.out.println("File Restored");
            }
        }
        if(this.receivedChunks!=this.numChunks){
            System.out.println("File Not Restored");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
            try {
                RandomAccessFile raf = new RandomAccessFile("restore/"+this.file, "rw");
                if (o instanceof MDRChannel && arg instanceof Message) {
                    Message message = (Message) arg;
                    if (message.getHeader().getFile_id().equals(filehash) && ! this.chunksReceived.contains(Integer.parseInt(message.getHeader().getChunk_no()))) {
                        int chunkNo = Integer.parseInt(message.getHeader().getChunk_no());
                        System.out.println("Restoring chunk:" + chunkNo);
                        byte[] data = message.getBody();
                        raf.seek(chunkNo * StoredChunk.MAX_SIZE);
                        raf.write(data);
                        raf.close();
                        this.chunksReceived.add(Integer.parseInt(message.getHeader().getChunk_no()));
                        this.receivedChunks++;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if(this.receivedChunks==this.numChunks){
            Peer.getMDB_channel().deleteObserver(this);
        }

    }
}
