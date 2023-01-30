package br.com.itau.creditrecoverybridge.keygeneration.service;

import br.com.itau.creditrecoverybridge.exceptions.TransactionNotFoundException;
import br.com.itau.creditrecoverybridge.modules.keygeneration.model.KeyPairModel;
import br.com.itau.creditrecoverybridge.modules.keygeneration.repository.KeyPairRepository;
import br.com.itau.creditrecoverybridge.modules.keygeneration.service.KeyPairService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class KeyPairServiceImplTest {

    public static final String UUID_FORMAT_INVALID = "01234567-9ABC-DEF0-1234-56789ABCDEF0-555-999";
    public static final String UUID_NOT_FOUND = "6a1051b0-df63-482d-87c6-5c77fff7e3c3";
    private byte[] serialization;
    private KeyPair rsaKeyPair;
    private UUID id;
    private KeyPairModel keyPairModel;

    @Autowired
    private KeyPairRepository keyPairRepository;

    @Autowired
    private KeyPairService keyPairService;

    @Before
    public void before() throws IOException, NoSuchAlgorithmException {
        id = UUID.randomUUID();

        KeyPairGenerator rsaGen = KeyPairGenerator.getInstance("RSA");
        rsaGen.initialize(2048);
        rsaKeyPair = rsaGen.generateKeyPair();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(rsaKeyPair.getPrivate());
        serialization = byteArrayOutputStream.toByteArray();

        objectOutputStream.close();
        byteArrayOutputStream.close();

        keyPairRepository.save(new KeyPairModel(id.toString(), serialization, 120L));
    }

    @Test
    public void testSavingInRedis() {
        KeyPairModel save = keyPairRepository.save(new KeyPairModel(id.toString(), serialization, 120L));
        assertNotNull(save);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByUuidFormatInvalid() {
        keyPairService.findByUuid(UUID_FORMAT_INVALID);
    }

    @Test
    public void testFindByUuidNotNull() {
        assertNotNull(keyPairService.findByUuid(id.toString()));
    }

    @Test(expected = TransactionNotFoundException.class)
    public void testFindByUuidTransactionNotFoundException() {
        keyPairService.findByUuid(UUID_NOT_FOUND);
    }

    @Test
    public void testFindByUuidEqualsAndDesserialize() throws IOException, ClassNotFoundException {
        assertEquals(this.rsaKeyPair.getPrivate(), keyPairService.desserializePrivateKey(keyPairService.findByUuid(id.toString())));
    }

}