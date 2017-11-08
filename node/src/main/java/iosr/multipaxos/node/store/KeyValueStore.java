package iosr.multipaxos.node.store;

import iosr.multipaxos.common.command.GetCommand;
import iosr.multipaxos.common.command.PutCommand;
import iosr.multipaxos.common.command.RemoveCommand;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
@Component
public class KeyValueStore {

    private final Map<Object, Object> store = new ConcurrentHashMap<>();

    public Object put(PutCommand putCommand) {
        return store.put(putCommand.getKey(), putCommand.getValue());
    }

    public Object remove(RemoveCommand removeCommand) {
        return store.remove(removeCommand.getKey());
    }

    public Object get(GetCommand getCommand) {
        return store.get(getCommand.getKey());
    }

    public Map<Object, Object> getAll() {
        return Collections.unmodifiableMap(store);
    }
}
