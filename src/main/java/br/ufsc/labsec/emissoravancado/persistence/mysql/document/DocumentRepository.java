package br.ufsc.labsec.emissoravancado.persistence.mysql.document;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
}
