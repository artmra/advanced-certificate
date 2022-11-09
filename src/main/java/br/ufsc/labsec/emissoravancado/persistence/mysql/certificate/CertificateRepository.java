package br.ufsc.labsec.emissoravancado.persistence.mysql.certificate;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<CertificateEntity, Long> {
    public Optional<CertificateEntity> findBySerialNumber(String serialNumber);
}
