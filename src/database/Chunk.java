package database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by diogo on 08/03/2016.
 */
public class Chunk implements Serializable {

    private String fileId;
    private int replicationDegree;
    private int chunkNo;
    private ArrayList<String > peers=new ArrayList<>(); //Peers that contains the chunck

    public int getReplication_degree() {
        return replicationDegree;
    }

    public String getFileId() {
        return fileId;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public Chunk(String fileId, int chunkNo, int replicationDegree){
        this.chunkNo = chunkNo;
        this.fileId=fileId;
        this.replicationDegree = replicationDegree;
    }

    public void addPeer(String peer){
        if(!peers.contains(peer)){
            peers.add(peer);
        }
    }

    @Override
    public String toString() {
        String s = "";
        for(String p : peers)
            s+= p + " ";

        return "Chunk{" +
                "fileId='" + fileId + '\'' +
                ", replicationDegree=" + replicationDegree +
                ", chunkNo=" + chunkNo +
                ", peers=" + s +
                '}';
    }
}
