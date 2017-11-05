package iosr.multipaxos.node.paxos;

import iosr.multipaxos.common.command.Command;
import iosr.multipaxos.node.communication.Messenger;
import iosr.multipaxos.node.communication.NodeAddress;
import iosr.multipaxos.node.communication.NodeConfig;
import iosr.multipaxos.node.paxos.message.*;
import iosr.multipaxos.node.store.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
@Component
public class MultiPaxosHandler {

    @Autowired
    private KeyValueStore keyValueStore;

    @Autowired
    private Messenger messenger;

    @Autowired
    private PaxosUtil paxosUtil;

    @Autowired
    private NodeConfig nodeConfig;

    @Autowired
    private MultiPaxosInfoManager multiPaxosInfoManager;

    public Object executeMultiPaxos(Command command) {
        boolean shouldRepeatPaxos = false;
        do {

            int index;
            int proposalNumber = paxosUtil.generateProposalNumber();
            Set<AcceptedInfo> acceptedInfoSet = null;

            if (!multiPaxosInfoManager.getPrepared()) {
                index = multiPaxosInfoManager.getFirstUnchosenIndex();
                multiPaxosInfoManager.setNextIndex(index + 1);
                acceptedInfoSet = executePreparePhase(proposalNumber, index);
            } else {
                index = multiPaxosInfoManager.getNextIndex();
                multiPaxosInfoManager.setNextIndex(index + 1);
            }
            shouldRepeatPaxos = executeAcceptPhase(proposalNumber, command, acceptedInfoSet, index);
        } while (shouldRepeatPaxos);

        throw new UnsupportedOperationException("Not implemented yet");
    }

    //todo - should send to all acceptors not only to quorum
    private Set<AcceptedInfo> executePreparePhase(int proposalNumber, int index) {
        Set<AcceptedInfo> acceptedInfoSet = new HashSet<>();
        int responseCount = 0;
        List<NodeAddress> members = nodeConfig.getMembers();
        int clusterSize = nodeConfig.getClusterSize();

        for (int i = 0; !(responseCount > clusterSize/2) && i < members.size(); i++) {
            try {
                NodeAddress nodeAddress = members.get(i);
                PrepareMessage prepareMessage = new PrepareMessage(proposalNumber, index);
                PromiseMessage promiseMessage = messenger.sendPrepareMessage(prepareMessage, nodeAddress);

                int acceptedProposal = promiseMessage.getAcceptedProposal();
                Command acceptedValue = promiseMessage.getAcceptedValue();
                boolean noMoreAccepted = promiseMessage.isNoMoreAccepted();

                acceptedInfoSet.add(new AcceptedInfo(acceptedProposal, acceptedValue, noMoreAccepted));
                responseCount++;
            } catch (Exception ex) {
                // node failed to send promise message in response
            }
        }

        long noMoreAcceptedCount = acceptedInfoSet.stream().filter(AcceptedInfo::isNoMoreAccepted).count();
        if (noMoreAcceptedCount == responseCount) {
            multiPaxosInfoManager.setPrepared(true);
        }
        return acceptedInfoSet;
    }

    //todo - should send to all acceptors not only to quorum
    private boolean executeAcceptPhase(int proposalNumber, Command value, Set<AcceptedInfo> acceptedInfoSet, int index) {
        boolean shouldRepeatPaxos = false;
        int responseCount = 0;
        List<NodeAddress> members = nodeConfig.getMembers();

        AcceptedInfo highestNumberedAI = acceptedInfoSet.stream()
                .sorted(Comparator.comparingInt(AcceptedInfo::getAcceptedProposal).reversed())
                .findFirst()
                .get();

        Command valueToSend = highestNumberedAI.getAcceptedProposal() != 0 ? highestNumberedAI.getAcceptedValue() : value;

        for(int i=0; !(responseCount > nodeConfig.getClusterSize()/2) && i<members.size(); i++) {
            try {
                NodeAddress nodeAddress = members.get(i);
                AcceptMessage acceptMessage = new AcceptMessage(proposalNumber, valueToSend, index);
                AcceptedMessage acceptedMessage = messenger.sendAcceptMessage(acceptMessage, nodeAddress);

                int n = acceptedMessage.getMinProposal();
                if (n > proposalNumber) {
                    multiPaxosInfoManager.setMaxRound(n);
                    multiPaxosInfoManager.setPrepared(false);
                    shouldRepeatPaxos = true;
                    break;
                }

                int firstUnchosenIndex = acceptedMessage.getFirstUnchosenIndex();
                int lastLogIndex = multiPaxosInfoManager.getLastLogIndex();
                int acceptedProposal = multiPaxosInfoManager.getAcceptedProposal(firstUnchosenIndex);
                Command acceptedValue = multiPaxosInfoManager.getAcceptedValue(firstUnchosenIndex);
                if (firstUnchosenIndex <= lastLogIndex && acceptedProposal == Integer.MAX_VALUE) {
                    executeSuccessPhase(firstUnchosenIndex, acceptedValue);
                }
            } catch (Exception ex) {
                // node failed to send accepted message in response
            }

        }

        return shouldRepeatPaxos;
    }

    private void executeSuccessPhase(int firstUnchosenIndex, Command acceptedValue) {
        int responseCount = 0;
        List<NodeAddress> members = nodeConfig.getMembers();
        int clusterSize = nodeConfig.getClusterSize();

        for (int i = 0; !(responseCount > clusterSize / 2) && i < members.size(); i++) {
            NodeAddress nodeAddress = members.get(i);
            SuccessMessage successMessage = new SuccessMessage(firstUnchosenIndex, acceptedValue);
            SuccessResponseMessage successResponseMessage = messenger.sendSuccessMessage(successMessage, nodeAddress);
        }

        throw new UnsupportedOperationException("Not implemented yet");
    }

}
