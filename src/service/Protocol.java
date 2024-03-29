package service;

import database.Chunk;
import database.StoredChunk;

/**
 * Created by Sonhs on 21/03/2016.
 */
public interface Protocol {

    String Version = "1.0";

    //TODO OS MÉTODOS NAO NECESSITAM DE RECEBER O SENDER_ID ....PROLLY

    //BackUp sub-protocol
    void putChunkMessage(String sender_id, StoredChunk c);
    void storedMessage(String sender_id, String file_id, String chunk_no);

    //Restore sub-protocol
    void getChunkMessage(String sender_id, String file_id, String chunk_no);
    void chunkMessage(String sender_id, StoredChunk c);

    //Deletion sub-protocol
    void deleteMessage(String sender_id, String file_id);

    //Space reclaiming sub-protocol
    void removedMessage(String id, String file_id, String chunk_no);
}
