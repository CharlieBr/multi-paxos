package iosr.multipaxos.node.paxos;

import iosr.multipaxos.common.command.Command;
import iosr.multipaxos.common.command.GetCommand;
import iosr.multipaxos.common.command.PutCommand;
import iosr.multipaxos.common.command.RemoveCommand;
import iosr.multipaxos.node.communication.Messenger;
import iosr.multipaxos.node.communication.NodeAddress;
import iosr.multipaxos.node.communication.NodeConfig;
import iosr.multipaxos.node.paxos.message.AcceptMessage;
import iosr.multipaxos.node.paxos.message.PrepareMessage;
import iosr.multipaxos.node.paxos.message.SuccessMessage;
import iosr.multipaxos.node.store.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
@Component
public class MultiPaxosHandler {

    private final Logger LOG = LoggerFactory.getLogger(MultiPaxosHandler.class);

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

    public Object executePutCommand(PutCommand putCommand) {
        boolean result = executeMultiPaxos(putCommand);
        return result ? keyValueStore.put(putCommand) : null;
    }

    public Object executeRemoveCommand(RemoveCommand removeCommand) {
        boolean result = executeMultiPaxos(removeCommand);
        return result ? keyValueStore.remove(removeCommand) : null;
    }

    public Object executeGetCommand(GetCommand getCommand) {
        boolean result = executeMultiPaxos(getCommand);
        return result ? keyValueStore.get(getCommand) : null;
    }

    private boolean executeMultiPaxos(Command command) {
        AtomicBoolean shouldRepeatPaxos = new AtomicBoolean(false);
        AtomicReference<Command> valueToSend = new AtomicReference<>(command);
        do {

            shouldRepeatPaxos.set(false);
            int index;
            int proposalNumber = paxosUtil.generateProposalNumber();

            if (!multiPaxosInfoManager.isPrepared()) {
                index = multiPaxosInfoManager.getFirstUnchosenIndex();
                multiPaxosInfoManager.setNextIndex(index + 1);
                executePreparePhase(proposalNumber, index, valueToSend);
            } else {
                index = multiPaxosInfoManager.getNextIndex();
                multiPaxosInfoManager.setNextIndex(index + 1);
            }
            executeAcceptPhase(proposalNumber, index, valueToSend, shouldRepeatPaxos);
        } while (shouldRepeatPaxos.get());

        return valueToSend.get() == command;
    }

    private void executePreparePhase(int proposalNumber, int index, AtomicReference<Command> valueToSend) {
        Set<AcceptedInfo> acceptedInfoSet = ConcurrentHashMap.newKeySet();
        List<NodeAddress> members = nodeConfig.getMembers();
        int clusterSize = nodeConfig.getClusterSize();
        CountDownLatch countDownLatch = new CountDownLatch(clusterSize/2);
        members.forEach(member -> sendPrepareMessageAsync(member, proposalNumber, index, acceptedInfoSet, countDownLatch));

        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        Optional<AcceptedInfo> highestNumberedAIOptional = acceptedInfoSet.stream()
                .sorted(Comparator.comparingInt(AcceptedInfo::getAcceptedProposal).reversed())
                .findFirst();


        if (highestNumberedAIOptional.isPresent()) {
            AcceptedInfo highestNumberedAI = highestNumberedAIOptional.get();
            if (highestNumberedAI.getAcceptedProposal() != 0) {
                //TODO - should it replace proposal number with received accepted proposal number?
                valueToSend.set(highestNumberedAI.getAcceptedValue());
            }
        }

        long noMoreAcceptedCount = acceptedInfoSet.stream().filter(AcceptedInfo::isNoMoreAccepted).count();
        if (noMoreAcceptedCount == acceptedInfoSet.size()) {
            multiPaxosInfoManager.setPrepared(true);
        }
    }

    private void executeAcceptPhase(int proposalNumber, int index, AtomicReference<Command> valueToSend, AtomicBoolean shouldRepeatPaxos) {
        List<NodeAddress> members = nodeConfig.getMembers();
        int clusterSize = nodeConfig.getClusterSize();
        CountDownLatch countDownLatch = new CountDownLatch(clusterSize/2);
        CountDownLatch cdl = new CountDownLatch(1);
        members.forEach(member -> sendAcceptMessageAsync(member, proposalNumber, index, valueToSend, countDownLatch, shouldRepeatPaxos, cdl));

        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        if (shouldRepeatPaxos.get()) {
            return;
        }

        multiPaxosInfoManager.setAcceptedProposal(index, Integer.MAX_VALUE);
        multiPaxosInfoManager.setAcceptedValue(index, valueToSend.get());

        multiPaxosInfoManager.setLastLogIndex(index);
        cdl.countDown();
    }

