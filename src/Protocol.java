/**
 * Created by Sonhs on 21/03/2016.
 */
public interface Protocol {

    public final String Version = "1.0";

    //BackUp sub-protocol
    void PutChunkMessage(String sender_id,Chunk c);
    void StoredMessage(String sender_id, String file_id, String chunk_no);

    //Restore sub-protocol
    void GetChunkMessage(String sender_id, String file_id, String chunk_no);
    void ChunkMessage(String sender_id,Chunk c);

    //Deletion sub-protocol
    void DeleteMessage(String sender_id, String file_id);

    //Space reclaiming sub-protocol
    void RemovedMessage(String id, String file_id, String chunk_no);
}
