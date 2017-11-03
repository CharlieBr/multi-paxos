package iosr.multipaxos.common.command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class RemoveCommand implements Command {

    private Object key;

    public RemoveCommand() {
    }

    public RemoveCommand(Object key) {
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

}
