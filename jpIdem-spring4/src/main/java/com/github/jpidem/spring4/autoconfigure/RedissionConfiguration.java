package com.github.jpidem.spring4.autoconfigure;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

@Getter
@Slf4j
public class RedissionConfiguration implements EnvironmentAware {
    private Environment environment;

    private String host;

    private String port;

    private String password;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        if (environment.containsProperty("spring.redis.host")) {
            this.host = environment.getProperty("spring.redis.host");
        }
        if (environment.containsProperty("spring.redis.port")) {
            this.port = environment.getProperty("spring.redis.port");
        }
        if (environment.containsProperty("spring.redis.password")) {
            this.password = environment.getProperty("spring.redis.password");
        }
        log.debug("redis_ip:" + host);
        log.debug("redis_port:" + port);
        log.debug("redis_password:" + password);
    }
}
