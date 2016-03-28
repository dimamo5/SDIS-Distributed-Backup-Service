package protocol;

import database.Chunk;
import database.StoredChunk;
import service.MessageSender;
import service.Peer;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by diogo on 26/03/2016.
 */
public class Reclaim implements Runnable, Observer {

    int amount;
    MessageSender sender = new MessageSender();
    ArrayList<Chunk> chunks;

    public Reclaim(int amount) {
        this.amount = amount;
        this.chunks = Peer.getDisk().sortLessImportantChunk();

    }


    @Override
    public void run() {
        if (Peer.getDisk().getSpaceUsage() < amount) {
            System.err.println("Not enough space to release");
            return;
        }
        Peer.getDisk().releaseMemory(amount);
        int spaceReleased = 0,i=0;

        while(spaceReleased<amount) {
            Chunk c=this.chunks.get(i);

            spaceReleased+= StoredChunk.MAX_SIZE; //TODO buscar size chunk

            sender.removedMessage(Peer.getId(),c.getFileId(),Integer.toString(c.getChunkNo()));
    }
}

    @Override
    public void update(Observable o, Object arg) {


    }
}
