package br.ufsc.labsec.emissoravancado.persistence.mysql.document;

import br.ufsc.labsec.emissoravancado.persistence.mysql.dossier.DossierEntity;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "document", schema = "emissor_avancado", catalog = "")
public class DocumentEntity implements Serializable {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Lob
    @Column(name = "b64_encoded", nullable = false)
    private String b64Encoded;

    @Basic
    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Basic
    @Column(name = "filename", nullable = false)
    private String filename;

    @ManyToOne
    @JoinColumn(name = "dossier_id", nullable = false)
    private DossierEntity dossier;

    public DocumentEntity(
            String b64Encoded,
            DocumentTypeEnum documentType,
            String filename,
            DossierEntity dossier) {
        this.b64Encoded = b64Encoded;
        this.documentType = documentType.getDocumentType();
        this.filename = filename;
        this.dossier = dossier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentEntity that = (DocumentEntity) o;
        return id == that.id
                && dossier == that.dossier
                && Objects.equals(b64Encoded, that.b64Encoded)
                && Objects.equals(documentType, that.documentType)
                && Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, b64Encoded, documentType, filename, dossier);
    }
}
