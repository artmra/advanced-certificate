package br.ufsc.labsec.emissoravancado.persistence.mysql.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    @Override
    Optional<ClientEntity> findById(Long id);
    Optional<ClientEntity> findByCpf(String cpf);
}
