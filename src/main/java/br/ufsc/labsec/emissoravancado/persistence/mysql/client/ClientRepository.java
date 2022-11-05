package br.ufsc.labsec.emissoravancado.persistence.mysql.client;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {}
