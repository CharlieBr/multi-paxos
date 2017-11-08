package iosr.multipaxos.node.paxos.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import iosr.multipaxos.common.command.Command;
import iosr.multipaxos.node.communication.CommandDeserializer;

/**
 * Created by Leszek Placzkiewicz on 05.11.17.
 */
public class SuccessMessage implements Message {

    private int index;

    private Command value;

    public SuccessMessage() {
    }

    public SuccessMessage(int index, Command value) {
        this.index = index;
        this.value = value;
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

}
