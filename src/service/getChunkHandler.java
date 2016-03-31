package service;

import channel.MCChannel;
import database.StoredChunk;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import message.Message;
import service.Peer;

/**
 * Created by diogo on 31/03/2016.
 */
public class GetChunkHandler implements Observer{

    private Message message;
    private boolean received_chunk = false;

    GetChunkHandler(Message message){
        this.message = message;
    }

    public Object processMessage() {

        if (Peer.getDisk().hasChunk(message.getHeader().getFile_id(), new Integer(message.getHeader().getChunk_no()))) {
            try {
                Thread.sleep(new Random().nextInt(400));
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error in Thread.sleep");
            }

            if(received_chunk){
                return null;
            }

            return Peer.getDisk().loadChunk(message.getHeader().getFile_id(), message.getHeader().getChunk_no());
        }
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MCChannel && arg instanceof Message) {
            Message m = (Message) arg;

            if (m.getHeader().getType().equals(MessageHandler.Types.CHUNK) &&
                    m.getHeader().getFile_id().equals(this.message.getHeader().getFile_id()) &&
                    m.getHeader().getChunk_no().equals(this.message.getHeader().getChunk_no())) {

                this.received_chunk = true;
            }
        }

    }
}
