package br.com.itau.creditrecoverybridge.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("serasa")
public class BureausCreditAdapterSerasaConfiguration {
    private String url;
}
