package iosr.multipaxos.node.store;

import iosr.multipaxos.common.command.GetCommand;
import iosr.multipaxos.common.command.PutCommand;
import iosr.multipaxos.common.command.RemoveCommand;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
@Component
public class KeyValueStore {

    private final Map<Object, Object> store = new HashMap<>();

    public Object put(PutCommand putCommand) {
        return store.put(putCommand.getKey(), putCommand.getValue());
    }

    public Object remove(RemoveCommand removeCommand) {
        return store.remove(removeCommand.getKey());
    }

    public Object get(GetCommand getCommand) {
        return store.get(getCommand.getKey());
    }

}
