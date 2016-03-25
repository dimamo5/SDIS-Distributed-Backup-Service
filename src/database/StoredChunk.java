package database;

/**
 * Created by diogo on 25/03/2016.
 */
public class StoredChunk {
    public static final int MAX_SIZE = 64000;

    private int chunkNo;

    private String fileId;

    private int replicationDegree;

    private byte[] dataBlock;

    public StoredChunk(String fileId, int chunkNo, int replicationDegree, byte[] dataBlock) {

        this.chunkNo=chunkNo;

        this.fileId=fileId;

        this.replicationDegree = replicationDegree;

        this.dataBlock = dataBlock;
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

    public byte[] getDataBlock() {
        return dataBlock;
    }
    public int getDataLength(){
        return dataBlock.length;
    }


}
