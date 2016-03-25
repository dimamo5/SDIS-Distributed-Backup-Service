package database;

/**
 * Created by diogo on 25/03/2016.
 */
public class StoredChunck {
    public static final int MAX_SIZE = 64000;

    private int chunkNo;

    private String fileId;

    private int replicationDegree;

    private byte[] data_block;

    public StoredChunck(String fileId, int chunkNo, int replicationDegree, byte[] data_block) {

        this.chunkNo=chunkNo;

        this.fileId=fileId;

        this.replicationDegree = replicationDegree;

        this.data_block = data_block;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public String getFileId() {
        return fileId;
    }

    public int getReplicationDegree() {
        return replicationDegree;
    }

    public byte[] getData_block() {
        return data_block;
    }
    public int getDataLength(){
        return data_block.length;
    }


}
