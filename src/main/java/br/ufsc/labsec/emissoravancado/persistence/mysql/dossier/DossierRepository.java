package br.ufsc.labsec.emissoravancado.persistence.mysql.dossier;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DossierRepository extends JpaRepository<DossierEntity, Long> {}
