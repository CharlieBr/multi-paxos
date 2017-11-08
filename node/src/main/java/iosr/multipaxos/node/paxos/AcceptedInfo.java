package iosr.multipaxos.node.paxos;

import iosr.multipaxos.common.command.Command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class AcceptedInfo {

    private final int acceptedProposal;

    private final Command acceptedValue;

    private final boolean noMoreAccepted;

    public AcceptedInfo(int acceptedProposal, Command acceptedValue, boolean noMoreAccepted) {
        this.acceptedProposal = acceptedProposal;
        this.acceptedValue = acceptedValue;
        this.noMoreAccepted = noMoreAccepted;
    }

    public int getAcceptedProposal() {
        return acceptedProposal;
    }

    public Command getAcceptedValue() {
        return acceptedValue;
    }

    public boolean isNoMoreAccepted() {
        return noMoreAccepted;
    }
}
