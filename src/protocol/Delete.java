package protocol;

import service.MessageSender;
import service.Peer;

import java.io.File;

/**
 * Created by diogo on 26/03/2016.
 */
public class Delete implements Runnable {
    private String filename;
    private final int MAX_DELETE_MESSAGE=3;

    public Delete(String filename) {
        this.filename = filename;
    }

    @Override
    public void run() {

        if (Peer.getDisk().hasFile(this.filename)) {
            String filehash = Peer.getDisk().files.get(filename).getFilehash();

            for(int i=0;i<MAX_DELETE_MESSAGE;i++)
                new MessageSender().deleteMessage(Peer.getId(),filehash);

            Peer.getDisk().files.remove(filename);

            System.out.println("File deleted from the network!");

            Peer.saveDisk();

            System.out.println("Size of files: "+Peer.getDisk().files.size());
        } else {
            System.out.println("No such file in the network");
        }
    }
}
