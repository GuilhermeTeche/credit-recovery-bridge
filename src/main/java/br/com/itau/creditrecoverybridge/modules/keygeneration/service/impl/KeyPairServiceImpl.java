package br.com.itau.creditrecoverybridge.modules.keygeneration.service.impl;

import br.com.itau.creditrecoverybridge.config.RedisProperties;
import br.com.itau.creditrecoverybridge.exceptions.TransactionNotFoundException;
import br.com.itau.creditrecoverybridge.modules.keygeneration.model.ClientModel;
import br.com.itau.creditrecoverybridge.modules.keygeneration.model.KeyPairModel;
import br.com.itau.creditrecoverybridge.modules.keygeneration.repository.KeyPairRepository;
import br.com.itau.creditrecoverybridge.modules.keygeneration.service.KeyPairService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static br.com.itau.creditrecoverybridge.util.Constants.MESSAGE_REGEX_UUID;
import static br.com.itau.creditrecoverybridge.util.Constants.REGEX_UUID;

/**
 * Class responsible for generate public key based in public key of server ans return public key
 */
@Slf4j
@Service
public class KeyPairServiceImpl implements KeyPairService {

    @Autowired
    private KeyPairRepository keyPairRepository;

    @Autowired
    private RedisProperties redisProperties;

    /**
     * Public method where it generates the key pair, stores the private key and returns the public key to the controller
     *
     * @return String
     */
    @Override
    public ClientModel getPublicKey() throws IOException, NoSuchAlgorithmException {
        String uuid = UUID.randomUUID().toString();
        KeyPair keyPair = generateAndInitializeKeyPair(uuid);
        return new ClientModel(
                uuid,
                Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded())
        );
    }

    /**
     * @param uuid
     * @return KeyPairModel
     */
    @Override
    public KeyPairModel findByUuid(String uuid) {
        Assert.isTrue(uuid.matches(REGEX_UUID), MESSAGE_REGEX_UUID);
        Optional<KeyPairModel> keyPairModelOptional = keyPairRepository.findById(uuid);

        if (!keyPairModelOptional.isPresent()) {
            throw new TransactionNotFoundException();
        }
        return keyPairModelOptional.get();
    }

    /**
     * Private method responsible to generate Pair of Keys
     *
     * @param uuid
     * @return KeyPair
     * @throws NoSuchAlgorithmException
     */
    private KeyPair generateAndInitializeKeyPair(String uuid) throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator rsaGen = KeyPairGenerator.getInstance("RSA");
        rsaGen.initialize(2048);
        KeyPair rsaKeyPair = rsaGen.generateKeyPair();
        storesPrivateKey(rsaKeyPair, uuid);

        return rsaKeyPair;
    }

    /**
     * Private method responsible to retrieve the public key and UUID
     *
     * @param rsaKeyPair
     * @param uuid
     */
    private void storesPrivateKey(KeyPair rsaKeyPair, String uuid) throws IOException {
        keyPairRepository.save(
                new KeyPairModel(
                        uuid,
                        serializePrivateKey(rsaKeyPair),
                        redisProperties.getTimeToLive()
                )
        );
    }

    /**
     * Private method responsible to serilize the private key in redis
     *
     * @param rsaKeyPair
     * @return
     */
    private byte[] serializePrivateKey(KeyPair rsaKeyPair) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(rsaKeyPair.getPrivate());
        byte[] result = byteArrayOutputStream.toByteArray();

        objectOutputStream.close();
        byteArrayOutputStream.close();

        return result;
    }

    /**
     * Public method responsible to desserilize the private key in redis
     *
     * @param keyPairModel
     * @return ObjectInputStream
     */
    @Override
    public Object desserializePrivateKey(KeyPairModel keyPairModel) throws IOException, ClassNotFoundException {
        log.info("10.3- testing 3...");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(keyPairModel.getPrivateKey());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = objectInputStream.readObject();

        objectInputStream.close();
        byteArrayInputStream.close();

        log.info("10.4- testing 4...");
        return object;
    }
}
