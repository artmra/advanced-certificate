package br.ufsc.labsec.emissoravancado.persistence.mysql.certificate;

import javax.persistence.*;

import br.ufsc.labsec.emissoravancado.persistence.mysql.client.ClientEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.dossier.DossierEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.keyPair.KeyPairEntity;
import lombok.*;

@Getter
@Setter
@Builder
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
    @Column(name = "pem", nullable = false)
    private String pem;

    @Basic
    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dossier_id", referencedColumnName = "id")
    private DossierEntity dossier;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "client_id", referencedColumnName = "cpf")
//    private ClientEntity client;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "key_pair_id", referencedColumnName = "id")
    private KeyPairEntity key;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "cpf", nullable = false)
    private ClientEntity client;

}
