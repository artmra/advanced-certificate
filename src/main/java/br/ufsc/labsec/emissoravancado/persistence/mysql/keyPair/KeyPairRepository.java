package br.ufsc.labsec.emissoravancado.persistence.mysql.keyPair;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyPairRepository extends JpaRepository<KeyPairEntity, Long> {
}
