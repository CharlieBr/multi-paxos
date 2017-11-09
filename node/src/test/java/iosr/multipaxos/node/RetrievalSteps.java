package iosr.multipaxos.node;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import iosr.multipaxos.common.command.Command;
import iosr.multipaxos.common.command.CommandResponse;
import iosr.multipaxos.common.command.GetCommand;
import iosr.multipaxos.common.command.PutCommand;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class RetrievalSteps {
    private RestTemplate client = new RestTemplate();
    private ResponseEntity response = null;

    @When("^client sends key (.*?) with value (.*?) to node (\\d+)")
    public void putValue(String key, String value, int nodeId) throws InterruptedException {
        String url = "http://localhost:808" + nodeId + "/store";
        HttpEntity<Command> entity = new HttpEntity<>(new PutCommand(1, key, value));
        response = client.exchange(url, HttpMethod.PUT, entity, CommandResponse.class, Collections.emptyMap());
        if (response.getStatusCode().is3xxRedirection()) {
            handleRedirect(response, entity);
        }
    }

    private void handleRedirect(ResponseEntity<CommandResponse> response, HttpEntity<Command> entity) {
        Map<Object, Object> responseBody = (Map<Object, Object>)response.getBody();
        String url = "http://localhost:808" + responseBody.get("leaderId") + "/store";
        this.response = client.exchange(url, HttpMethod.PUT, entity, CommandResponse.class, Collections.emptyMap());
    }

    @Then("^node (\\d+) has saved key (.*?) with value (.*?)$")
    public void checkValueOnNode(int nodeId, String key, String value) {
        assertCorrectResponse("PUT was not successful");
        String url = "http://localhost:808" + nodeId + "/store/all";
        ResponseEntity<Map> store = client.getForEntity(url, Map.class);
        assertCorrectResponse("GET was not successful");
        assertThat(store.getBody().containsKey(key));
        assertThat(store.getBody().get(key)).isEqualTo(value);
    }

    private void assertCorrectResponse(String errorMessage) {
        if (response.getStatusCode() == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException(errorMessage + " " + response.getStatusCode());
        }
        assertThat(response).as("response is null").isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).as("Status code is not 200").isTrue();
    }

}
