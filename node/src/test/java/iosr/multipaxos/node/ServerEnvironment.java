package iosr.multipaxos.node;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class ServerEnvironment {
    private static List<SpringApplicationWrapper> instances = new ArrayList<>();
    private static final int NODE_1_PORT = 8081;
    private static final int NODE_2_PORT = 8082;
    private static final int NODE_3_PORT = 8083;

    static {
        List<String> defaultArgs = Lists
                .newArrayList(
                        "--endpoints.shutdown.sensitive=false",
                        "--endpoints.shutdown.enabled=true",
                        "--management.context-path=/manage",
                        "--logging.level.org.springframework=WARN");
        instances.add(createNodeInstance(Lists.newArrayList(defaultArgs), 3, NODE_3_PORT, NODE_1_PORT, NODE_2_PORT));
        instances.add(createNodeInstance(Lists.newArrayList(defaultArgs), 2, NODE_2_PORT, NODE_1_PORT, NODE_3_PORT));
        instances.add(createNodeInstance(Lists.newArrayList(defaultArgs), 1, NODE_1_PORT, NODE_2_PORT, NODE_3_PORT));
    }

    public static void start() {
        instances.forEach(SpringApplicationWrapper::start);
    }

    public static void shutdown() {
        instances.forEach(SpringApplicationWrapper::shutdown);
    }

    private static SpringApplicationWrapper createNodeInstance(List<String> defaultArgs, int id, int nodePort, int firstMemberPort, int secondMemberPort) {
        String jarFilePath = PathUtil
                .getJarfile(PathUtil.getProjectRoot() + "/multi-paxos/node/target");
        defaultArgs.add("--server.port=" + nodePort);
        defaultArgs.add("--id=" + id);
        defaultArgs.add("--members=localhost:" + firstMemberPort + ",localhost:" + secondMemberPort);
        return new SpringApplicationWrapper("http://localhost:808" + id + "/store/all", jarFilePath, defaultArgs);
    }

}
