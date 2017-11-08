package iosr.multipaxos.common.command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class PutCommand implements Command {

    private final CommandType commandType = CommandType.PUT;

    private int id;

    private Object key;
    private Object value;

    public PutCommand() {
    }

    public PutCommand(int id, Object key, Object value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
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
        return new PutCommand(id, this.key, this.value);
    }
}
