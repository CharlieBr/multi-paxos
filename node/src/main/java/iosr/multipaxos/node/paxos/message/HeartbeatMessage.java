package iosr.multipaxos.node.paxos.message;

/**
 * Created by Leszek Placzkiewicz on 04.11.17.
 */
public class HeartbeatMessage implements Message {

    private int id;

    public HeartbeatMessage() {
    }

    public HeartbeatMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
