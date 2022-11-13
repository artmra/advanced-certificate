package br.ufsc.labsec.emissoravancado.persistence.mysql.certificate;

import br.ufsc.labsec.emissoravancado.persistence.mysql.client.ClientEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.dossier.DossierEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.keyPair.KeyPairEntity;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "certificate", schema = "emissor_avancado", catalog = "")
public class CertificateEntity implements Serializable {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id")
    private long id;

    @Basic
    @Lob
    @Column(name = "b64cert", nullable = false)
    private String b64Cert;

    @Basic
    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Basic
    @Column(name = "revocation_date")
    private Date revocationDate;

    @Basic
    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "cpf", nullable = false)
    private ClientEntity client;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dossier_id", referencedColumnName = "id")
    private DossierEntity dossier;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "key_pair_id", referencedColumnName = "id")
    private KeyPairEntity key;

    public CertificateEntity(
            String b64Cert,
            boolean revoked,
            String serialNumber,
            ClientEntity client,
            DossierEntity dossier,
            KeyPairEntity keyPair) {
        this.b64Cert = b64Cert;
        this.revoked = revoked;
        this.serialNumber = serialNumber;
        this.client = client;
        this.dossier = dossier;
        this.key = keyPair;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateEntity that = (CertificateEntity) o;
        return id == that.id
                && revoked == that.revoked
                && Objects.equals(b64Cert, that.b64Cert)
                && Objects.equals(serialNumber, that.serialNumber)
                && Objects.equals(client, that.client)
                && Objects.equals(dossier, that.dossier)
                && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, b64Cert, revoked, serialNumber, client, dossier, key);
    }
}
