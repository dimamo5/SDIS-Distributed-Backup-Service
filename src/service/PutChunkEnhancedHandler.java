package service;

import channel.MCChannel;
import database.Chunk;
import database.StoredChunk;
import message.Header;
import message.Message;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Created by diogo on 01/04/2016.
 */
public class PutChunkEnhancedHandler implements Observer {

    private Message message;
    private int stored_count;

    public PutChunkEnhancedHandler(Message message) {
        this.message = message;
        this.stored_count = Peer.getDisk().getChunk(message.getHeader().getFile_id(),Integer.parseInt(message.getHeader().getChunk_no())).getPeers().size();
    }

    public Chunk processMessage(){
        System.out.println("Received PUTCHUNCK!");

        //verifica se tem espaço suficiente
        if (!Peer.getDisk().canSaveChunk(message.getBodyLength())) {
            System.out.println("Not enough space to save chunk");
            return null;
        }

        //verifica se chunk
        if (Peer.getDisk().hasChunk(message.getHeader().getFile_id(), Integer.parseInt(message.getHeader().getChunk_no()))) {
            System.out.println("Send STORED Already in database");
            System.out.println("SIZE: "+Peer.getDisk().chunks.size());

            return Peer.getDisk().getChunk(message.getHeader().getFile_id(),Integer.parseInt(message.getHeader().getChunk_no()));

        }else if(Peer.getDisk().hasFileByHash(message.getHeader().getFile_id())){
            System.out.println("Initial Peer");
            return null;
        }
        else {//SAVE CHUNK
            //random delay [0,400]
            try {
                Thread.sleep(new Random().nextInt(400));
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error in Thread.sleep");
            }

            if (stored_count < Integer.parseInt(message.getHeader().getReplic_deg())) {

                Chunk c = new Chunk(message.getHeader().getFile_id(), Integer.parseInt(message.getHeader().getChunk_no()), Integer.parseInt(message.getHeader().getReplic_deg()));

                Peer.getDisk().addChunk(c);
                StoredChunk chunk = new StoredChunk(message.getHeader().getFile_id(), Integer.parseInt(message.getHeader().getChunk_no()), Integer.parseInt(message.getHeader().getReplic_deg()), message.getBody());

                //armazena chunk data + registo hashmap
                Peer.getDisk().storeChunk(chunk);
                Peer.saveDisk();

                return c;
            }
            else return null;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MCChannel && arg instanceof Message) {
            Message m = (Message) arg;

            if (m.getHeader().getType().equals(MessageHandler.Types.STORED.name()) &&
                    m.getHeader().getFile_id().equals(this.message.getHeader().getFile_id()) &&
                    m.getHeader().getChunk_no().equals(this.message.getHeader().getChunk_no())) {

                this.stored_count++;
            }
        }
    }
}
