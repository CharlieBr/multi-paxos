package iosr.multipaxos.client.model;

import java.util.Map;


public class TargetAddresses {
    private final Map<String, String> targetAddresses;

    public TargetAddresses(final Map<String, String> targetAddresses) {
        this.targetAddresses = targetAddresses;
    }

    public String getTargetAddressById(final String id) {
        return this.targetAddresses.get(id);
    }
}
