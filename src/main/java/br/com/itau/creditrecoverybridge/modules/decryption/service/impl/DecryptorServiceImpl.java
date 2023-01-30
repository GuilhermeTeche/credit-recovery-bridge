package br.com.itau.creditrecoverybridge.modules.decryption.service.impl;

import br.com.itau.creditrecoverybridge.exceptions.BusinessException;
import br.com.itau.creditrecoverybridge.modules.decryption.mapper.CommercialPlaceMapper;
import br.com.itau.creditrecoverybridge.modules.decryption.model.CommercialModel;
import br.com.itau.creditrecoverybridge.modules.decryption.model.CommercialPlaceDataModel;
import br.com.itau.creditrecoverybridge.modules.decryption.service.DecryptorService;
import br.com.itau.creditrecoverybridge.modules.keygeneration.model.KeyPairModel;
import br.com.itau.creditrecoverybridge.modules.keygeneration.service.KeyPairService;
import br.com.itau.creditrecoverybridge.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.itau.creditrecoverybridge.modules.decryption.model.QueuesActiveMQ.TRANSACTIONS_QUEUE;
import static java.util.Objects.requireNonNull;

/**
 * Class responsible for generate pair of key's and decryptography content JWE
 */
@Slf4j
@Service
public class DecryptorServiceImpl implements DecryptorService {

    @Autowired
    private KeyPairService keyPairService;

    @Autowired
    private CommercialPlaceMapper commercialPlaceMapper;

    @Autowired
    private Validator validator;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * @param exchange
     * @param response
     * @return
     */
    @Override
    public HttpEntity<String> saveTransactionPaysStudio(HttpEntity<String> exchange, HttpServletResponse response) throws JsonProcessingException {
        jmsTemplate.convertAndSend(TRANSACTIONS_QUEUE.toString(), requireNonNull(exchange.getBody()));
        return new HttpEntity<>(requireNonNull(exchange.getBody()));
    }

    /**
     * @param tokenJwe
     * @return
     */
    @Override
    public HttpEntity<String> createHttpEntityAndDecrypt(String tokenJwe, String uuid) throws ClassNotFoundException, JOSEException, ParseException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(decryptData(tokenJwe, uuid), headers);
    }

    /**
     * Responsible method to decrypt the sensitive data sent by the sdk client
     *
     * @param tokenJwe
     * @param uuid
     * @return String
     */
    private String decryptData(String tokenJwe, String uuid) throws IOException, ClassNotFoundException, ParseException, JOSEException {
        log.info("10- performing JWE parser...");
        KeyPairModel byUuid = keyPairService.findByUuid(uuid);
        JWEObject jweObject = JWEObject.parse(tokenJwe);
        jweObject.decrypt(new RSADecrypter((RSAPrivateKey) keyPairService.desserializePrivateKey(byUuid)));

        log.info("11- decryption performed successfully.");
        return new Gson().toJson(transformToCommercialModel(fieldValidator(jweObject, byUuid)));
    }

    /**
     * @param jweObject
     * @param byUuid
     * @return CommercialPlaceDataModel
     */
    private CommercialPlaceDataModel fieldValidator(JWEObject jweObject, KeyPairModel byUuid) {
        log.info("10.1- validating filds...");
        CommercialPlaceDataModel commercialPlaceDataModel = new Gson().fromJson(String.valueOf(jweObject.getPayload()), CommercialPlaceDataModel.class);
        validateOriginCall(byUuid, commercialPlaceDataModel);
        Set<ConstraintViolation<CommercialPlaceDataModel>> errors = validator.validate(commercialPlaceDataModel);

        if (!errors.isEmpty()) {
            log.error("10.2- fild(s) invalid(s)...");
            List<String> errorMessages = errors.stream()
                    .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                    .collect(Collectors.toList());
            throw new BusinessException(errorMessages);
        }
        log.info("10.3- fields validated successfull!");
        return commercialPlaceDataModel;
    }

    /**
     * @param byUuid
     * @param commercialPlaceDataModel
     */
    private void validateOriginCall(KeyPairModel byUuid, CommercialPlaceDataModel commercialPlaceDataModel) {
        if (!byUuid.getId().equals(commercialPlaceDataModel.getRequestId())) {
            throw new BusinessException(Constants.INVALID_SENDER);
        }
    }

    /**
     * @param commercialPlaceDataModel
     * @return
     */
    private CommercialModel transformToCommercialModel(CommercialPlaceDataModel commercialPlaceDataModel) {
        return commercialPlaceMapper.commercialPlaceDataToCommercialModel(commercialPlaceDataModel);
    }
}
