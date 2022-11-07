package br.ufsc.labsec.emissoravancado.persistence.mysql.client;

import javax.persistence.*;

import br.ufsc.labsec.emissoravancado.persistence.mysql.certificate.CertificateEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.dossier.DossierEntity;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client")
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Basic
    @Column(name = "last_certificate_serial_number", nullable = false)
    private String lastCertificateSerialNumber;

    @OneToMany(mappedBy = "client")
    private Set<CertificateEntity> certificate;
}
