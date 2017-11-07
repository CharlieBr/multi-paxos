package iosr.multipaxos.client;

import static java.util.Collections.emptyMap;

import iosr.multipaxos.common.command.PutCommand;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;

@RestController("/client")
public class ClientController {

    private final String serverUrl = "http://[::1]:8080/store";

    @RequestMapping(method = RequestMethod.PUT)
    public Object put(@RequestParam("key") final String key, @RequestParam("value") final Integer value) {
        if(Strings.isNullOrEmpty(key) || value == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        HttpEntity<PutCommand> entity = new HttpEntity<>(new PutCommand(key, value));
        ResponseEntity response =
                new RestTemplate().exchange(serverUrl, HttpMethod.PUT, entity, Object.class, emptyMap());
        if(HttpStatus.TEMPORARY_REDIRECT.equals(response.getStatusCode())) {
            Map<String, Integer> body = (Map<String,Integer>) response.getBody();
            System.out.println(body.get("leaderId"));
        }
        return response;
    }
}
