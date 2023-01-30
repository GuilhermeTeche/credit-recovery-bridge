package br.com.itau.creditrecoverybridge.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ItauCreditRecoveryExceptionHandlerTest {

    public static final String UUID_NOT_FOUND = "UUID informado, não encontrado!";
    public static final String ALGORITHM_NOT_FOUND = "Algoritmo criptográfico específico solicitado, mas não está disponível no ambiente!";
    public static final String ENTRY_AND_EXIT_CORROMPTY = "Entrada-Saída/Serialização-Desserialização interrompida!";
    public static final String CLASS_NOT_FOUND_IN_CLASSPATH = "Falha ao tentar carregar classe, não encontrou a classe solicitada no classpath!";
    public static final String PARSER_ERROR = "Erro atingido inesperadamente durante a análise do parser!";
    public static final String EXPORT_PRIVATE_KEY_NOT_SUPPORTED = "Exportar para java.security.PrivateKey não suportado.";

    @InjectMocks
    private ItauCreditRecoveryExceptionHandler itauCreditRecoveryExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private MessageSource messageSource;

    @Mock
    private TransactionNotFoundException transactionNotFoundException;

    @Mock
    private NoSuchAlgorithmException noSuchAlgorithmException;

    @Mock
    private IOException ioException;

    @Mock
    private ClassNotFoundException classNotFoundException;

    @Mock
    private ParseException parseException;

    @Mock
    private JOSEException joseException;

    @Mock
    private BusinessException businessException;

    @Mock
    private JsonProcessingException jsonprocessingexception;

    @Before
    public void setUp() {
        Map<String, String[]> mockParameterMap = new HashMap<>();
        mockParameterMap.put("teste", new String[]{"teste01"});
        when(webRequest.getParameterMap()).thenReturn(mockParameterMap);
    }

    @Test
    public void testHandleEmptyResultDataAccessException() {
        when(transactionNotFoundException.getMessage()).thenReturn(UUID_NOT_FOUND);
        ResponseEntity<Object> responseEntity = itauCreditRecoveryExceptionHandler.handleTransactionNotFoundException(transactionNotFoundException, webRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testHandleNoSuchAlgorithmException() {
        when(noSuchAlgorithmException.getMessage()).thenReturn(ALGORITHM_NOT_FOUND);
        ResponseEntity<Object> responseEntity = itauCreditRecoveryExceptionHandler.handleNoSuchAlgorithmException(noSuchAlgorithmException, webRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testHandleIOException() {
        when(ioException.getMessage()).thenReturn(ENTRY_AND_EXIT_CORROMPTY);
        ResponseEntity<Object> responseEntity = itauCreditRecoveryExceptionHandler.handleIOException(ioException, webRequest);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testHandleClassNotFoundException() {
        when(classNotFoundException.getMessage()).thenReturn(CLASS_NOT_FOUND_IN_CLASSPATH);
        ResponseEntity<Object> responseEntity = itauCreditRecoveryExceptionHandler.handleClassNotFoundException(classNotFoundException, webRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testHandleParseException() {
        when(parseException.getMessage()).thenReturn(PARSER_ERROR);
        ResponseEntity<Object> responseEntity = itauCreditRecoveryExceptionHandler.handleParseException(parseException, webRequest);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testHandleJOSEException() {
        when(joseException.getMessage()).thenReturn(EXPORT_PRIVATE_KEY_NOT_SUPPORTED);
        ResponseEntity<Object> responseEntity = itauCreditRecoveryExceptionHandler.handleJOSEException(joseException, webRequest);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testHandleBusinessException() {
        ResponseEntity<Object> responseEntity = itauCreditRecoveryExceptionHandler.handleBusinessException(businessException, webRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testHandleJsonProcessingException() {
        ResponseEntity<Object> responseEntity = itauCreditRecoveryExceptionHandler.handleJsonProcessingException(jsonprocessingexception, webRequest);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testMethodErrorGetMessageDeveloper() {
        ItauCreditRecoveryExceptionHandler.Error error = new ItauCreditRecoveryExceptionHandler.Error("teste", "teste");
        Assert.assertEquals("teste", error.getMessageDeveloper());
    }

    @Test
    public void testMethodErrorGetMessageUser() {
        ItauCreditRecoveryExceptionHandler.Error error = new ItauCreditRecoveryExceptionHandler.Error("teste", "teste");
        Assert.assertEquals("teste", error.getMessageUser());
    }
}