package br.ufsc.labsec.emissoravancado.persistence.mysql.document;

import br.ufsc.labsec.emissoravancado.persistence.mysql.dossier.DossierEntity;
import javax.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document")
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Basic
    @Column(name = "b64_encoded", nullable = false)
    private String b64Encoded;

    @Basic
    @Column(name = "filename", nullable = false)
    private String filename;

    @Basic
    @Column(name = "document_type", nullable = false)
    private String documentType;

    @ManyToOne
    @JoinColumn(name = "dossier_id", nullable = false)
    private DossierEntity dossier;
}
