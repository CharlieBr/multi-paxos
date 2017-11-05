package iosr.multipaxos.node.communication;

import iosr.multipaxos.common.command.Command;
import iosr.multipaxos.node.paxos.HeartbeatManager;
import iosr.multipaxos.node.paxos.MultiPaxosInfoManager;
import iosr.multipaxos.node.paxos.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @RequestMapping(path = "/prepare", method = RequestMethod.GET)
    public Object prepare(PrepareMessage prepareMessage) {
        LOG.info("Received prepare message: (" + prepareMessage.getIndex() + ", " + prepareMessage.getProposalNumber() + ")");

        PromiseMessage promiseMessage = handlePrepareMessage(prepareMessage);
        return new ResponseEntity<>(promiseMessage, HttpStatus.OK);
    }

    @RequestMapping(path = "/accept", method = RequestMethod.GET)
    public Object accept(AcceptMessage acceptMessage) {
        LOG.info("Received accept message: (" + acceptMessage.getIndex() + ", " + acceptMessage.getProposalNumber() + ")");

        AcceptedMessage acceptedMessage = handleAcceptMessage(acceptMessage);
        return new ResponseEntity<>(acceptedMessage, HttpStatus.OK);
    }

    @RequestMapping(path = "/success", method = RequestMethod.GET)
    public Object success(SuccessMessage successMessage) {
        LOG.info("Received success message: (" + successMessage.getIndex() + ")");

        Object response = handleSuccessMessage(successMessage);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/heartbeat", method = RequestMethod.GET)
    public Object heartbeat(HeartbeatMessage heartbeatMessage) {
        LOG.info("Received heartbeat message: (" + heartbeatMessage.getId() + ")");

        handleHeartbeatMessage(heartbeatMessage);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private PromiseMessage handlePrepareMessage(PrepareMessage prepareMessage) {
        int proposalNumber = prepareMessage.getProposalNumber();
        int minProposal = multiPaxosInfoManager.getMinProposal();
        int index = prepareMessage.getIndex();
        int acceptedProposal = multiPaxosInfoManager.getAcceptedProposal(index);

        Command acceptedValue = multiPaxosInfoManager.getAcceptedValue(index);
        boolean noMoreAccepted = multiPaxosInfoManager.getLastLogIndex() <= index;

        if (proposalNumber > minProposal) {
            multiPaxosInfoManager.setMinProposal(proposalNumber);
        }

        return new PromiseMessage(acceptedProposal, acceptedValue, noMoreAccepted);
    }

    private AcceptedMessage handleAcceptMessage(AcceptMessage acceptMessage) {
        int minProposal = multiPaxosInfoManager.getMinProposal();
        int n = acceptMessage.getProposalNumber();
        int index = acceptMessage.getIndex();
        int firstUnchosenIndex = multiPaxosInfoManager.getFirstUnchosenIndex();

        if (n >= minProposal) {
            multiPaxosInfoManager.setAcceptedProposal(index, n);
            multiPaxosInfoManager.setAcceptedValue(index, acceptMessage.getValue());
            multiPaxosInfoManager.setMinProposal(n);
        }

        return new AcceptedMessage(minProposal, firstUnchosenIndex);
    }

    private Object handleSuccessMessage(SuccessMessage successMessage) {
        int index = successMessage.getIndex();
        Command value = successMessage.getValue();

        multiPaxosInfoManager.setAcceptedValue(index, value);
        multiPaxosInfoManager.setAcceptedProposal(index, Integer.MAX_VALUE);

        throw new UnsupportedOperationException();
    }

    private void handleHeartbeatMessage(HeartbeatMessage heartbeatMessage) {
        heartbeatManager.receive(heartbeatMessage);
    }

}
