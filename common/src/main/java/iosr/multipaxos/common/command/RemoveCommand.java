package iosr.multipaxos.common.command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class RemoveCommand implements Command {

    private final CommandType commandType = CommandType.REMOVE;

    private String id;

    private Object key;

    public RemoveCommand() {
    }

    public RemoveCommand(String id, Object key) {
        this.id = id;
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }

    @Override
    public String getId() {
        return id;
    }
}
