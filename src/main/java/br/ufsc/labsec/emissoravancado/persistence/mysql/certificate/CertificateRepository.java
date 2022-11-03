package br.ufsc.labsec.emissoravancado.persistence.mysql.certificate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<CertificateEntity, Long> {}
