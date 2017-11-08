package iosr.multipaxos.node.paxos.message;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class AcceptedMessage implements Message {

    private int minProposal;

    private int firstUnchosenIndex;

    public AcceptedMessage() {
    }

    public AcceptedMessage(int minProposal, int firstUnchosenIndex) {
        this.minProposal = minProposal;
        this.firstUnchosenIndex = firstUnchosenIndex;
    }

    public int getMinProposal() {
        return minProposal;
    }

    public void setMinProposal(int minProposal) {
        this.minProposal = minProposal;
    }

    public int getFirstUnchosenIndex() {
        return firstUnchosenIndex;
    }

    public void setFirstUnchosenIndex(int firstUnchosenIndex) {
        this.firstUnchosenIndex = firstUnchosenIndex;
    }

}
