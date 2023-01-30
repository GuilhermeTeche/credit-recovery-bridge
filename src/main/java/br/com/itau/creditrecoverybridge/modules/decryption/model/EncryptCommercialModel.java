package br.com.itau.creditrecoverybridge.modules.decryption.model;

import br.com.itau.creditrecoverybridge.util.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EncryptCommercialModel {

    @JsonProperty
    @NotEmpty
    @Pattern(regexp = Constants.REGEX_UUID, message = Constants.MESSAGE_REGEX_UUID)
    private String uuid;

    @JsonProperty
    @NotEmpty
    private String token;
}
