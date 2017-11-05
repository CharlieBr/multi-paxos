package iosr.multipaxos.node.communication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class NodeConfig {

    private final List<NodeAddress> members;

    private final int id;

    public NodeConfig(List<NodeAddress> members, int serverId) {
        this.members = members;
        this.id = serverId;
    }

    public List<NodeAddress> getMembers() {
        return members;
    }

    public int getId() {
        return id;
    }

    public int getClusterSize() {
        return members.size() + 1;
    }

    @Configuration
    public static class Config {

        @Bean
        public NodeConfig nodeConfig(@Value("${members:}") String membersHostPort,
                                     @Value("${id}")String serverId) {

            List<NodeAddress> membersNodeAddress = new ArrayList<>();
            String[] membersHostPortInfo = membersHostPort.split(",");
            for (String memberHostPort : membersHostPortInfo) {
                String[] memberInfo = memberHostPort.split(":");
                membersNodeAddress.add(new NodeAddress(memberInfo[0], Integer.valueOf(memberInfo[1])));
            }

            return new NodeConfig(membersNodeAddress, Integer.valueOf(serverId));
        }
    }

}
