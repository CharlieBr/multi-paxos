package iosr.multipaxos.node.communication;

import iosr.multipaxos.common.command.Command;
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
        String queryParamsString = buildQueryParamsString(prepareMessage);
        return restTemplate.getForObject(url + queryParamsString, PromiseMessage.class, prepareMessage);
    }

    public AcceptedMessage sendAcceptMessage(AcceptMessage acceptMessage, NodeAddress nodeAddress) {
        LOG.info("Sending ACCEPT message to: " + nodeAddress.getHost() + ":" + nodeAddress.getPort());
        String url = prepareUrl(nodeAddress, ACCEPT_PATH);
        String queryParamsString = buildQueryParamsString(acceptMessage);
        return restTemplate.getForObject(url + queryParamsString, AcceptedMessage.class, acceptMessage);
    }

    public SuccessResponseMessage sendSuccessMessage(SuccessMessage successMessage, NodeAddress nodeAddress) {
        LOG.info("Sending SUCCESS message to: " + nodeAddress.getHost() + ":" + nodeAddress.getPort());
        String url = prepareUrl(nodeAddress, SUCCESS_PATH);
        String queryParamsString = buildQueryParamsString(successMessage);
        return restTemplate.getForObject(url + queryParamsString, SuccessResponseMessage.class, successMessage);
    }

    public void sendHeartbeatMessage(HeartbeatMessage heartbeatMessage, NodeAddress nodeAddress) {
        LOG.info("Sending HEARTBEAT message to: " + nodeAddress.getHost() + ":" + nodeAddress.getPort()
                + ", with id: " + heartbeatMessage.getId());
        String url = prepareUrl(nodeAddress, HEARTBEAT_PATH);
        String queryParamsString = buildQueryParamsString(heartbeatMessage);
        try {
            restTemplate.getForObject(url + queryParamsString, Object.class);
        } catch (Exception ex) {
            LOG.info("Failed to connect to: " + url);
        }
    }

    private String prepareUrl(NodeAddress nodeAddress, String path) {
        return "http://" + nodeAddress.getHost() + ":" + nodeAddress.getPort() + path;
    }

    private String buildQueryParamsString(HeartbeatMessage heartbeatMessage) {
        return "?id=" + heartbeatMessage.getId();
    }

    private String buildQueryParamsString(PrepareMessage prepareMessage) {
        int proposalNumber = prepareMessage.getProposalNumber();
        int index = prepareMessage.getIndex();
        return "?proposalNumber=" + proposalNumber + "&index=" + index;
    }

    private String buildQueryParamsString(AcceptMessage acceptMessage) {
        int index = acceptMessage.getIndex();
        int proposalNumber = acceptMessage.getProposalNumber();
        //todo - value

        return "?proposalNumber=" + proposalNumber + "&index=" + index;
    }

    private String buildQueryParamsString(SuccessMessage successMessage) {
        int index = successMessage.getIndex();
        Command value = successMessage.getValue();

        //todo - value
        return "?index=" + index;
    }
}
