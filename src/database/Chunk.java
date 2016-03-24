package database;

/**
 * Created by diogo on 08/03/2016.
 */
public class Chunk {

    private String fileId, replication_degree;
    private String chunkNo;
    private byte[] data;

    public static final int MAX_CHUNK_SIZE =64000;

    public String getReplication_degree() {
        return replication_degree;
    }

    public String getFileId() {
        return fileId;
    }

    public String getChunkNo() {
        return chunkNo;
    }

    public byte[] getData() {
        return data;
    }

    Chunk(String fileId, String chunkNo, String replication_degree, byte[] data_block){
        this.chunkNo = chunkNo;
        this.fileId=fileId;
        this.replication_degree = replication_degree;
        this.data = data_block;
    }

}
