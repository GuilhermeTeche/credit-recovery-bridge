//package br.com.itau.creditrecoverybridge.decryption.controller;
//
//import br.com.itau.creditrecoverybridge.modules.decryption.controller.SecurityLayerController;
//import br.com.itau.creditrecoverybridge.modules.decryption.model.EncryptCommercialModel;
//import br.com.itau.creditrecoverybridge.modules.decryption.model.QueuesActiveMQ;
//import br.com.itau.creditrecoverybridge.modules.decryption.service.DecryptorService;
//import br.com.itau.creditrecoverybridge.modules.keygeneration.model.ClientModel;
//import br.com.itau.creditrecoverybridge.modules.keygeneration.service.KeyPairService;
//import com.google.gson.Gson;
//import com.nimbusds.jose.EncryptionMethod;
//import com.nimbusds.jose.JOSEException;
//import com.nimbusds.jose.JWEAlgorithm;
//import com.nimbusds.jose.JWEHeader;
//import com.nimbusds.jose.JWEObject;
//import com.nimbusds.jose.Payload;
//import com.nimbusds.jose.crypto.RSAEncrypter;
//import org.json.JSONObject;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentMatchers;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.client.RestTemplate;
//
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import java.io.IOException;
//import java.net.URI;
//import java.security.KeyFactory;
//import java.security.NoSuchAlgorithmException;
//import java.security.PublicKey;
//import java.security.interfaces.RSAPublicKey;
//import java.security.spec.InvalidKeySpecException;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.Base64;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//public class SecurityLayerControllerTest {
//
//    private ClientModel publicKey;
//    private JWEAlgorithm algorithmOfCriptography = JWEAlgorithm.RSA_OAEP_256;
//    private EncryptionMethod methodOfEnccryption = EncryptionMethod.A128CBC_HS256;
//    private JWEObject jwe;
//    private MockHttpServletResponse response;
//
//    @Autowired
//    private DecryptorService decryptorService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private SecurityLayerController securityLayerController;
//
//    @Autowired
//    private KeyPairService keyPairService;
//
//    @MockBean(name = "sslRestTemplate")
//    private RestTemplate sslRestTemplate;
//
//    @MockBean(name = "jmsTemplate")
//    private JmsTemplate jmsTemplate;
//
//    @Before
//    public void setUp() throws IOException, NoSuchAlgorithmException {
//        this.response = new MockHttpServletResponse();
//        publicKey = keyPairService.getPublicKey();
//        this.mockMvc = MockMvcBuilders.standaloneSetup(securityLayerController).build();
//    }
//
//    @Test
//    public void testDecryptKeyIsHttpStatusCode200() throws Exception {
//        JSONObject fieldsJson = new JSONObject();
//        fieldsJson.put("requestId", publicKey.getUuid());
//        fieldsJson.put("amount", 0.01);
//        fieldsJson.put("cardNumber", "5067550003989012");
//        fieldsJson.put("cardholderName", "PORTADOR DO CARTAO");
//        fieldsJson.put("establishmentCode", "000001000603439");
//        fieldsJson.put("expirationMonth", 8);
//        fieldsJson.put("expirationYear", 20);
//        fieldsJson.put("securityCode", "00");
//
//        // Configurando RestTemplate para mockar chamada à API de captura
//        ResponseEntity<String> response = ResponseEntity.created(URI.create("/")).body("");
//        when(
//                sslRestTemplate
//                        .exchange(
//                                any(String.class),
//                                eq(HttpMethod.POST),
//                                ArgumentMatchers.<HttpEntity<String>>any(),
//                                eq(String.class)
//                        )
//        ).thenReturn(response);
//
//        EncryptCommercialModel encryptCommercial = new EncryptCommercialModel(
//                publicKey.getUuid(),
//                generatesCryptography(getPublicKey(publicKey.getPublicKey()), fieldsJson)
//        );
//
//        this.mockMvc.perform(post("/security/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new Gson().toJson(encryptCommercial)))
//                .andExpect(status().isCreated());
//
//        // Validando que o sslRestTemplate foi chamado com os parâmetros esperados
//        Mockito.verify(sslRestTemplate)
//                .exchange(
//                        any(String.class),
//                        eq(HttpMethod.POST),
//                        ArgumentMatchers.<HttpEntity<String>>any(),
//                        eq(String.class)
//                );
//
//        Mockito.verify(jmsTemplate).convertAndSend(eq(QueuesActiveMQ.TRANSACTIONS_QUEUE.name()), any(String.class));
//    }
//
//    private String generatesCryptography(PublicKey rsaPublicKey, JSONObject data) throws NoSuchAlgorithmException, JOSEException {
//        jwe = new JWEObject(
//                new JWEHeader(algorithmOfCriptography, methodOfEnccryption),
//                new Payload(String.valueOf(data))
//        );
//        jwe.encrypt(new RSAEncrypter((RSAPublicKey) rsaPublicKey, generatePredefinedCEKContentKey()));
//        return jwe.serialize();
//    }
//
//    private PublicKey getPublicKey(String publicKeyServer) throws InvalidKeySpecException, NoSuchAlgorithmException {
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyServer)));
//    }
//
//    private SecretKey generatePredefinedCEKContentKey() throws NoSuchAlgorithmException {
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        keyGenerator.init(methodOfEnccryption.cekBitLength());
//        return keyGenerator.generateKey();
//    }
//
//}