package br.com.itau.creditrecoverybridge.modules.decryption.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import org.springframework.http.HttpEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

public interface DecryptorService {
    HttpEntity<String> saveTransactionPaysStudio(HttpEntity<String> exchange, HttpServletResponse response) throws JsonProcessingException;
    HttpEntity<String> createHttpEntityAndDecrypt(String tokenJwe, String uuid) throws ClassNotFoundException, JOSEException, ParseException, IOException;
}