package iosr.multipaxos.client.controller;

import static java.util.Collections.emptyMap;

import iosr.multipaxos.client.model.TargetAddresses;
import iosr.multipaxos.common.command.Command;
import iosr.multipaxos.common.command.GetCommand;
import iosr.multipaxos.common.command.PutCommand;
import iosr.multipaxos.common.command.RemoveCommand;

import java.net.ConnectException;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;

@RestController("/client")
public class ClientController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);
    @Autowired
    private TargetAddresses targetAddresses;
    @Autowired
    private RestTemplate restTemplate;
    private String currentLeader = "0";

    @RequestMapping(method = RequestMethod.PUT)
    public Object put(@RequestParam("key") final String key, @RequestParam("value") final Integer value) {
        if(Strings.isNullOrEmpty(key) || value == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final HttpEntity<Command> entity = new HttpEntity<>(new PutCommand(key, value));
        return executeRequest(HttpMethod.PUT, entity);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Object get(@RequestParam("key") final String key) {
        if(Strings.isNullOrEmpty(key)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final HttpEntity<Command> entity = new HttpEntity<>(new GetCommand(key));
        return executeRequest(HttpMethod.GET, entity);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public Object delete(@RequestParam("key") final String key) {
        if(Strings.isNullOrEmpty(key)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final HttpEntity<Command> entity = new HttpEntity<>(new RemoveCommand(key));
        return executeRequest(HttpMethod.DELETE, entity);
    }

    private static boolean isRedirectResponse(final ResponseEntity response) {
        return HttpStatus.TEMPORARY_REDIRECT.equals(response.getStatusCode());
    }

    private ResponseEntity processRedirectResponse(final HttpMethod method,
                                                   final HttpEntity<Command> entity,
                                                   final ResponseEntity response) {
        final Map<String, Integer> responseBody = (Map<String, Integer>) response.getBody();
        this.currentLeader = responseBody.get("leaderId").toString();
        LOGGER.info("Redirecting to new leader with id {}", this.currentLeader);
        return executeRequest(method, entity);
    }

    private ResponseEntity executeRequest(final HttpMethod method,
                                          final HttpEntity<Command> entity) {
        final String targetUrl = this.targetAddresses.getTargetAddressById(this.currentLeader);
        LOGGER.info("Execute " + method.name() + " request to: " + targetUrl);
        ResponseEntity response = getResponse(targetUrl, method, entity);
        if(isRedirectResponse(response)) {
            response = processRedirectResponse(method, entity, response);
        }
        return response;
    }

    private ResponseEntity getResponse(final String targetUrl,
                                       final HttpMethod method,
                                       final HttpEntity<Command> entity) {
        try {
            return this.restTemplate.exchange(targetUrl, method, entity, Object.class, emptyMap());
        }
        catch(final RestClientException e) {
            if(e.getRootCause() instanceof ConnectTimeoutException || e.getRootCause() instanceof ConnectException) {
                return processTimeout(method, entity);
            }
        }
        return new ResponseEntity<>("Something went wrong! :(", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity processTimeout(final HttpMethod method, final HttpEntity<Command> entity) {
        this.currentLeader = this.targetAddresses.getNextTargetAddress(this.currentLeader)
                .orElseThrow(() -> new RuntimeException("No leader address to choose"));
        return executeRequest(method, entity);
    }
}
