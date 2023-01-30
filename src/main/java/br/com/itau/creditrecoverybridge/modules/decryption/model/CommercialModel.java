package br.com.itau.creditrecoverybridge.modules.decryption.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
public class CommercialModel {

    @JsonProperty
    private String requestId;

    @JsonProperty
    private BigDecimal amount;

    @JsonProperty
    private String cardNumber;

    @JsonProperty
    private String cardholderName;

    @JsonProperty
    private String establishmentCode;

    @JsonProperty
    private String expirationDate;

    @JsonProperty
    private String securityCode;

    @JsonProperty
    private String currencyCode;

    @JsonProperty
    private String dateTime;
}
