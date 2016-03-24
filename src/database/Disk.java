package database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by diogo on 16/03/2016.
 */
public class Disk implements Serializable{

    private static final int CAPACITY = 2000000;

    private int capacityBytes=CAPACITY;

    public ArrayList<Chunk> chuncks =new ArrayList<>();

    public HashMap<String,File> files =new HashMap<>(); //File name to hash

    public Disk(){
    }

}
