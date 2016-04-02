package service;

import channel.MCChannel;
import database.Chunk;
import database.StoredChunk;
import message.Header;
import message.Message;
import protocol.BackupChunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Created by diogo on 28/03/2016.
 */
public class RemovedHandler implements Observer {

    Message message;
    int putChunkCount = 0;

    public RemovedHandler(Message message) {
        this.message = message;
    }

    public void process(Message message) {

        String filehash = message.getHeader().getFile_id();
        int chunkno = Integer.parseInt(message.getHeader().getChunk_no());

        if (Peer.getDisk().hasChunk(filehash, chunkno)) {

            Chunk c = Peer.getDisk().getChunk(filehash, chunkno);
            c.removePeer(Peer.getId());

            int currentRep = c.getPeers().size();
            int desiredRep = c.getReplicationDegree();

            if (currentRep < desiredRep) {
                System.out.println("Below the desired degree");
                try {
                    Thread.sleep(new Random().nextInt(400));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Error in Thread.sleep");
                }
                if (putChunkCount == 0) {
                    try {
                        FileInputStream fs = new FileInputStream(System.getProperty("user.dir") + "/chunks/" + c.getFileId() + "/" + c.getChunkNo());
                        byte buffer[] = new byte[StoredChunk.MAX_SIZE]; //TODO save chunk size

                        int numBytesRead=fs.read(buffer);
                        if(numBytesRead<0){
                            numBytesRead=0;
                        }
                        byte[] newBuffer= Arrays.copyOfRange(buffer,0,numBytesRead);

                        System.out.println("Started backing chunk");

                        StoredChunk chunk = new StoredChunk(c.getFileId(), c.getChunkNo(), c.getReplicationDegree(), newBuffer);
                        BackupChunk bc = new BackupChunk(chunk);

                        Peer.getMC_channel().addObserver(bc);
                        new Thread(bc).start();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MCChannel && arg instanceof Message) {
            Message m = (Message) arg;

            if (m.getHeader().getType().equals(MessageHandler.Types.PUTCHUNK.name()) &&
                    m.getHeader().getFile_id().equals(this.message.getHeader().getFile_id()) &&
                    m.getHeader().getChunk_no().equals(this.message.getHeader().getChunk_no())) {
                this.putChunkCount++;
            }
        }
    }
}