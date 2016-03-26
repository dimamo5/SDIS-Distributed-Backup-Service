package database;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public HashMap<String,File> files = new HashMap<>(); //File name to hash

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

    private int useSpace(int space){

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

    public boolean hasChunk(String file_id, String chunk_no) {

        for(int i =0;i<chunks.size();i++){
            Chunk c= chunks.get(i);
            if(c.getChunkNo().equals(chunk_no) && c.getFileId().equals(file_id)){
                return true;
            }
        }
        return false;
    }

    //TODO VERIFICAR ISTO
    public void storeChunk(StoredChunk c){

        /* Creates chunk file */
        String chunkFileNameOut = "files/" + c.getFileId()+"/" + c.getChunkNo();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(chunkFileNameOut);
            fos.write(c.getDataBlock());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //actualiza espaço no disco
        useSpace(c.getDataLength());

        //TODO regista chunk no hashmap
        //files ou chunks ??

    }

    public boolean canSaveChunk(int dataLength) {

        return dataLength <= getFreeSpace();
    }

    public boolean hasFile(String filename){
        return files.containsKey(filename);
    }
}
