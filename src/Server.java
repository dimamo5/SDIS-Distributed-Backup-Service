/**
 * Created by Sonhs on 12/03/2016.
 */
public class Server {

    String sv_id, MC_ip, MDB_ip,MDR_ip;
    int MC_port,MDB_port,MDR_port;

    Server(String id,String mc_ip, int mc_port, String mdb_ip,int mdb_port, String mdr_ip, int mdr_port ){
        sv_id = id;
        MC_ip = mc_ip;
        MC_port = mc_port;
        MDB_ip = mdb_ip;
        MDB_port = mdb_port;
        MDR_ip = mdr_ip;
        MDR_port = mdr_port;
    }
}
