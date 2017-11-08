package iosr.multipaxos.client.model;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class TargetAddresses {
    private final Map<String, String> targetAddresses;

    public TargetAddresses(final Map<String, String> targetAddresses) {
        this.targetAddresses = targetAddresses;
    }

    public String getTargetAddressById(final String id) {
        return this.targetAddresses.get(id);
    }

    public Optional<String> getNextTargetAddress(final String id) {
        return targetAddresses.keySet().stream().filter(k -> !Objects.equals(k, id)).findAny();
    }
}
