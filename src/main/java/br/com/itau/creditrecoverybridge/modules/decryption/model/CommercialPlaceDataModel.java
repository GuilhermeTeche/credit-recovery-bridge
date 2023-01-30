package br.com.itau.creditrecoverybridge.modules.decryption.model;

import br.com.itau.creditrecoverybridge.util.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
public class CommercialPlaceDataModel {

    @JsonProperty
    @NotEmpty
    @Pattern(regexp = Constants.REGEX_UUID, message = Constants.MESSAGE_REGEX_UUID)
    private String requestId;

    @JsonProperty
    @NotNull
    @Positive
    @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @JsonProperty
    @NotEmpty
    @Length(min = 1, max = 19)
    @Pattern(regexp = "[0-9]*", message = Constants.MESSAGE_ONLY_NUMBERS)
    private String cardNumber;

    @JsonProperty
    @NotEmpty
    @Length(min = 1, max = 30)
    @Pattern(regexp = "[a-zA-Z ]*", message = Constants.MESSAGE_ONLY_LETTERS_SPACE)
    private String cardholderName;

    @JsonProperty
    @NotEmpty
    @Length(min = 15, max = 15, message = Constants.MESSAGE_15_CARACTERS)
    @Pattern(regexp = "[0-9]*", message = Constants.MESSAGE_ONLY_NUMBERS)
    private String establishmentCode;

    @JsonProperty
    @NotNull
    @Min(value = 1, message = Constants.MESSAGE_VALID_MONTH)
    @Max(value = 12, message = Constants.MESSAGE_VALID_MONTH)
    private Integer expirationMonth;

    @JsonProperty
    @NotNull
    @Min(value = 1, message = Constants.MESSAGE_VALID_YEAR)
    @Max(value = 99, message = Constants.MESSAGE_VALID_YEAR)
    private Integer expirationYear;

    @JsonProperty
    @NotEmpty
    @Length(min = 1, max = 4)
    private String securityCode;
}
