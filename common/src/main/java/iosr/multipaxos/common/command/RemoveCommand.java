package iosr.multipaxos.common.command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class RemoveCommand implements Command {

    private final CommandType commandType = CommandType.REMOVE;

    private int id;

    private Object key;

    public RemoveCommand() {
    }

    public RemoveCommand(int id, Object key) {
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
    public int getId() {
        return id;
    }

    @Override
    public Command withIncrementedId(final int id) {
        return new RemoveCommand(id, this.key);
    }
}
