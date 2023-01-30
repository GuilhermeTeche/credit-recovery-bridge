package br.com.itau.creditrecoverybridge.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class ItauCreditRecoveryExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({TransactionBadRequestException.class})
    public ResponseEntity<Object> handleTransactionBadRequestException(TransactionBadRequestException ex, WebRequest request) {
        String messageUser = messageSource.getMessage("message.transactionbadrequest", null, LocaleContextHolder.getLocale());
        String messageDeveloper = ex.toString();
        List<Error> errors = Arrays.asList(new Error(messageUser, messageDeveloper));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({TransactionNotFoundException.class})
    public ResponseEntity<Object> handleTransactionNotFoundException(TransactionNotFoundException ex, WebRequest request) {
        String messageUser = messageSource.getMessage("message.uuidnotfound", null, LocaleContextHolder.getLocale());
        String messageDeveloper = ex.toString();
        List<Error> errors = Arrays.asList(new Error(messageUser, messageDeveloper));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({NoSuchAlgorithmException.class})
    public ResponseEntity<Object> handleNoSuchAlgorithmException(NoSuchAlgorithmException ex, WebRequest request) {
        String messageUser = messageSource.getMessage("message.nosuchalgorithm", null, LocaleContextHolder.getLocale());
        String messageDeveloper = ex.toString();
        List<Error> errors = Arrays.asList(new Error(messageUser, messageDeveloper));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<Object> handleIOException(IOException ex, WebRequest request) {
        String messageUser = messageSource.getMessage("message.ioexception", null, LocaleContextHolder.getLocale());
        String messageDeveloper = ex.toString();
        List<Error> errors = Arrays.asList(new Error(messageUser, messageDeveloper));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ClassNotFoundException.class})
    public ResponseEntity<Object> handleClassNotFoundException(ClassNotFoundException ex, WebRequest request) {
        String messageUser = messageSource.getMessage("message.classnotfound", null, LocaleContextHolder.getLocale());
        String messageDeveloper = ex.toString();
        List<Error> errors = Arrays.asList(new Error(messageUser, messageDeveloper));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ParseException.class})
    public ResponseEntity<Object> handleParseException(ParseException ex, WebRequest request) {
        String messageUser = messageSource.getMessage("message.parseexception", null, LocaleContextHolder.getLocale());
        String messageDeveloper = ex.toString();
        List<Error> errors = Arrays.asList(new Error(messageUser, messageDeveloper));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({JOSEException.class})
    public ResponseEntity<Object> handleJOSEException(JOSEException ex, WebRequest request) {
        String messageUser = messageSource.getMessage("message.joseexception", null, LocaleContextHolder.getLocale());
        String messageDeveloper = ex.toString();
        List<Error> errors = Arrays.asList(new Error(messageUser, messageDeveloper));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({JsonProcessingException.class})
    public ResponseEntity<Object> handleJsonProcessingException(JsonProcessingException ex, WebRequest request) {
        String messageUser = messageSource.getMessage("message.jsonprocessingexception", null, LocaleContextHolder.getLocale());
        String messageDeveloper = ex.toString();
        List<Error> errors = Arrays.asList(new Error(messageUser, messageDeveloper));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
        String messageUser = messageSource.getMessage("message.businessexception", null, LocaleContextHolder.getLocale());
        String messageDeveloper = ex.toString();
        List<Error> errors = Arrays.asList(new Error(messageUser, messageDeveloper));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @Getter
    public static class Error {
        private String messageUser;
        private String messageDeveloper;

        public Error(String messageUser, String messageDeveloper) {
            this.messageUser = messageUser;
            this.messageDeveloper = messageDeveloper;
        }
    }
}

