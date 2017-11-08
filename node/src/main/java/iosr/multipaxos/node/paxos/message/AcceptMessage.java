package iosr.multipaxos.node.paxos.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import iosr.multipaxos.common.command.Command;
import iosr.multipaxos.node.communication.CommandDeserializer;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class AcceptMessage implements Message {

    private int proposalNumber;

    private int index;

    private Command value;

    private int firstUnchosenIndex;

    public AcceptMessage() {
    }

    public AcceptMessage(int proposalNumber, Command value, int index, int firstUnchosenIndex) {
        this.proposalNumber = proposalNumber;
        this.index = index;
        this.value = value;
        this.firstUnchosenIndex = firstUnchosenIndex;
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

    @JsonDeserialize(using = CommandDeserializer.class)
    public void setValue(Command value) {
        this.value = value;
    }

    public int getFirstUnchosenIndex() {
        return firstUnchosenIndex;
    }

    public void setFirstUnchosenIndex(int firstUnchosenIndex) {
        this.firstUnchosenIndex = firstUnchosenIndex;
    }
}
