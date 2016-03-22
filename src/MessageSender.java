/**
 * Created by Sonhs on 22/03/2016.
 */
public class MessageSender implements Protocol{

    private static final String put_chunk = "PUTCHUNK",
            stored = "STORED", get_chunk = "GETCHUNK",
            chunk = "CHUNK", delete= "DELETE", removed= "REMOVED";


    @Override
    public void PutChunkMessage(String sender_id,Chunk c) {

        Header h = new Header(put_chunk, Version, sender_id,c.getFileId(),c.getChunkNo(),c.getReplication_degree());
        Message m = new Message(h,c.getData());

        //m.getMessageBytes();
        //envia byte[] obtido em cima para MDBChannel
    }

    @Override
    public void StoredMessage(String sender_id, String file_id, String chunk_no) {

        Header h = new Header(stored, Version, sender_id, file_id, chunk_no);
        Message m = new Message(h);

        //m.getMessageBytes();
        //envia byte[] obtido em cima para MCChannel
    }

    @Override
    public void GetChunkMessage(String sender_id, String file_id, String chunk_no) {

        Header h = new Header(get_chunk, Version, sender_id, file_id, chunk_no);
        Message m = new Message(h);

        //m.getMessageBytes();
        //envia byte[] obtido em cima para MCChannel
    }

    @Override
    public void ChunkMessage(String sender_id, Chunk c) {

        Header h = new Header(chunk,Version,sender_id,c.getFileId(),c.getChunkNo());
        Message m = new Message(h,c.getData());

        //m.getMessageBytes();
        //envia byte[] obtido em cima para MDRhannel
    }

    @Override
    public void DeleteMessage(String sender_id, String file_id) {

        Header h = new Header(delete, Version, sender_id,file_id);
        Message m = new Message(h);

        //m.getMessageBytes();
        //envia byte[] obtido em cima para MCChannel
    }

    @Override
    public void RemovedMessage(String sender_id, String file_id, String chunk_no) {
        Header h = new Header(removed, Version, sender_id, file_id, chunk_no);
        Message m = new Message(h);

        //m.getMessageBytes();
        //envia byte[] obtido em cima para MCChannel
    }
}
