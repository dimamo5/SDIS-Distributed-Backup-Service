package message;

import com.sun.xml.internal.org.jvnet.fastinfoset.sax.FastInfosetReader;

import java.util.Arrays;

/**
 * Created by Sonhs on 13/03/2016.
 */

public class Header {

    public static final String CRLF = "\r\n";

    private static final String versionPattern = "\\d.\\d";
    private static final int file_id_length = 64, //Bytes -> 256 bits (SHA256)
                             chunk_no_max_length = 6; //Bytes

    private String type="", version="", sender_id="", file_id="", chunk_no="", replic_deg="";
    private String header_data ="";

    public Header(byte[] header_info){
        parseHeader(header_info);

        if (buildHeader() != 0){
            System.out.println("Incorrect header parameters \n");
            System.exit(1);
        }

        System.out.println("message.Header msg:"+this.toString());
    }

    public Header(String MessageType, String Version, String SenderId, String FileId, String ChunkNo, String ReplicationDeg){

        type = MessageType;
        version = Version;
        sender_id = SenderId;
        file_id = FileId;
        chunk_no = ChunkNo;
        replic_deg = ReplicationDeg;

        System.out.println("message.Header msg:"+this.toString());

        if (buildHeader() != 0){
            System.out.println("Incorrect header parameters \n");
            System.exit(1);
        }
    }

    public Header(String MessageType, String Version, String SenderId, String FileId, String ChunkNo){

        type = MessageType;
        version = Version;
        sender_id = SenderId;
        file_id = FileId;
        chunk_no = ChunkNo;

        System.out.println("message.Header msg:"+this.toString());

        if (buildHeader() != 0){
            System.out.println("Incorrect header parameters \n");
            System.exit(1);
        }
    }

    public Header(String MessageType, String Version, String SenderId, String FileId){

        type = MessageType;
        version = Version;
        sender_id = SenderId;
        file_id = FileId;

        System.out.println("message.Header msg:"+this.toString());

        if (buildHeader() != 0){
            System.out.println("Incorrect header parameters \n");
            System.exit(1);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getChunk_no() {
        return chunk_no;
    }

    public void setChunk_no(String chunk_no) {
        this.chunk_no = chunk_no;
    }

    public String getReplic_deg() {
        return replic_deg;
    }

    public void setReplic_deg(String replic_deg) {
        this.replic_deg = replic_deg;
    }

    @Override
    public String toString() {
        return "message.Header{" +
                "type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", sender_id='" + sender_id + '\'' +
                ", file_id='" + file_id + '\'' +
                ", chunk_no='" + chunk_no + '\'' +
                ", replic_deg='" + replic_deg + '\'' +
                ", header_data='" + header_data + '\'' +
                '}';
    }

    private void parseHeader(byte[] header_info) {
        //"\\p{Space}+" -> regex separa tokens intervalados por 1+ espaços
        String partitioned_header[] = new String(header_info).split("\\p{Space}+");

        for(String s : partitioned_header)
            System.out.println(s);

        type = partitioned_header[0];
        version = partitioned_header[1];
        sender_id = partitioned_header[2];
        file_id = partitioned_header[3];

        if(partitioned_header.length == 5)
            chunk_no = partitioned_header[4];
        else if(partitioned_header.length == 6)
            chunk_no = partitioned_header[4];
            replic_deg = partitioned_header[5];

        System.out.println("message.Header parsed");
    }

    byte[] getHeaderMsg(){
        return header_data.getBytes();
    }

    private int buildHeader(){
        if(!checkParams())
            return -1;

        String file_id_to_64B_ascii = convertToHexString(file_id);
        //System.out.println("converted to 64B ascii: " + file_id_to_64B_ascii);

        header_data = type+ ' ' + version + ' ' + sender_id + ' ' + file_id_to_64B_ascii + ' ' + chunk_no + ' ' + replic_deg + ' ' + CRLF+CRLF;
        return 0;
    }

    private boolean checkParams(){
        return version.matches(versionPattern) && (file_id.length() == file_id_length) &&
                (chunk_no.length() <= chunk_no_max_length) && (replic_deg.equals("")  || Integer.parseInt(replic_deg) <= 9);
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

        for(byte element : converted){
            result += Integer.toString((int)element,16);
        }
        return result;
    }

    public String parseHexString(String string){

        StringBuilder output = new StringBuilder();

        for (int i = 0; i < string.length(); i+=2) {
            String str = string.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }

        return output.toString();
    }


    public static void main(String[] args) {

        //message.Header h = new message.Header("tipox","1.0","omeuid","sha256sha256sha256sha256sha256sh","2","2");
       Header h = new Header("tipox 1.0 omeuid sha256sha256sha256sha256sha256sh 2 2\r\n\r\n".getBytes());
        System.out.println(h.convertToHexString(h.getFile_id()));
       System.out.println(h.parseHexString(h.convertToHexString(h.getFile_id())));

    }
}
