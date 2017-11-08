package iosr.multipaxos.common.command;

/**
 * Created by Leszek Placzkiewicz on 08.11.17.
 */
public class CommandResponse {

    private Object value;

    public CommandResponse() {
    }

    public CommandResponse(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
