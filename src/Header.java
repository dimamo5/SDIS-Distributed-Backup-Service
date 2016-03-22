
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sonhs on 13/03/2016.
 */

public class Header {

    public static final String CRLF = "\r\n"; //TODO corrigir isto

    private static final String versionPattern = "\\d.\\d";
    private static final int file_id_length = 32, //Bytes -> 256 bits (SHA256)
                             chunk_no_max_length = 6; //Bytes

    private String type, version, sender_id, file_id,chunk_no,replic_deg;
    private String header="";

    @Override
    public String toString() {
        return "Header{" +
                "type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", sender_id='" + sender_id + '\'' +
                ", file_id='" + file_id + '\'' +
                ", chunk_no='" + chunk_no + '\'' +
                ", replic_deg='" + replic_deg + '\'' +
                ", header='" + header + '\'' +
                '}';
    }

    Header(byte[] header_info){
        parseHeader(header_info);
    }

    private void parseHeader(byte[] header_info) {

        String partitioned_header[] = new String(header_info).split("[ ]+"); //separa tokens intervalados por 1+ espaços

        //TODO CONSIDERAMOS QUE O HEADER TEM SEMPRE 6 FIELDS ??

        type= partitioned_header[0];
        version=partitioned_header[1];
        sender_id=partitioned_header[2];
        file_id=partitioned_header[3];
        chunk_no=partitioned_header[4];
        replic_deg=partitioned_header[5];

        System.out.println("Header parsed -> result:");
        this.toString();
    }

    Header(String MessageType, String Version, String SenderId, String FileId, String ChunkNo, String ReplicationDeg){
        type = MessageType;
        version = Version;
        sender_id = SenderId;
        file_id = FileId;
        chunk_no = ChunkNo;
        replic_deg = ReplicationDeg;

        System.out.println(this.toString());

        if (buildHeader() != 0){
            System.out.println("Incorrect header parameters \n");
            System.exit(1);
        }
    }

    String getHeaderMsg(){
        return header;
    }

    private int buildHeader(){

        if(checkParams() == false)
            return -1;

        String file_id_to_64B_ascii = convertToHexString(file_id);
        //System.out.println("converted to 64B ascii: " + file_id_to_64B_ascii);

        header = header.concat(type+ ' ' + version + ' ' + sender_id + ' ' + file_id_to_64B_ascii + ' ' + chunk_no + ' ' + replic_deg + ' ' + CRLF+CRLF);
        return 0;
    }

    private boolean checkParams(){
        return version.matches(versionPattern) && (file_id.length() == file_id_length) &&
                (chunk_no.length() <= chunk_no_max_length) && (Integer.parseInt(replic_deg) <= 9);
    }

    private String convertToHexString(String string){
        String result = "" ;
        byte[] partitioned = string.getBytes();
        byte converted[] = new byte[string.length()*2];
        int right_most = 0x0F, left_most = 0xF0;

        for(int i = 0,j=0; i < partitioned.length ; i++, j+=2){
            converted[j] = (byte) (((partitioned[i] & left_most))>>> 4);
            converted[j+1] = (byte) (partitioned[i] & right_most);
        }

        for(int i = 0; i < converted.length ; i++){
            result += Integer.toString((int)converted[i],16);
        }
        return result;
    }


    public static void main(String[] args){

        Header h = new Header("tipox","1.0","omeuid","sha256sha256sha256sha256sha256sh","2","2");

        System.out.println(h.getHeaderMsg());
    }


}
