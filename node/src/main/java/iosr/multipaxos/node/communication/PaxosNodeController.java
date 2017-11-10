package iosr.multipaxos.node.communication;

import iosr.multipaxos.common.command.*;
import iosr.multipaxos.node.paxos.HeartbeatManager;
import iosr.multipaxos.node.paxos.MultiPaxosInfoManager;
import iosr.multipaxos.node.paxos.message.*;
import iosr.multipaxos.node.store.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Leszek Placzkiewicz on 05.11.17.
 */
@RestController
@RequestMapping("/paxos")
public class PaxosNodeController {

    private final Logger LOG = LoggerFactory.getLogger(PaxosNodeController.class);

    @Autowired
    private MultiPaxosInfoManager multiPaxosInfoManager;

    @Autowired
    private HeartbeatManager heartbeatManager;

    @Autowired
    private KeyValueStore keyValueStore;


    @RequestMapping(path = "/prepare", method = RequestMethod.POST)
    public Object prepare(@RequestBody PrepareMessage prepareMessage) {
        LOG.info("Received PREPARE message: (" + prepareMessage.getIndex() + ", " + prepareMessage.getProposalNumber() + ")");

        PromiseMessage promiseMessage = handlePrepareMessage(prepareMessage);
        return new ResponseEntity<>(promiseMessage, HttpStatus.OK);
    }

    @RequestMapping(path = "/accept", method = RequestMethod.POST)
    public Object accept(@RequestBody AcceptMessage acceptMessage) {
        LOG.info("Received ACCEPT message: (" + acceptMessage.getIndex() + ", " + acceptMessage.getProposalNumber()
                + ", " + acceptMessage.getValue().getCommandType() + ")");

        AcceptedMessage acceptedMessage = handleAcceptMessage(acceptMessage);
        return new ResponseEntity<>(acceptedMessage, HttpStatus.OK);
    }

    @RequestMapping(path = "/success", method = RequestMethod.POST)
    public Object success(@RequestBody SuccessMessage successMessage) {
        LOG.info("Received SUCCESS message: (" + successMessage.getIndex() + ")");

        SuccessResponseMessage successResponseMessage = handleSuccessMessage(successMessage);
        return new ResponseEntity<>(successResponseMessage, HttpStatus.OK);
    }

    @RequestMapping(path = "/heartbeat", method = RequestMethod.POST)
    public Object heartbeat(@RequestBody HeartbeatMessage heartbeatMessage) {
        LOG.info("Received HEARTBEAT message: (" + heartbeatMessage.getId() + ")");

        heartbeatManager.receive(heartbeatMessage);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private PromiseMessage handlePrepareMessage(PrepareMessage prepareMessage) {
        int proposalNumber = prepareMessage.getProposalNumber();
        int minProposal = multiPaxosInfoManager.getMinProposal();
        int index = prepareMessage.getIndex();
        int acceptedProposal = multiPaxosInfoManager.getAcceptedProposal(index);
        Command acceptedValue = multiPaxosInfoManager.getAcceptedValue(index);

        if (index > multiPaxosInfoManager.getLastLogIndex()) {
            multiPaxosInfoManager.setLastLogIndex(index);
        }

        if (proposalNumber > minProposal) {
            multiPaxosInfoManager.setMinProposal(proposalNumber);
        }

        boolean noMoreAccepted = true;
        for (int idx = index; idx < multiPaxosInfoManager.getLastLogIndex(); idx++) {
            if (multiPaxosInfoManager.getAcceptedProposal(idx) != 0) {
                noMoreAccepted = false;
                break;
            }
        }

        return new PromiseMessage(acceptedProposal, acceptedValue, noMoreAccepted);
    }

    private AcceptedMessage handleAcceptMessage(AcceptMessage acceptMessage) {
        int minProposal = multiPaxosInfoManager.getMinProposal();
        int n = acceptMessage.getProposalNumber();
        int index = acceptMessage.getIndex();
        if (index > multiPaxosInfoManager.getLastLogIndex()) {
            multiPaxosInfoManager.setLastLogIndex(index);
        }

        int firstUnchosenIndex = multiPaxosInfoManager.getFirstUnchosenIndex();

        if (n >= minProposal) {
            multiPaxosInfoManager.setAcceptedProposal(index, n);
            multiPaxosInfoManager.setAcceptedValue(index, acceptMessage.getValue());
            multiPaxosInfoManager.setMinProposal(n);
        }

        for(int idx=0; idx<acceptMessage.getFirstUnchosenIndex(); idx++) {
            if (multiPaxosInfoManager.getAcceptedProposal(idx) == n) {
                multiPaxosInfoManager.setAcceptedProposal(idx, Integer.MAX_VALUE);
            }
        }

        return new AcceptedMessage(multiPaxosInfoManager.getMinProposal(), firstUnchosenIndex);
    }

    private SuccessResponseMessage handleSuccessMessage(SuccessMessage successMessage) {
        int index = successMessage.getIndex();
        Command value = successMessage.getValue();

        multiPaxosInfoManager.setAcceptedValue(index, value);
        multiPaxosInfoManager.setAcceptedProposal(index, Integer.MAX_VALUE);

        int lastLogIndex = multiPaxosInfoManager.getLastLogIndex();
        int firstUnchosenIndex = multiPaxosInfoManager.getFirstUnchosenIndex();
        multiPaxosInfoManager.setFirstUnchosenIndex(lastLogIndex + 1);
        for(int idx=firstUnchosenIndex; idx<lastLogIndex; idx++) {
            if (multiPaxosInfoManager.getAcceptedProposal(idx) != Integer.MAX_VALUE) {
                multiPaxosInfoManager.setFirstUnchosenIndex(idx);
                break;
            }
        }
        executeCommand(value);

        return new SuccessResponseMessage(multiPaxosInfoManager.getFirstUnchosenIndex());
    }

    private void executeCommand(Command command) {
        CommandType commandType = command.getCommandType();
        if (commandType == CommandType.PUT) {
            keyValueStore.put((PutCommand) command);
        } else if (commandType == CommandType.GET) {
            keyValueStore.get((GetCommand) command);
        } else if (commandType == CommandType.REMOVE) {
            keyValueStore.remove((RemoveCommand) command);
        }
    }

}
