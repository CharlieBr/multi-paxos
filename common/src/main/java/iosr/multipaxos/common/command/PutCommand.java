package iosr.multipaxos.common.command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class PutCommand implements Command {

    private final CommandType commandType = CommandType.PUT;

    private String id;

    private Object key;
    private Object value;

    public PutCommand() {
    }

    public PutCommand(String id, Object key, Object value) {
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
    public String getId() {
        return id;
    }

    @Override
    public Command withIncrementedId(final String id) {
        return new PutCommand(id, this.key, this.value);
    }
}
