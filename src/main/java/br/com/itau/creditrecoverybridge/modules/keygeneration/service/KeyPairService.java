package br.com.itau.creditrecoverybridge.modules.keygeneration.service;

import br.com.itau.creditrecoverybridge.modules.keygeneration.model.ClientModel;
import br.com.itau.creditrecoverybridge.modules.keygeneration.model.KeyPairModel;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface KeyPairService {
    ClientModel getPublicKey() throws IOException, NoSuchAlgorithmException;
    KeyPairModel findByUuid(String uuid);
    Object desserializePrivateKey(KeyPairModel keyPairModel) throws IOException, ClassNotFoundException;
}
