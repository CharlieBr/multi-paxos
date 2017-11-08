package iosr.multipaxos.common.command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class GetCommand implements Command {

    private final CommandType commandType = CommandType.GET;

    private String id;

    private Object key;

    public GetCommand() {
    }

    public GetCommand(String id, Object key) {
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

    @Override
    public Command withIncrementedId(String id) {
        return new GetCommand(id, this.key);
    }
}
