package br.com.itau.creditrecoverybridge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("spring.redis")
@Data
public class RedisProperties {
    private String host;
    private Integer port;
    private Long timeToLive;
}