    private void executeSuccessPhase(NodeAddress nodeAddress, int index, Command value) {
//        List<NodeAddress> members = nodeConfig.getMembers();
//        members.forEach(member -> sendSuccessMessageAsync(member, index, value));
        sendSuccessMessageAsync(nodeAddress, index, value);
    }

    private void sendPrepareMessageAsync(NodeAddress nodeAddress, int proposalNumber, int index,
                                         Set<AcceptedInfo> acceptedInfoSet, CountDownLatch countDownLatch) {
        CompletableFuture.supplyAsync(() -> {
            PrepareMessage prepareMessage = new PrepareMessage(proposalNumber, index);
            return messenger.sendPrepareMessage(prepareMessage, nodeAddress);
        }).thenAccept(promiseMessage -> {
            int acceptedProposal = promiseMessage.getAcceptedProposal();
            Command acceptedValue = promiseMessage.getAcceptedValue();
            boolean noMoreAccepted = promiseMessage.isNoMoreAccepted();

            acceptedInfoSet.add(new AcceptedInfo(acceptedProposal, acceptedValue, noMoreAccepted));
            countDownLatch.countDown();
        }).exceptionally(t -> {
            LOG.error("Failed to exchange prepare message with " + nodeAddress.getHost() + ":" + nodeAddress.getPort());
            return null;
        });
    }

    private void sendAcceptMessageAsync(NodeAddress nodeAddress, int proposalNumber, int index, AtomicReference<Command> valueToSend,
                                        CountDownLatch acceptPhaseCDL, AtomicBoolean shouldRepeatPaxos, CountDownLatch successPhaseCDL) {
        CompletableFuture.supplyAsync(() -> {
            int firstUnchosenIndex = multiPaxosInfoManager.getFirstUnchosenIndex();
            AcceptMessage acceptMessage = new AcceptMessage(proposalNumber, valueToSend.get(), index, firstUnchosenIndex);
            return messenger.sendAcceptMessage(acceptMessage, nodeAddress);
        }).thenAccept(acceptedMessage -> {
            int n = acceptedMessage.getMinProposal();
            if (n > proposalNumber) {
                multiPaxosInfoManager.setMaxRound(n);
                multiPaxosInfoManager.setPrepared(false);
                shouldRepeatPaxos.set(true);
                acceptPhaseCDL.countDown();
                return;
            }
            acceptPhaseCDL.countDown();

            try {
                successPhaseCDL.await();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }


            int firstUnchosenIndex = acceptedMessage.getFirstUnchosenIndex();
            int lastLogIndex = multiPaxosInfoManager.getLastLogIndex();
            int acceptedProposal = multiPaxosInfoManager.getAcceptedProposal(firstUnchosenIndex);
            Command acceptedValue = multiPaxosInfoManager.getAcceptedValue(firstUnchosenIndex);
            if (firstUnchosenIndex <= lastLogIndex && acceptedProposal == Integer.MAX_VALUE) {
                executeSuccessPhase(nodeAddress, firstUnchosenIndex, acceptedValue);
            }
        }).exceptionally(t -> {
            LOG.error("Failed to exchange accept message with " + nodeAddress.getHost() + ":" + nodeAddress.getPort());
            return null;
        });
    }

    private void sendSuccessMessageAsync(NodeAddress nodeAddress, int index, Command value) {
        CompletableFuture.supplyAsync(() -> {
            SuccessMessage successMessage = new SuccessMessage(index, value);
            return messenger.sendSuccessMessage(successMessage, nodeAddress);
        }).thenAccept(successResponseMessage -> {
            int firstUnchosenIndex = successResponseMessage.getFirstUnchosenIndex();
            if (firstUnchosenIndex < multiPaxosInfoManager.getFirstUnchosenIndex()) {
                System.out.println("Inside " + firstUnchosenIndex + ":" + multiPaxosInfoManager.getFirstUnchosenIndex());
                Command acceptedValue = multiPaxosInfoManager.getAcceptedValue(firstUnchosenIndex);
                sendSuccessMessageAsync(nodeAddress, firstUnchosenIndex, acceptedValue);
            }
        }).exceptionally(t -> {
            LOG.error("Failed to exchange success message with " + nodeAddress.getHost() + ":" + nodeAddress.getPort());
            return null;
        });
    }

}
