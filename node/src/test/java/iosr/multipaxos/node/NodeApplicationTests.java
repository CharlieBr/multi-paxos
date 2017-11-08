package iosr.multipaxos.node;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"id=1", "server.port=8080", "members=localhost:8081"})
public class NodeApplicationTests {

	@Test
	public void contextLoads() {
	}

}
