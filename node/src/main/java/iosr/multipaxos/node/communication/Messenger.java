package iosr.multipaxos.node.communication;

import iosr.multipaxos.node.paxos.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
@Component
public class Messenger {

    private final Logger LOG = LoggerFactory.getLogger(Messenger.class);


    private final String PREPARE_PATH = "/paxos/prepare";

    private final String ACCEPT_PATH = "/paxos/accept";

    private final String HEARTBEAT_PATH = "/paxos/heartbeat";

    private final String SUCCESS_PATH = "/paxos/success";


    private final RestTemplate restTemplate = new RestTemplate();


    public PromiseMessage sendPrepareMessage(PrepareMessage prepareMessage, NodeAddress nodeAddress) {
        LOG.info("Sending PREPARE message to: " + nodeAddress.getHost() + ":" + nodeAddress.getPort());
        String url = prepareUrl(nodeAddress, PREPARE_PATH);
        return restTemplate.postForObject(url, prepareMessage, PromiseMessage.class);
    }

    public AcceptedMessage sendAcceptMessage(AcceptMessage acceptMessage, NodeAddress nodeAddress) {
        LOG.info("Sending ACCEPT message to: " + nodeAddress.getHost() + ":" + nodeAddress.getPort());
        String url = prepareUrl(nodeAddress, ACCEPT_PATH);
        return restTemplate.postForObject(url, acceptMessage, AcceptedMessage.class);
    }

    public SuccessResponseMessage sendSuccessMessage(SuccessMessage successMessage, NodeAddress nodeAddress) {
        LOG.info("Sending SUCCESS message to: " + nodeAddress.getHost() + ":" + nodeAddress.getPort());
        String url = prepareUrl(nodeAddress, SUCCESS_PATH);
        return restTemplate.postForObject(url, successMessage, SuccessResponseMessage.class);
    }

    public void sendHeartbeatMessage(HeartbeatMessage heartbeatMessage, NodeAddress nodeAddress) {
        LOG.info("Sending HEARTBEAT message to: " + nodeAddress.getHost() + ":" + nodeAddress.getPort()
                + ", with id: " + heartbeatMessage.getId());
        String url = prepareUrl(nodeAddress, HEARTBEAT_PATH);
        restTemplate.postForObject(url, heartbeatMessage, Object.class);
    }

    private String prepareUrl(NodeAddress nodeAddress, String path) {
        return "http://" + nodeAddress.getHost() + ":" + nodeAddress.getPort() + path;
    }

}
