/**
 * Created by Sonhs on 12/03/2016.
 */
public class Peer {

    //valores por defeito

    private String id;
    private String MC_ip ="224.0.0.1", MDB_ip="224.0.1.0", MDR_ip="224.1.0.0";
    private int MC_port = 10001, MDB_port = 10002, MDR_port = 10003;

    Peer(String params[]){
        initAttr(params);
    }

    private void initAttr(String args[]){

        this.id = args[0];

        //config ip/port
        if(args.length > 1) {
            MC_ip = args[1];
            MC_port = Integer.parseInt(args[2]);
            MDB_ip = args[3];
            MDB_port = Integer.parseInt(args[4]);
            MDR_ip = args[5];
            MDR_port = Integer.parseInt(args[6]);
        }
    }


}
