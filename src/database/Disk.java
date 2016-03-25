package database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by diogo on 16/03/2016.
 */
public class Disk implements Serializable{

    private static final int MAX_CAPACITY = 2000000;

    private int spaceUsage;

    public ArrayList<Chunk> chunks =new ArrayList<>();

    public HashMap<String,File> files =new HashMap<>(); //File name to hash

    public Disk(){
        spaceUsage = 0;
    }

    /* METHODS */

    public int getFreeSpace(){
        return MAX_CAPACITY - spaceUsage;
    }

    public int releaseMemory(int space){
        if(space > MAX_CAPACITY)
            return -1;
        else {
            if (spaceUsage > space)
                spaceUsage -= space;
            else spaceUsage = 0;

            return 0; //retorna espaço disponivel/usado ?
        }
    }

    public int useSpace(int space){

        if(space > getAvailableMemory()){
            System.out.println("Not enough memory in disk");
            return -1;
        }
        else {
            spaceUsage += space;
            return 0;
        }
    }


    private int getAvailableMemory() {
        return MAX_CAPACITY - spaceUsage;
    }

    public int getSpaceUsage() {
        return spaceUsage;
    }

    public ArrayList<Chunk> getChunks() {
        return chunks;
    }

    public HashMap<String, File> getFiles() {
        return files;
    }
}
