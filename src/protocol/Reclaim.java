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

    long amount;
    MessageSender sender = new MessageSender();
    ArrayList<Chunk> chunks;

    public Reclaim(long amount) {
        this.amount = amount;
        this.chunks = Peer.getDisk().sortLessImportantChunk();

    }


    @Override
    public void run() {
        if (Peer.getDisk().getSpaceUsage() < amount) {
            System.err.println("Release all disk");
        }

        int spaceReleased = 0,i=0;

        while(spaceReleased<amount && Peer.getDisk().getSpaceUsage()>0) {
            Chunk c=this.chunks.get(i);
            this.chunks.remove(i);
            Peer.getDisk().removeChunk(c);

            spaceReleased+= StoredChunk.MAX_SIZE;

            sender.removedMessage(Peer.getId(),c.getFileId(),Integer.toString(c.getChunkNo()));

            Peer.getDisk().releaseMemory(StoredChunk.MAX_SIZE);
    }
}

    @Override
    public void update(Observable o, Object arg) {


    }
}
