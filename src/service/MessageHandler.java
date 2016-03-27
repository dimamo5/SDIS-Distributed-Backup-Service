package service;

import database.Chunk;
import database.StoredChunk;
import message.*;

import java.net.DatagramPacket;
import java.util.Random;

/**
 * Created by Sonhs on 21/03/2016.
 */
public class MessageHandler implements Handler, Runnable  {

    public enum Types {
        PUTCHUNK,STORED, GETCHUNK, CHUNK, DELETE, REMOVED;
    }

    DatagramPacket raw_message;
    MessageSender message_sender;

    public MessageHandler(DatagramPacket raw_message){
        this.raw_message = raw_message;
        message_sender = new MessageSender();
    }

    @Override
    public void run() {

        Message processed_message = new Message(raw_message);

        //delegate message to the corresponding processor method
        dispatcher(processed_message.getHeader().getType(), processed_message);
    }

    @Override
    public void dispatcher(String type, Message message) {
        Types t = Types.valueOf(type);
        System.out.println("message type :"+t);

        switch(t){

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

        System.out.println("Received PUTCHUNCK!");

        //verifica se tem espaço suficiente
        if(!Peer.getDisk().canSaveChunk(message.getBodyLength())){
            System.out.println("Not enough space to save chunk");
            return;
        }

        //verifica se chunk
        if(Peer.getDisk().hasChunk(message.getHeader().getFile_id(), message.getHeader().getChunk_no())){
            //random delay [0,400]
            try {
                Thread.sleep(new Random().nextInt(400));
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error in Thread.sleep");
            }


            System.out.println("Send STORED");
            //send "STORED" message
            message_sender.storedMessage(Peer.getId(),message.getHeader().getFile_id(),message.getHeader().getChunk_no());

            //message_sender.storedMessage(Peer.getId(),message.getHeader().getFile_id(),message.getHeader().getChunk_no());

            StoredChunk chunk = new StoredChunk(message.getHeader().getFile_id(), Integer.parseInt(message.getHeader().getChunk_no()),Integer.parseInt(message.getHeader().getReplic_deg()),message.getBody());

            //armazena chunk data + registo hashmap
            Peer.getDisk().storeChunk(chunk);

            return;
        }

        //================================================
         /*TODO ENHANCEMENT !!!!!!!!!!!!!!!!
        TODO VERIFICA SE NO GRUPO DE MULTICAST NAO RECEBEU UM NUMERO DE STORED CONFIRMATIONS SUPERIOR OU IGUAL AO REPLIC DEGREE PARA ESTE CHUNK;
        TODO SE NAO RECEBEU ENVIA STORED MESSAGE;

        MAYBE USE SOME IFDEF (ENHANCEMENT CONFIGS == ON) ...

        if(some_stored_message_counter_variable >= replic_degree){
            return;
        }*/
        //================================================


        //message_sender.storedMessage(Peer.getId(),message.getHeader().getFile_id(),message.getHeader().getChunk_no());

        StoredChunk chunk = new StoredChunk(message.getHeader().getFile_id(), Integer.parseInt(message.getHeader().getChunk_no()),Integer.parseInt(message.getHeader().getReplic_deg()),message.getBody());


    }

    @Override
    public void processStored(Message message) {

        System.out.println("Received STORED!");

        Chunk c = new Chunk(message.getHeader().getFile_id(),message.getHeader().getChunk_no(),-1);
        Peer.getDisk().addChunkMirror(c, message.getHeader().getReplic_deg());

    }

    @Override
    public void processGetChunk(Message message) {

    }

    @Override
    public void processChunk(Message message) {
        String filehash = message.getHeader().getFile_id();
        if(Peer.getMDR_channel().canStore(filehash)){
            Peer.getMDR_channel().notifyObservers(message);
        }
    }

    @Override
    public void processDelete(Message message) {

    }

    @Override
    public void processRemoved(Message message) {

    }
}
