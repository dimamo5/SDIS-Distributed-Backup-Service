package database;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Disk implements Serializable{

    private static final long MAX_CAPACITY = 2000000;

    private long spaceUsage=0;

    public ArrayList<Chunk> chunks = new ArrayList<>();

    public HashMap<String,File> files = new HashMap<>(); //File name to hash

    public Disk(){
        if(!folderExists("files")){
            createFolder("files");
        }
        if(!folderExists("restore")){
            createFolder("restore");
        }
        if(!folderExists("chunks")){
            createFolder("chunks");
        }
    }

    /* METHODS */

    private long getFreeSpace(){
        return MAX_CAPACITY - spaceUsage;
    }

    public long releaseMemory(long space){
        if(space > MAX_CAPACITY)
            return -1;
        else {
            if (spaceUsage > space)
                spaceUsage -= space;
            else spaceUsage = 0;

            return 0; //retorna espaço disponivel/usado ?
        }
    }

    private long useSpace(long space){

        if(space > getAvailableMemory()){
            System.out.println("Not enough memory in disk");
            return -1;
        }
        else {
            spaceUsage += space;
            return 0;
        }
    }


    private long getAvailableMemory() {
        return MAX_CAPACITY - spaceUsage;
    }

    public long getSpaceUsage() {
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

        if(!folderExists("chunks/" + c.getFileId())){
            createFolder("chunks/"+c.getFileId());
        }

        /* Creates chunk file */
        String chunkFileNameOut = "chunks/" + c.getFileId()+"/" + c.getChunkNo();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(chunkFileNameOut);
            fos.write(c.getDataBlock());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //actualiza espaço no disco
        useSpace(c.getDataLength());
    }

    public boolean canSaveChunk(int dataLength) {

        return dataLength <= getFreeSpace();
    }

    public boolean hasFile(String filename){
        return files.containsKey(filename);
    }


    public boolean hasFileByHash(String filehash){
        Iterator it = this.files.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            File f= (File)pair.getValue();
            if(f.getFilehash().equals(filehash))
                return true;
            it.remove(); // avoids a ConcurrentModificationException
        }
        return false;
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

    @Override
    public String toString() {
        return "Disk{" +
                "spaceUsage=" + spaceUsage +
                ", chunks=" + chunks +
                ", files=" + files +
                '}';
    }

    public StoredChunk loadChunk(String fileId, String chunkNo){

        Path path = Paths.get("chunks/" + fileId+"/" + chunkNo);
        StoredChunk sc=null;
        try {
            byte[] data = Files.readAllBytes(path);
            sc=new StoredChunk(fileId,Integer.parseInt(chunkNo),-1,data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sc;
    }

    public void addFile(String name,String hash,int numChunks){
        this.files.put(name,new File(name,hash,numChunks));
    }

    public ArrayList<Chunk> getChunkFromFileId(String filehash){
        ArrayList<Chunk> chunksFromFile=new ArrayList<>();
        for(int i =0;i<chunks.size();i++){
            if(chunks.get(i).getFileId().equals(filehash)){
                chunksFromFile.add(chunks.get(i));
            }
        }
        return chunksFromFile;
    }

    public void removeChunk(Chunk c){
        java.io.File file = new java.io.File("chunks/" + c.getFileId()+"/"+c.getChunkNo());
        long filesize = file.length();

        this.releaseMemory(filesize);
        file.delete();

        chunks.remove(c);
        System.out.println("Database size: "+chunks.size());
    }

    public ArrayList<Chunk> sortLessImportantChunk(){
        ArrayList<Chunk> returnArray =new ArrayList<>(this.chunks);
        Collections.sort(returnArray);
        return returnArray;
    }

    public Chunk getChunk(String filehash,int chunkNo){
        for(Chunk c:chunks){
            if(c.getChunkNo()==chunkNo && c.getFileId().equals(filehash)){
                return c;
            }
        }
        return null;
    }

    public void removeFolder(String folder){
        if(folderExists("files/"+folder)){
            java.io.File f = new java.io.File("files/" + folder);
            f.delete();
        }


    }
}
