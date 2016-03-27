package protocol;

import channel.MCChannel;
import channel.MDRChannel;
import database.StoredChunk;
import message.Message;
import service.MessageSender;
import service.Peer;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by diogo on 26/03/2016.
 */
public class BackupChunk  implements Runnable,Observer{

    public static final long INITIAL_WAITING_TIME = 500;
    public static final int MAX_ATTEMPTS = 5;

    private StoredChunk chunk;
    private ArrayList<Integer> confirmationReceived = new ArrayList<>();

    public BackupChunk(StoredChunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void run(){
        MessageSender sender=new MessageSender();

        long waitingTime = INITIAL_WAITING_TIME;
        int attempt = 0;

        boolean done = false;
        while (!done) {
            sender.putChunkMessage(Peer.getId(),this.chunk);

            try {
                System.out.println("Waiting for STOREDs for " + waitingTime
                        + "ms");
                Thread.sleep(waitingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int confirmedRepDeg =confirmationReceived.size();

            System.out.println(confirmedRepDeg + " peers have backed up chunk no. "
                    + chunk.getChunkNo() + ". (desired: "
                    + chunk.getReplicationDegree() + " )");

            if (confirmedRepDeg < chunk.getReplicationDegree()) {
                attempt++;

                if (attempt > MAX_ATTEMPTS) {
                    System.out.println("Reached maximum number of attempts to backup chunk with desired replication degree.");
                    done = true;
                } else {
                    System.out.println("Desired replication degree was not reached. Trying again...");
                    waitingTime *= 2;
                }
            } else {
                System.out.println("Desired replication degree reached.");
                done = true;
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Chamou o update");
        if (o instanceof MCChannel && arg instanceof Message) {
            Message m = (Message) arg;
            if(this.chunk.getFileId().equals(m.getHeader().getFile_id())){
                this.confirmationReceived.add(Integer.parseInt(m.getHeader().getSender_id()));
            }
        }
    }
}
