package iosr.multipaxos.client.config;

import iosr.multipaxos.client.model.TargetAddresses;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Splitter;

@Configuration
public class ClientConfiguration {

    @Bean
    public TargetAddresses targetAddresses(@Value("${ids}") String ids) {
        ids = ids.replaceAll("localhost", "http://[::1]");
        final Map<String, String> targetIds = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .withKeyValueSeparator('_')
                .split(ids);
        return new TargetAddresses(targetIds);
    }
}
