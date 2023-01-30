package br.com.itau.creditrecoverybridge.decryption.service;

import br.com.itau.creditrecoverybridge.exceptions.BusinessException;
import br.com.itau.creditrecoverybridge.modules.decryption.model.EncryptCommercialModel;
import br.com.itau.creditrecoverybridge.modules.decryption.service.DecryptorService;
import br.com.itau.creditrecoverybridge.modules.keygeneration.model.ClientModel;
import br.com.itau.creditrecoverybridge.modules.keygeneration.service.KeyPairService;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSAEncrypter;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Base64;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.core.StringRegularExpression.matchesRegex;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DecryptorServiceImplTest {

    public static final String TYPE_ALGORITHM = "RSA";
    public static final String TYPE_ALGORITHM1_RSA = TYPE_ALGORITHM;
    public static final String AES = "AES";

    private ClientModel publicKey;
    private JWEAlgorithm algorithmOfCriptography = JWEAlgorithm.RSA_OAEP_256;
    private EncryptionMethod methodOfEnccryption = EncryptionMethod.A128CBC_HS256;
    private JWEObject jwe;
    private EncryptCommercialModel encryptCommercial;
    private EncryptCommercialModel encryptCommercialErrorBusiness;
    private JSONObject fieldsJson;
    private JSONObject fieldsJsonError;

    @Autowired
    private DecryptorService decryptorService;

    @Autowired
    private KeyPairService keyPairService;

    @Before
    public void setUp() throws Exception {
        publicKey = keyPairService.getPublicKey();

        fieldsJson = new JSONObject();
        fieldsJson.put("requestId", publicKey.getUuid());
        fieldsJson.put("amount", 0.01);
        fieldsJson.put("cardNumber", "5067550003989012");
        fieldsJson.put("cardholderName", "PORTADOR DO CARTAO");
        fieldsJson.put("establishmentCode", "000001000603439");
        fieldsJson.put("expirationMonth", 8);
        fieldsJson.put("expirationYear", 20);
        fieldsJson.put("securityCode", "00");

        encryptCommercial = new EncryptCommercialModel(
                publicKey.getUuid(),
                generatesCryptography(getPublicKey(publicKey.getPublicKey()), fieldsJson)
        );

        fieldsJsonError = new JSONObject();
        fieldsJsonError.put("requestId", publicKey.getUuid());
        fieldsJsonError.put("amount", 0.00);
        fieldsJsonError.put("cardNumber", "5067550003989012");
        fieldsJsonError.put("cardholderName", "PORTADOR DO CARTAO");
        fieldsJsonError.put("establishmentCode", "000001000603439");
        fieldsJsonError.put("expirationMonth", 8);
        fieldsJsonError.put("expirationYear", 20);
        fieldsJsonError.put("securityCode", "00");

        encryptCommercialErrorBusiness = new EncryptCommercialModel(
                publicKey.getUuid(),
                generatesCryptography(getPublicKey(publicKey.getPublicKey()), fieldsJsonError)
        );
    }

    @Test
    public void testCreateHttpEntityAndDecryptBodyIsEquals() throws ClassNotFoundException, IOException, ParseException, JOSEException, JSONException {
        HttpEntity<String> httpEntity = decryptorService.createHttpEntityAndDecrypt(encryptCommercial.getToken(), encryptCommercial.getUuid());
        assertThatJson(httpEntity.getBody())
                .node("requestId").isEqualTo(publicKey.getUuid())
                .node("amount").isEqualTo(fieldsJson.get("amount"))
                .node("cardNumber").matches(matchesRegex("\\d{16}"))
                .node("cardholderName").isEqualTo(fieldsJson.get("cardholderName"))
                .node("establishmentCode").matches(matchesRegex("\\d{15}"))
                .node("expirationDate").isEqualTo(padWithZeroes(fieldsJson.getInt("expirationYear"), fieldsJson.getInt("expirationMonth")))
                .node("securityCode").matches(matchesRegex(""))
                .node("currencyCode").isEqualTo("\"986\"")
                .node("dateTime").matches(matchesRegex("\\d{12}"))
        ;
    }

    @Test(expected = BusinessException.class)
    public void testCreateHttpEntityAndDecryptBodyExpectedBusinessException() throws ClassNotFoundException, IOException, ParseException, JOSEException, JSONException {
        decryptorService.createHttpEntityAndDecrypt(encryptCommercialErrorBusiness.getToken(), encryptCommercialErrorBusiness.getUuid());
    }

    private String padWithZeroes(Integer year, Integer month) {
        return MessageFormat.format("\"{0,number,00}{1,number,00}\"", year, month);
    }

    @Test
    public void testCreateHttpEntityAndDecryptContentTypeIsJsonValid() throws ClassNotFoundException, IOException, JOSEException, ParseException {
        HttpEntity<String> httpEntity = decryptorService.createHttpEntityAndDecrypt(encryptCommercial.getToken(), encryptCommercial.getUuid());
        assertEquals(MediaType.APPLICATION_JSON, httpEntity.getHeaders().getContentType());
    }

    @Test(expected = InvalidKeySpecException.class)
    public void testCreateHttpEntityAndDecryptExpectedInvalidKeySpecException() throws ClassNotFoundException, IOException, ParseException, JOSEException, InvalidKeySpecException, NoSuchAlgorithmException {

        String encodeToString = Base64.getEncoder().encodeToString(publicKey.getPublicKey().getBytes());
        EncryptCommercialModel encryptCommercialFailed = new EncryptCommercialModel(
                publicKey.getUuid(),
                generatesCryptography(getPublicKey(encodeToString), fieldsJson)
        );

        HttpEntity<String> httpEntity = decryptorService.createHttpEntityAndDecrypt(encryptCommercialFailed.getToken(), encryptCommercial.getUuid());
        assertEquals(fieldsJson.toString(), httpEntity.getBody());
    }

    private String generatesCryptography(PublicKey rsaPublicKey, JSONObject data) throws NoSuchAlgorithmException, JOSEException {
        jwe = new JWEObject(
                new JWEHeader(algorithmOfCriptography, methodOfEnccryption),
                new Payload(String.valueOf(data))
        );
        jwe.encrypt(new RSAEncrypter((RSAPublicKey) rsaPublicKey, generatePredefinedCEKContentKey()));
        return jwe.serialize();
    }

    private PublicKey getPublicKey(String publicKeyServer) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance(TYPE_ALGORITHM1_RSA);
        return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyServer)));
    }

    private SecretKey generatePredefinedCEKContentKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        keyGenerator.init(methodOfEnccryption.cekBitLength());
        return keyGenerator.generateKey();
    }
}