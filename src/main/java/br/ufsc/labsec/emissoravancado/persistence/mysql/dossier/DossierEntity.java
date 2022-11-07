package br.ufsc.labsec.emissoravancado.persistence.mysql.dossier;

import javax.persistence.*;

import br.ufsc.labsec.emissoravancado.persistence.mysql.certificate.CertificateEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.document.DocumentEntity;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dossier")
public class DossierEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "xml", nullable = false)
    private String xml;

    @Basic
    @Column(name = "b64_signature",  nullable = false)
    private String b64Signature;

    @OneToOne(mappedBy = "dossier")
    private CertificateEntity certificate;

    @OneToMany(mappedBy = "dossier")
    private Set<DocumentEntity> documents;
}
