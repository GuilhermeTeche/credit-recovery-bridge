package br.com.itau.creditrecoverybridge.modules.decryption.controller;

import br.com.itau.creditrecoverybridge.config.BureausCreditAdapterSerasaConfiguration;
import br.com.itau.creditrecoverybridge.exceptions.TransactionBadRequestException;
import br.com.itau.creditrecoverybridge.modules.decryption.model.EncryptCommercialModel;
import br.com.itau.creditrecoverybridge.modules.decryption.service.DecryptorService;
import br.com.itau.creditrecoverybridge.util.Constants;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

/**
 * Controller to address and decrypt requests for the STX service
 */
@Slf4j
@RestController
@RequestMapping(value = "/security", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SecurityLayerController {

    @Autowired
    private DecryptorService decryptorService;

    @Autowired
    private BureausCreditAdapterSerasaConfiguration bureausCreditAdapterSerasaConfiguration;

    @Autowired
    private RestTemplate sslRestTemplate;

    /**
     * @param encryptCommercialModel
     * @return ResponseEntity </String>
     */
    @PostMapping("/create")
    public ResponseEntity<String> createTransaction(@RequestBody EncryptCommercialModel encryptCommercialModel, HttpServletResponse response) throws ClassNotFoundException, IOException, ParseException, JOSEException {
        log.info("9- decrypting token jwe...");
        try {
            sslRestTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            ResponseEntity<String> exchange = sslRestTemplate.exchange(
                    bureausCreditAdapterSerasaConfiguration.getUrl(),
                    HttpMethod.POST,
                    decryptorService.createHttpEntityAndDecrypt(encryptCommercialModel.getToken(), encryptCommercialModel.getUuid()),
                    String.class
            );

            log.info("12 - receive return serasa and save in sqs.");
            return exchange;
        } catch (HttpStatusCodeException e) {
            throw new TransactionBadRequestException(Constants.FAILED_COMUNICATION_PAYSTUDIO);
        }
    }
}
