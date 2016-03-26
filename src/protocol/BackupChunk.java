package protocol;

import database.StoredChunk;

/**
 * Created by diogo on 26/03/2016.
 */
public class BackupChunk  implements Runnable{

    public static final long INITIAL_WAITING_TIME = 500;
    public static final int MAX_ATTEMPTS = 5;

    private StoredChunk chunk;

    public BackupChunk(StoredChunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void run(){
        Peer.getMcListener().startSavingStoredConfirmsFor(chunk.getID());

        long waitingTime = INITIAL_WAITING_TIME;
        int attempt = 0;

        boolean done = false;
        while (!done) {
            Peer.getMcListener().clearSavedStoredConfirmsFor(chunk.getID());

            Peer.getCommandForwarder().sendPUTCHUNK(chunk);

            try {
                System.out.println("Waiting for STOREDs for " + waitingTime
                        + "ms");
                Thread.sleep(waitingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int confirmedRepDeg = Peer.getMcListener().getNumStoredConfirmsFor(
                    chunk.getID());

            Log.info(confirmedRepDeg + " peers have backed up chunk no. "
                    + chunk.getID().getChunkNo() + ". (desired: "
                    + chunk.getReplicationDegree() + " )");

            if (confirmedRepDeg < chunk.getReplicationDegree()) {
                attempt++;

                if (attempt > MAX_ATTEMPTS) {
                    Log.info("Reached maximum number of attempts to backup chunk with desired replication degree.");
                    done = true;
                } else {
                    Log.info("Desired replication degree was not reached. Trying again...");
                    waitingTime *= 2;
                }
            } else {
                Log.info("Desired replication degree reached.");
                done = true;
            }
        }

        Peer.getMcListener().stopSavingStoredConfirmsFor(chunk.getID());
    }
}
