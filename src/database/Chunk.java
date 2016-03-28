package database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by diogo on 08/03/2016.
 */
public class Chunk implements Serializable, Comparable<Chunk> {

    private String fileId;
    private int replicationDegree;
    private int chunkNo;

    public ArrayList<String> getPeers() {
        return peers;
    }

    private ArrayList<String > peers=new ArrayList<>(); //Peers that contains the chunck

    public int getReplicationDegree() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chunk)) return false;

        Chunk chunk = (Chunk) o;

        if (chunkNo != chunk.chunkNo) return false;
        return fileId.equals(chunk.fileId);

    }

    @Override
    public int hashCode() {
        int result = fileId.hashCode();
        result = 31 * result + chunkNo;
        return result;
    }

    @Override
    public int compareTo(Chunk c) {
            int thisExtraBackup=this.peers.size()-this.replicationDegree;
            int oExtraBackup=c.getPeers().size()-c.getReplicationDegree();

            if(thisExtraBackup>oExtraBackup){
                return 1;
            }else
                return 0;
    }
}
