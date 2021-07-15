package com.oliverlockwood.example;

import com.ecwid.consul.v1.ConsulClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.ConsulAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(value = { ConsulClient.class })
@AutoConfigureAfter(ConsulAutoConfiguration.class)
@ConditionalOnConsulEnabled
@Slf4j
public class ExampleAutoConfiguration {

    @Autowired
    PropertiesConfiguration pc;

    @Bean
    String configInfoString() {
        return String.format("Values are:\n  %s\n  %s\n  %s\n  %s\n  %s\n  %s\n  %s\n  %s",
                pc.alpha, pc.bravo, pc.charlie, pc.delta, pc.echo, pc.foxtrot, pc.gamma, pc.hotel);
    }

}
