package message;

 /**
 * Created by Sonhs on 13/03/2016.
 */

public class Header {

    public static final String CRLF = "\r\n";

    private static final String versionPattern = "\\d.\\d";
    private static final int file_id_raw_length = 32, //Bytes -> 256 bits (SHA256)
            file_id_hex_length = 64,
            chunk_no_max_length = 6; //Bytes


    private String type = "", version = "", sender_id = "", file_id = "", chunk_no = "", replic_deg = "";
    private String header_data = "";

    public Header(byte[] header_info) {
        parseHeader(header_info);

        if (!checkParams()) {
            System.out.println("Incorrect header parameters \n");
            System.exit(1);
        }
        header_data = type + ' ' + version + ' ' + sender_id + ' ' + file_id + ' ' + chunk_no + ' ' + replic_deg + ' ' + CRLF + CRLF;

    }

    public Header(String MessageType, String Version, String SenderId, String FileId, String ChunkNo, String ReplicationDeg) {

        type = MessageType;
        version = Version;
        sender_id = SenderId;
        file_id = FileId;
        chunk_no = ChunkNo;
        replic_deg = ReplicationDeg;

        if (!checkParams()) {
            System.out.println("Incorrect header parameters \n");
            System.exit(1);
        }
        header_data = type + ' ' + version + ' ' + sender_id + ' ' + file_id + ' ' + chunk_no + ' ' + replic_deg + ' ' + CRLF + CRLF;
    }

    public Header(String MessageType, String Version, String SenderId, String FileId, String ChunkNo) {

        type = MessageType;
        version = Version;
        sender_id = SenderId;
        file_id = FileId;
        chunk_no = ChunkNo;

        if (!checkParams()) {
            System.out.println("Incorrect header parameters \n");
            System.exit(1);
        }
        header_data = type + ' ' + version + ' ' + sender_id + ' ' + file_id + ' ' + chunk_no + ' ' + replic_deg + ' ' + CRLF + CRLF;
    }

    public Header(String MessageType, String Version, String SenderId, String FileId) {

        type = MessageType;
        version = Version;
        sender_id = SenderId;
        file_id = FileId;

        if (!checkParams()) {
            System.out.println("Incorrect header parameters \n");
            System.exit(1);
        }
        header_data = type + ' ' + version + ' ' + sender_id + ' ' + file_id + ' ' + chunk_no + ' ' + replic_deg + ' ' + CRLF + CRLF;
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
        return type +" "+version +" "+sender_id +" "+file_id +" "+chunk_no +" "+replic_deg;
    }

    private void parseHeader(byte[] header_info) {
        //"\\p{Space}+" -> regex separa tokens intervalados por 1+ espaços
        String partitioned_header[] = new String(header_info).split("\\p{Space}+");

        //System.out.println("Header Tokens");

        type = partitioned_header[0];
        version = partitioned_header[1];
        sender_id = partitioned_header[2];
        file_id = partitioned_header[3];

        if (partitioned_header.length == 5)
            chunk_no = partitioned_header[4];
        else if (partitioned_header.length == 6){
            chunk_no = partitioned_header[4];
            replic_deg = partitioned_header[5];
        }

        //System.out.println("message.Header parsed");
    }

    byte[] getHeaderMsg() {
        return header_data.getBytes();
    }


    private boolean checkParams() {

        return version.matches(versionPattern) && (file_id.length() == file_id_raw_length || file_id.length() == file_id_hex_length) &&
                (chunk_no.length() <= chunk_no_max_length) && (replic_deg.equals("") || Integer.parseInt(replic_deg) <= 9);
    }
}
