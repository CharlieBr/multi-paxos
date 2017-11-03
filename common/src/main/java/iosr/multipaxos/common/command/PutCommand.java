package iosr.multipaxos.common.command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class PutCommand implements Command {

    private Object key;
    private Object value;

    public PutCommand() {
    }

    public PutCommand(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
