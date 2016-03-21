/**
 * Created by Sonhs on 12/03/2016.
 */
public class Server {

    private String sv_id, MC_ip, MDB_ip,MDR_ip;
    private int MC_port,MDB_port,MDR_port;

    Server(String id,String MC_ip, int MC_port, String MDB_ip,int MDB_port, String MDR_ip, int MDR_port ){
        sv_id = id;
        this.MC_ip = MC_ip;
        this.MC_port = MC_port;
        this.MDB_ip = MDB_ip;
        this.MDB_port = MDB_port;
        this.MDR_ip = MDR_ip;
        this.MDR_port = MDR_port;
    }
}
