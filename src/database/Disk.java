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

    private int spaceUsage=0;

    public ArrayList<Chunk> chunks =new ArrayList<>();

    public HashMap<String,File> files = new HashMap<>(); //File name to hash

    public Disk(){
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

    public boolean hasChunk(String file_id, int chunk_no) {

        for(int i =0;i<chunks.size();i++){
            Chunk c= chunks.get(i);
            if(c.getChunkNo()==chunk_no && c.getFileId().equals(file_id)){
                return true;
            }
        }
        return false;
    }
    public void addChunkMirror(Chunk c,String peerId){
        for(int i =0;i<chunks.size();i++){
            if(c.getChunkNo()==chunks.get(i).getChunkNo() && c.getFileId().equals(chunks.get(i).getFileId())){
                chunks.get(i).addPeer(peerId);
            }
        }

    }
    //TODO VERIFICAR ISTO
    public void storeChunk(StoredChunk c){

        if(!folderExists("files/" + c.getFileId())){
            createFolder("files/"+c.getFileId());
        }

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

    private  boolean folderExists(String name) {
        java.io.File file = new java.io.File(name);

        return file.exists() && file.isDirectory();
    }

    private  void createFolder(String name) {
        java.io.File file = new java.io.File(name);

        file.mkdir();
    }

    public void addChunk(Chunk c){
        this.chunks.add(c);

    }
}
