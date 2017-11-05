package iosr.multipaxos.node.paxos;

import iosr.multipaxos.common.command.Command;
import iosr.multipaxos.node.communication.NodeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Leszek Placzkiewicz on 04.11.17.
 */
//todo - persist
@Service
public class MultiPaxosInfoManager {

    private final Logger LOG = LoggerFactory.getLogger(MultiPaxosInfoManager.class);

    @Autowired
    private NodeConfig nodeConfig;

    private final AtomicBoolean isLeader = new AtomicBoolean(false);

    private AtomicInteger leaderId = new AtomicInteger();


    private final AtomicInteger lastLogIndex = new AtomicInteger(0);

    private final AtomicInteger minProposal = new AtomicInteger(0);

    private final AtomicInteger firstUnchosenIndex = new AtomicInteger();

    private final AtomicInteger maxRound = new AtomicInteger(0);


    private final AtomicInteger nextIndex = new AtomicInteger(0);

    private final AtomicBoolean prepared = new AtomicBoolean(false);

    private final Map<Integer, Integer> acceptedProposals = new ConcurrentHashMap<>();

    private final Map<Integer, Command> acceptedValues = new ConcurrentHashMap<>();


    public int getAcceptedProposal(int index) {
        return acceptedProposals.get(index);
    }

    public void setAcceptedProposal(int index, int n) {
        acceptedProposals.put(index, n);
    }

    public Command getAcceptedValue(int index) {
        return acceptedValues.get(index);
    }

    public void setAcceptedValue(int index, Command value) {
        acceptedValues.put(index, value);
    }

    public int getLastLogIndex() {
        return lastLogIndex.get();
    }

    public void setLastLogIndex(int newLastLongIndex) {
        lastLogIndex.set(newLastLongIndex);
    }

    public int getMinProposal() {
        return minProposal.get();
    }

    public void setMinProposal(int newMinProposal) {
        minProposal.set(newMinProposal);
    }

    public int getFirstUnchosenIndex() {
        return firstUnchosenIndex.get();
    }

    public void setFirstUnchosenIndex(int newFirstUnchosenIndex) {
        firstUnchosenIndex.set(newFirstUnchosenIndex);
    }

    public int getNextIndex() {
        return nextIndex.get();
    }

    public void setNextIndex(int newNextIndex) {
        nextIndex.set(newNextIndex);
    }

    public boolean getPrepared() {
        return prepared.get();
    }

    public void setPrepared(boolean newPrepared) {
        prepared.set(newPrepared);
    }

    public int getMaxRound() {
        return maxRound.get();
    }

    public void setMaxRound(int newMaxRound) {
        maxRound.set(newMaxRound);
    }

    public int incrementAndGetMaxRound() {
        return maxRound.incrementAndGet();
    }

    public boolean isLeader() {
        return isLeader.get();
    }

    public int getLeaderId() {
        return leaderId.get();
    }

    public void updateLeaderInfo(Set<Integer> nodeIds) {
        if (nodeIds.isEmpty()) {
            LOG.info("Empty heartbeat messages set.");
            return;
        }
        int maxReceivedId = Collections.max(nodeIds);

        int id = nodeConfig.getId();
        isLeader.set(maxReceivedId > id);
        if (maxReceivedId > id) {
            isLeader.set(false);
            leaderId.set(maxReceivedId);
        } else {
            isLeader.set(true);
            leaderId.set(id);
        }
    }
}
