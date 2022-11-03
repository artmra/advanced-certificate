package br.ufsc.labsec.emissoravancado.persistence.mysql.key;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyPairRepository extends JpaRepository<KeyPairEntity, Long> {}
