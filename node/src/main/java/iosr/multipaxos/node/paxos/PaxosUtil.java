package iosr.multipaxos.node.paxos;

import iosr.multipaxos.node.communication.NodeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
@Component
public class PaxosUtil {

    @Autowired
    private MultiPaxosInfoManager multiPaxosInfoManager;

    @Autowired
    private NodeConfig nodeConfig;

    public int generateProposalNumber() {
        int maxRound = multiPaxosInfoManager.incrementAndGetMaxRound();
        int serverId = nodeConfig.getId();

        return Integer.valueOf("" + maxRound + serverId);
    }

}