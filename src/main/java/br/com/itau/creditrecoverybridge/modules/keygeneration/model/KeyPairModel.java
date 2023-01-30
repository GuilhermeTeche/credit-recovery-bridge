package br.com.itau.creditrecoverybridge.modules.keygeneration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@AllArgsConstructor
@RedisHash(value = "mykey")
@Getter
public class KeyPairModel implements Serializable {

    @Id
    private String id;
    private byte[] privateKey;

    @TimeToLive
    private Long timeToLive;

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }
}
