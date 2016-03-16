/**
 * Created by Sonhs on 13/03/2016.
 */


public class Header {

    public static final char CRLF[] = {0xD , 0xA}; //TODO corrigir isto

    String type, version, sender_id, file_id;
    int chunk_no, replic_deg;

    Header(String MessageType,String Version, String SenderId,String FileId,int ChunkNo, int ReplicationDeg){
        type = MessageType;
        version = Version;
        sender_id = SenderId;
        file_id = FileId;
        chunk_no = ChunkNo;
        replic_deg = ReplicationDeg;
    }
}
