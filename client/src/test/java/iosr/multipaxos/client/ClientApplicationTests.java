package iosr.multipaxos.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"server.port=5555", "ids=0_localhost:8080,1_localhost:8081"})
public class ClientApplicationTests {

	@Test
	public void contextLoads() {
	}

}
