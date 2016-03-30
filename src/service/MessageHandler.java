package service;

import channel.MDBChannel;
import database.Chunk;
import database.StoredChunk;
import message.*;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Sonhs on 21/03/2016.
 */
public class MessageHandler implements Handler, Runnable {

    public enum Types {
        PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED;
    }

    Message message;
    MessageSender message_sender;

    public MessageHandler(DatagramPacket raw_message) {
        this.message = new Message(raw_message);
        message_sender = new MessageSender();

    }

    @Override
    public void run() {
        //delegate message to the corresponding processor method
        dispatcher(this.message.getHeader().getType(), this.message);
    }

    @Override
    public void dispatcher(String type, Message message) {
        Types t = Types.valueOf(type);
        //System.out.println("message type :" + t);

        switch (t) {

            case PUTCHUNK:
                processPutChunk(message);
                break;

            case STORED:
                processStored(message);
                break;

            case GETCHUNK:
                processGetChunk(message);
                break;

            case CHUNK:
                processChunk(message);
                break;

            case DELETE:
                processDelete(message);
                break;

            case REMOVED:
                processRemoved(message);
                break;

            default:
                System.out.println("UNKNOWN TYPE OF MESSAGE RECEIVED");
                System.exit(1);
        }
    }

    @Override
    public void processPutChunk(Message message) { //TODO VERIFICAR PASSOS DO MÉTODO !!!!!!!!!

        int chunk_peer_count = 0;

        //============ ENHANCEMENT 3.2 ============
        if(Peer.isEnhancements_ON()) {
            chunk_peer_count = Peer.getDisk().getChunk(message.getHeader().getFile_id(),Integer.parseInt(message.getHeader().getChunk_no())).getPeers().size();
        }
        //======================================


        System.out.println("Received PUTCHUNCK!");

        //verifica se tem espaço suficiente
        if (!Peer.getDisk().canSaveChunk(message.getBodyLength())) {
            System.out.println("Not enough space to save chunk");
            return;
        }

        //verifica se chunk
        if (Peer.getDisk().hasChunk(message.getHeader().getFile_id(), Integer.parseInt(message.getHeader().getChunk_no()))) {
            System.out.println("Send STORED Already in database");
            System.out.println("SIZE: "+Peer.getDisk().chunks.size());
            //send "STORED" message
            message_sender.storedMessage(Peer.getId(), message.getHeader().getFile_id(), message.getHeader().getChunk_no());
        }else if(Peer.getDisk().hasFileByHash(message.getHeader().getFile_id())){
            System.out.println("Initial Peer");
        }
            else {

            //random delay [0,400]
            try {
                Thread.sleep(new Random().nextInt(400));
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error in Thread.sleep");
            }

            //=============== ENHANCEMENT 3.2 ================
            if(Peer.isEnhancements_ON()){
                int stores_received=
                        Peer.getDisk().getChunk(message.getHeader().getFile_id(),Integer.parseInt(message.getHeader().getChunk_no())).getPeers().size() -
                                chunk_peer_count;

                //Replication degree reached, ignore backup
                if(stores_received >= Integer.parseInt(message.getHeader().getReplic_deg()))
                    return;
            }
            //============================================

            System.out.println("Send STORED");
            //send "STORED" message
            message_sender.storedMessage(Peer.getId(), message.getHeader().getFile_id(), message.getHeader().getChunk_no());

            Peer.getDisk().addChunk(new Chunk(message.getHeader().getFile_id(), Integer.parseInt(message.getHeader().getChunk_no()), Integer.parseInt(message.getHeader().getReplic_deg())));

            StoredChunk chunk = new StoredChunk(message.getHeader().getFile_id(), Integer.parseInt(message.getHeader().getChunk_no()), Integer.parseInt(message.getHeader().getReplic_deg()), message.getBody());

            //armazena chunk data + registo hashmap
            Peer.getDisk().storeChunk(chunk);
            Peer.saveDisk();
        }
        return;


        //================================================



         /*TODO ENHANCEMENT !!!!!!!!!!!!!!!!
        TODO VERIFICA SE NO GRUPO DE MULTICAST NAO RECEBEU UM NUMERO DE STORED CONFIRMATIONS SUPERIOR OU IGUAL AO REPLIC DEGREE PARA ESTE CHUNK;
        TODO SE NAO RECEBEU ENVIA STORED MESSAGE;

        MAYBE USE SOME IFDEF (ENHANCEMENT CONFIGS == ON) ...

        if(some_stored_message_counter_variable >= replic_degree){
            return;
        }*/
        //================================================


    }

    @Override
    public void processStored(Message message) {

        System.out.println("Received STORED!");

        Chunk c = new Chunk(message.getHeader().getFile_id(), Integer.parseInt(message.getHeader().getChunk_no()), -1);
        Peer.getDisk().addChunkMirror(c, message.getHeader().getReplic_deg());

        Peer.getMC_channel().notifyObservers(message);

    }

    @Override
    public void processGetChunk(Message message) {

        //System.out.println("Received GETCHUNK!"+ message.toString());

        if (Peer.getDisk().hasChunk(message.getHeader().getFile_id(), new Integer(message.getHeader().getChunk_no()))) {
            try {
                Thread.sleep(new Random().nextInt(400));
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error in Thread.sleep");
            }

            StoredChunk c = Peer.getDisk().loadChunk(message.getHeader().getFile_id(), message.getHeader().getChunk_no());

            message_sender.chunkMessage(Peer.getId(), c);

        }
    }

    @Override
    public void processChunk(Message message) {

        //System.out.println("Received CHUNK!");

        Peer.getMDR_channel().notifyObservers(message);
    }

    @Override
    public void processDelete(Message message) {
        ArrayList<Chunk> chunksDeleted = Peer.getDisk()
                .getChunkFromFileId(message.getHeader().getFile_id());

        for(int i=0;i<chunksDeleted.size();i++){
            Peer.getDisk().removeChunk(chunksDeleted.get(i));
        }

        Peer.getDisk().removeFolder(message.getHeader().getFile_id());
        Peer.saveDisk();
    }

    @Override
    public void processRemoved(Message message) {
        System.out.println("Received REMOVED");
        RemovedHandler rh=new RemovedHandler(message);
        Peer.getMDB_channel().addObserver(rh);
        rh.process(message);
    }

}
