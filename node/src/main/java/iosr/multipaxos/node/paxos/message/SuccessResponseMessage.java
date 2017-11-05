package iosr.multipaxos.node.paxos.message;

/**
 * Created by Leszek Placzkiewicz on 05.11.17.
 */
public class SuccessResponseMessage implements Message {

    private int firstUnchosenIndex;


    public SuccessResponseMessage() {
    }

    public SuccessResponseMessage(int firstUnchosenIndex) {
        this.firstUnchosenIndex = firstUnchosenIndex;
    }

    public int getFirstUnchosenIndex() {
        return firstUnchosenIndex;
    }

    public void setFirstUnchosenIndex(int newFirstUnchosenIndex) {
        firstUnchosenIndex = newFirstUnchosenIndex;
    }

}
