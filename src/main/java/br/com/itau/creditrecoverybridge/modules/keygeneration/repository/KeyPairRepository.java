package br.com.itau.creditrecoverybridge.modules.keygeneration.repository;

import br.com.itau.creditrecoverybridge.modules.keygeneration.model.KeyPairModel;
import org.springframework.data.repository.CrudRepository;

public interface KeyPairRepository extends CrudRepository<KeyPairModel, String> {
}
