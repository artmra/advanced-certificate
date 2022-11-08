package br.ufsc.labsec.emissoravancado.persistence.mysql.certificate;

import br.ufsc.labsec.emissoravancado.persistence.mysql.client.ClientEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.dossier.DossierEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.keyPair.KeyPairEntity;
import javax.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "certificate")
public class CertificateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Basic
    @Column(name = "b64Cert", nullable = false)
    private String b64Cert;

    @Basic
    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dossier_id", referencedColumnName = "id")
    private DossierEntity dossier;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "key_pair_id", referencedColumnName = "id")
    private KeyPairEntity key;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "cpf", nullable = false)
    private ClientEntity client;
}
