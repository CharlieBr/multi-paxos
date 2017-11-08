package iosr.multipaxos.common.command;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public interface Command {
    CommandType getCommandType();

    String getId();

    Command withIncrementedId(String id);
}
