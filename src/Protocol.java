/**
 * Created by Sonhs on 21/03/2016.
 */
public interface Protocol {

    String Version = "1.0";

    //BackUp sub-protocol
    void putChunkMessage(String sender_id, Chunk c);
    void storedMessage(String sender_id, String file_id, String chunk_no);

    //Restore sub-protocol
    void getChunkMessage(String sender_id, String file_id, String chunk_no);
    void chunkMessage(String sender_id, Chunk c);

    //Deletion sub-protocol
    void deleteMessage(String sender_id, String file_id);

    //Space reclaiming sub-protocol
    void removedMessage(String id, String file_id, String chunk_no);
}
