package br.com.itau.creditrecoverybridge.test;

import com.github.fppt.jedismock.RedisServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.io.IOException;

@Configuration
public class RedisTestConfiguration {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisConfiguration configuration) {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(configuration);
        return connectionFactory;
    }

    @Bean
    public RedisConfiguration redisConfiguration(RedisServer redisServer) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisServer.getHost());
        configuration.setPort(redisServer.getBindPort());
        return configuration;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RedisServer redisServer() throws IOException {
        return new RedisServer();
    }
}
