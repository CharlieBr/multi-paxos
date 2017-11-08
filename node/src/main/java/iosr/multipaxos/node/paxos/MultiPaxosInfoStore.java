package iosr.multipaxos.node.paxos;

import iosr.multipaxos.common.command.Command;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Leszek Placzkiewicz on 07.11.17.
 */
//TODO - persist and load
@Component
public class MultiPaxosInfoStore {

    public int loadLastLogIndex() {
        return 0;
    }

    public int loadMinProposal() {
        return 0;
    }


    public int loadMaxRound() {
        return 0;
    }

    public Map<Integer, Integer> loadAcceptedProposals() {
        return Collections.emptyMap();
    }


    public Map<Integer, Command> loadAcceptedValues() {
        return Collections.emptyMap();
    }


    public void saveAcceptedProposal(int index, int n) {

    }

    public void saveAcceptedValue(int index, Command value) {

    }

    public void saveLastLogIndex(int newLastLongIndex) {

    }

    public void saveMinProposal(int newMinProposal) {

    }

    public void saveMaxRound(int newMaxRound) {

    }
}
