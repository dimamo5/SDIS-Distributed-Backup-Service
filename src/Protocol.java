/**
 * Created by Sonhs on 21/03/2016.
 */
public interface Protocol {

    public final String Version = "1.0";

    //BackUp sub-protocol
    void PutChunkMessage(Chunk c);
    void StoredMessage(String id);

    //Restore sub-protocol
    void GetChunkMessage(String id);
    void ChunkMessage(Chunk c);

    //Deletion sub-protocol
    void DeleteMessage(String id);

    //Space reclaiming sub-protocol
    void RemovedMessage(String id);
}
