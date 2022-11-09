package br.ufsc.labsec.emissoravancado.persistence.mysql.dossier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DossierRepository extends JpaRepository<DossierEntity, Long> {}
