package com.github.jpidem.spring4.autoconfigure;

import com.github.jpidem.spring4.support.RetryConditional;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@Slf4j
public class RedissionConfiguration {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;

    @Bean(name = "redissonClient")
    @RetryConditional(missingBeanType = RedissonClient.class)
    public RedissonClient init() {
        log.debug("redis_ip:" + host);
        log.debug("redis_port:" + port);
        log.debug("redis_password:" + password);
        Config config = new Config();
        String url = "redis://" + host + ":" + port;
        log.debug(url);
        SingleServerConfig singleServerConfig = config.useSingleServer().setAddress(url);
        if (!StringUtils.isEmpty(password)) {
            singleServerConfig.setPassword(password);
        }
        RedissonClient redissonClient = Redisson.create(config);
        log.debug("初始化RedissonClient");
        return redissonClient;
    }
}
