package iosr.multipaxos.node.paxos.message;

import iosr.multipaxos.common.command.Command;

/**
 * Created by Leszek Placzkiewicz on 05.11.17.
 */
public class SuccessMessage implements Message {

    private final int index;

    private final Command value;

    public SuccessMessage(int index, Command value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public Command getValue() {
        return value;
    }

}
