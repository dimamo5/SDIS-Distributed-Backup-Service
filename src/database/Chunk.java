package database;

import java.util.ArrayList;

/**
 * Created by diogo on 08/03/2016.
 */
public class Chunk {

    private String fileId;
    private int replicationDegree;
    private String chunkNo;
    private byte[] data;
    private ArrayList<String > peers=new ArrayList<>(); //Peers that contains the chunck

    public static final int MAX_CHUNK_SIZE =64000;

    public int getReplication_degree() {
        return replicationDegree;
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

    public Chunk(String fileId, String chunkNo, int replicationDegree, byte[] data_block){
        this.chunkNo = chunkNo;
        this.fileId=fileId;
        this.replicationDegree = replicationDegree;
        this.data = data_block;
    }

}
