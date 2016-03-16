import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by diogo on 16/03/2016.
 */
public class Disk implements Serializable{

    public HashMap<String,ArrayList<Integer>> database = new HashMap<>();

    public Disk(){

    }
}
