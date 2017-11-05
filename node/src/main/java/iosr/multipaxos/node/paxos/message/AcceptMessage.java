package iosr.multipaxos.node.paxos.message;

import iosr.multipaxos.common.command.Command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class AcceptMessage implements Message {

    private int proposalNumber;

    private int index;

    private Command value;

    public AcceptMessage() {
    }

    public AcceptMessage(int proposalNumber, Command value, int index) {
        this.proposalNumber = proposalNumber;
        this.index = index;
        this.value = value;
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

    public void setIndex(int index) {
        this.index = index;
    }

    public Command getValue() {
        return value;
    }

    public void setValue(Command value) {
        this.value = value;
    }

}
