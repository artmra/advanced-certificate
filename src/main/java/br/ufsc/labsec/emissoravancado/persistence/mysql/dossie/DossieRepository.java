package br.ufsc.labsec.emissoravancado.persistence.mysql.dossie;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DossieRepository extends JpaRepository<DossieEntity, Long> {}
