package iosr.multipaxos.node.controller;

import iosr.multipaxos.common.command.GetCommand;
import iosr.multipaxos.common.command.PutCommand;
import iosr.multipaxos.common.command.RemoveCommand;
import iosr.multipaxos.node.paxos.MultiPaxosHandler;
import iosr.multipaxos.node.paxos.MultiPaxosInfoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
@RestController
@RequestMapping("/store")
public class StoreController {

    private final Logger LOG = LoggerFactory.getLogger(StoreController.class);

    @Autowired
    private MultiPaxosHandler multiPaxosHandler;

    @Autowired
    private MultiPaxosInfoManager multiPaxosInfoManager;


    @RequestMapping(method = RequestMethod.PUT)
    public Object put(@RequestBody PutCommand putCommand) {
        LOG.info("Received PUT command: " + putCommand.getKey() + ":" + putCommand.getValue());

        if (!multiPaxosInfoManager.isLeader()) {
            return prepareRedirectResponse();
        }

        Object result = multiPaxosHandler.executeMultiPaxos(putCommand);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public Object remove(@RequestBody RemoveCommand removeCommand) {
        LOG.info("Received REMOVE command: " + removeCommand.getKey());

        if (!multiPaxosInfoManager.isLeader()) {
            return prepareRedirectResponse();
        }

        Object result = multiPaxosHandler.executeMultiPaxos(removeCommand);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    public Object get(@PathVariable(value="key") String key) {
        LOG.info("Received GET command: " + key);

        if (!multiPaxosInfoManager.isLeader()) {
            return prepareRedirectResponse();
        }

        GetCommand getCommand = new GetCommand(key);
        Object result = multiPaxosHandler.executeMultiPaxos(getCommand);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private ResponseEntity prepareRedirectResponse() {
        Map<Object, Object> body = new HashMap<>();
        body.put("leaderId", multiPaxosInfoManager.getLeaderId());
        return new ResponseEntity<>(body, HttpStatus.TEMPORARY_REDIRECT);
    }

}
