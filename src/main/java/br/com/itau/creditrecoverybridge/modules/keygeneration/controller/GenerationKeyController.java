package br.com.itau.creditrecoverybridge.modules.keygeneration.controller;

import br.com.itau.creditrecoverybridge.modules.keygeneration.model.ClientModel;
import br.com.itau.creditrecoverybridge.modules.keygeneration.service.KeyPairService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping(value = "/keys", produces = MediaType.APPLICATION_JSON_VALUE)
public class GenerationKeyController {

    @Autowired
    private KeyPairService keyPairService;

    /**
     * @return ResponseEntity String
     */
    @GetMapping("/getPublicKey")
    public ResponseEntity<ClientModel> getPublicKey() throws IOException, NoSuchAlgorithmException {
        return ResponseEntity.ok(keyPairService.getPublicKey());
    }
}
