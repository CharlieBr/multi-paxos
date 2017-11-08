package iosr.multipaxos.node.paxos;

import iosr.multipaxos.node.communication.Messenger;
import iosr.multipaxos.node.communication.NodeAddress;
import iosr.multipaxos.node.communication.NodeConfig;
import iosr.multipaxos.node.paxos.message.HeartbeatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Leszek Placzkiewicz on 05.11.17.
 */
@Component
public class HeartbeatManager {

    private final Logger LOG = LoggerFactory.getLogger(HeartbeatManager.class);

    private AtomicReference<Set<Integer>> receivedIds = new AtomicReference<>(new HashSet<>());

    @Autowired
    private MultiPaxosInfoManager multiPaxosInfoManager;

    @Autowired
    private NodeConfig nodeConfig;

    @Autowired
    private Messenger messenger;


    public void receive(HeartbeatMessage heartbeatMessage) {
        receivedIds.get().add(heartbeatMessage.getId());
    }

    @Scheduled(fixedRateString = "${heartbeat.interval}")
    public void sendHeartbeat() {
        List<NodeAddress> members = nodeConfig.getMembers();
        members.forEach(this::sendHeartbeatMessageAsync);

    }

    @Scheduled(fixedRateString = "#{2 * ${heartbeat.interval}}")
    public void selectLeader() {
        LOG.info("Selecting leader...");

        Set<Integer> nodeIds = receivedIds.getAndSet(new HashSet<>());
        multiPaxosInfoManager.updateLeaderInfo(nodeIds);
        LOG.info("Leader id: " + multiPaxosInfoManager.getLeaderId());
    }

    private void sendHeartbeatMessageAsync(NodeAddress nodeAddress) {
        CompletableFuture.supplyAsync(() -> {
            messenger.sendHeartbeatMessage(new HeartbeatMessage(nodeConfig.getId()), nodeAddress);
            return null;
        }).exceptionally(t -> {
            LOG.error("Failed to send HEARTBEAT message to " + nodeAddress.getHost() + ":" + nodeAddress.getPort());
            return null;
        });
    }
}
