/**
 * Created by diogo on 08/03/2016.
 */
public class Chunck {

    private int chunckNo;
    private String fileId;
    public static final int MAX_CHUNCK_SIZE=64000;

    Chunck(String fileId,int chunckNo){
        this.chunckNo=chunckNo;
        this.fileId=fileId;
    }

}
