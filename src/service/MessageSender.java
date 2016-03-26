package service;

import database.Chunk;
import database.StoredChunk;
import message.Header;
import message.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by Sonhs on 22/03/2016.
 */

public class MessageSender implements Protocol{

    private static final String put_chunk = "PUTCHUNK",
            stored = "STORED", get_chunk = "GETCHUNK",
            chunk = "CHUNK", delete = "DELETE", removed = "REMOVED";

    public MessageSender(){}

    @Override
    public void putChunkMessage(String sender_id, StoredChunk c) {

        Header h = new Header(put_chunk, Version, sender_id,c.getFileId(),Integer.toString(c.getChunkNo()),Integer.toString(c.getReplicationDegree()));
        Message m = new Message(h,c.getDataBlock());

        sendMessage(m,Peer.Channels.MDB);
    }

    @Override
    public void storedMessage(String sender_id, String file_id, String chunk_no) {

        Header h = new Header(stored, Version, sender_id, file_id, chunk_no);
        Message m = new Message(h);

        sendMessage(m,Peer.Channels.MC);
    }

    @Override
    public void getChunkMessage(String sender_id, String file_id, String chunk_no) {

        Header h = new Header(get_chunk, Version, sender_id, file_id, chunk_no);
        Message m = new Message(h);

        sendMessage(m,Peer.Channels.MC);
    }

    @Override
    public void chunkMessage(String sender_id, StoredChunk c) {

        Header h = new Header(chunk,Version,sender_id,c.getFileId(),Integer.toString(c.getChunkNo()));
        Message m = new Message(h,c.getDataBlock());

        sendMessage(m,Peer.Channels.MDR);
    }

    @Override
    public void deleteMessage(String sender_id, String file_id) {

        Header h = new Header(delete, Version, sender_id,file_id);
        Message m = new Message(h);

        sendMessage(m,Peer.Channels.MC);
    }

    @Override
    public void removedMessage(String sender_id, String file_id, String chunk_no) {
        Header h = new Header(removed, Version, sender_id, file_id, chunk_no);
        Message m = new Message(h);

        sendMessage(m,Peer.Channels.MC);
    }

    private void sendMessage(Message m, Peer.Channels channel){

        DatagramPacket packet;
        InetAddress address = null;
        int port = -1;

        switch(channel){
            case MC:
                address = Peer.getMC_channel().getAddress();
                port = Peer.getMC_channel().getPort();
                break;

            case MDB:
                address = Peer.getMDB_channel().getAddress();
                port = Peer.getMDB_channel().getPort();
                break;

            case MDR:
                address = Peer.getMDR_channel().getAddress();
                port = Peer.getMDR_channel().getPort();
                break;

            default:
                System.out.println("Wrong channel");
                System.exit(1);
        }

        packet = new DatagramPacket(m.getMessageBytes(),m.getMessageBytes().length,
                address, port);

        try {
            Peer.getComunication_socket().send(packet);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("#service.MessageSender# Error sending packet");
            System.exit(1);
        }
    }
}
