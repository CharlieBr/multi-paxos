package iosr.multipaxos.node.communication;

/**
 * Created by Leszek Placzkiewicz on 03.11.17.
 */
public class NodeAddress {

    private final String host;
    private final int port;

    public NodeAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
