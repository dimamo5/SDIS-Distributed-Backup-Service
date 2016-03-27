package protocol;

import service.MessageSender;
import service.Peer;

import java.io.File;

/**
 * Created by diogo on 26/03/2016.
 */
public class Delete implements Runnable {
    private String filename;

    public Delete(String filename) {
        this.filename = filename;
    }

    @Override
    public void run() {
        File f = new File(this.filename);
        if(f.exists() && !f.isDirectory()) {
            f.delete();
            System.out.println("Deleted " + this.filename);
        }

        if (Peer.getDisk().hasFile(this.filename)) {
            String filehash = Peer.getDisk().files.get(filename).getFilehash();

            new MessageSender().deleteMessage(Peer.getId(),filehash);

            Peer.getDisk().files.remove(filename);

            System.out.println("File deleted from the network!");
        } else {
            System.out.println("No such file in the network");
        }
    }
}
