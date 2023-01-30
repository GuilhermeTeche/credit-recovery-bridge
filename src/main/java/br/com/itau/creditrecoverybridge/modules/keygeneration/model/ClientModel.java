package br.com.itau.creditrecoverybridge.modules.keygeneration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientModel {
    private String uuid;
    private String publicKey;
}
