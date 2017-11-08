package iosr.multipaxos.node.paxos.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import iosr.multipaxos.common.command.Command;
import iosr.multipaxos.node.communication.CommandDeserializer;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class PromiseMessage implements Message {

    private int acceptedProposal;

    private Command acceptedValue;

    private boolean noMoreAccepted;


    public PromiseMessage() {
    }

    public PromiseMessage(int acceptedProposal, Command acceptedValue, boolean noMoreAccepted) {
        this.acceptedProposal = acceptedProposal;
        this.acceptedValue = acceptedValue;
        this.noMoreAccepted = noMoreAccepted;
    }

    public int getAcceptedProposal() {
        return acceptedProposal;
    }

    public void setAcceptedProposal(int acceptedProposal) {
        this.acceptedProposal = acceptedProposal;
    }

    public Command getAcceptedValue() {
        return acceptedValue;
    }

    @JsonDeserialize(using = CommandDeserializer.class)
    public void setAcceptedValue(Command acceptedValue) {
        this.acceptedValue = acceptedValue;
    }

    public boolean isNoMoreAccepted() {
        return noMoreAccepted;
    }

    public void setNoMoreAccepted(boolean noMoreAccepted) {
        this.noMoreAccepted = noMoreAccepted;
    }

}
