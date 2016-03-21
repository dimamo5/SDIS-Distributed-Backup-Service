/**
 * Created by diogo on 08/03/2016.
 */
public class Chunk {

    private int chunkNo;
    private String fileId;
    private byte[] data;

    public static final int MAX_CHUNK_SIZE =64000;

    Chunk(String fileId, int chunkNo, byte[] data_block){
        this.chunkNo = chunkNo;
        this.fileId=fileId;
        this.data = data_block;
    }

}
