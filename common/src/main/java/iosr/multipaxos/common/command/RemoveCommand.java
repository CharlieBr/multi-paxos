package iosr.multipaxos.common.command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class RemoveCommand implements Command {

    private final CommandType commandType = CommandType.REMOVE;

    private Object key;

    public RemoveCommand() {
    }

    public RemoveCommand(Object key) {
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }
}
