/**
 * Created by Sonhs on 23/03/2016.
 */
public interface Handler {

    /*dispatchs messages type X to the correspondent function: "processX"*/
    void dispatcher(String type,Message message);

    void processPutChunk(Message message);

    void processStored(Message message);

    void processGetChunk(Message message);

    void processChunk(Message message);

    void processDelete(Message message);

    void processRemoved(Message message);
}
