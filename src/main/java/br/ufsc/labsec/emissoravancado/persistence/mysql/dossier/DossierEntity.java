package br.ufsc.labsec.emissoravancado.persistence.mysql.dossier;

import br.ufsc.labsec.emissoravancado.persistence.mysql.certificate.CertificateEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.document.DocumentEntity;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "dossier", schema = "emissor_avancado", catalog = "")
public class DossierEntity implements Serializable {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Lob
    @Column(name = "xml", nullable = false)
    private String xml;

    @OneToOne(mappedBy = "dossier")
    private CertificateEntity certificate;

    @OneToMany(mappedBy = "dossier")
    private Set<DocumentEntity> documents;

    public DossierEntity(String xml) {
        this.xml = xml;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DossierEntity that = (DossierEntity) o;
        return id == that.id && Objects.equals(xml, that.xml);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, xml);
    }
}
