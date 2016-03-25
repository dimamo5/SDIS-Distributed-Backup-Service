package service;

import message.*;

import java.net.DatagramPacket;

/**
 * Created by Sonhs on 21/03/2016.
 */
public class MessageHandler implements Handler, Runnable  {

    public enum Types {
        PUTCHUNK,STORED, GETCHUNK, CHUNK, DELETE, REMOVED;
    }

    DatagramPacket raw_message;

    public MessageHandler(DatagramPacket raw_message){
        this.raw_message = raw_message;
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
    public void processPutChunk(Message message) {

        if(message.getHeader().getSender_id().equals(Peer.getId())){
            //A Peer never stores the chunks of i's own files
            //TODO Verificar o sender_id já cobre todas as ocorrências deste problema?
            return;
        }

        //peer has space to store chunk
        if(Peer.getDisk().getFreeSpace() < message.getBodyLength()){
            System.out.println("Not enough space on disk to store chunk");
            return;
        }


        //if(Peer.getDisk().hasChunk())

        //se peer já tiver chunk responde com STORED ?

        //armazena chunk data + registo hashmap

        //


    }

    @Override
    public void processStored(Message message) {

    }

    @Override
    public void processGetChunk(Message message) {

    }

    @Override
    public void processChunk(Message message) {

    }

    @Override
    public void processDelete(Message message) {

    }

    @Override
    public void processRemoved(Message message) {

    }
}
