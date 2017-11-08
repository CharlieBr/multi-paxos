package iosr.multipaxos.common.command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class GetCommand implements Command {

    private final CommandType commandType = CommandType.GET;

    private Object key;

    public GetCommand() {
    }

    public GetCommand(Object key) {
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
