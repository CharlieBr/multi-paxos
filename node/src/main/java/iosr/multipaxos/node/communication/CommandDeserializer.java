package iosr.multipaxos.node.communication;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import iosr.multipaxos.common.command.Command;
import iosr.multipaxos.common.command.GetCommand;
import iosr.multipaxos.common.command.PutCommand;
import iosr.multipaxos.common.command.RemoveCommand;

import java.io.IOException;

/**
 * Created by Leszek Placzkiewicz on 07.11.17.
 */
public class CommandDeserializer extends JsonDeserializer<Command> {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Command deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
        String commandType = objectMapper.treeToValue(treeNode.get("commandType"), String.class);
        if (commandType.equals("PUT")) {
            Object key = objectMapper.treeToValue(treeNode.get("key"), Object.class);
            Object value = objectMapper.treeToValue(treeNode.get("value"), Object.class);

            return new PutCommand(key, value);
        } else if (commandType.equals("REMOVE")) {
            Object key = objectMapper.treeToValue(treeNode.get("key"), Object.class);

            return new RemoveCommand(key);
        } else if (commandType.equals("GET")) {
            Object key = objectMapper.treeToValue(treeNode.get("key"), Object.class);

            return new GetCommand(key);
        }

        throw new AssertionError("Unknown command type.");
    }
}
