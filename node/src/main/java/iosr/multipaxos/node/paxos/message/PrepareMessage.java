package iosr.multipaxos.node.paxos.message;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class PrepareMessage implements Message {

    private int proposalNumber;

    private int index;


    public PrepareMessage() {
    }

    public PrepareMessage(int proposalNumber, int index) {
        this.proposalNumber = proposalNumber;
        this.index = index;
    }

    public int getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(int proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int newIndex) {
        index = newIndex;
    }

}
