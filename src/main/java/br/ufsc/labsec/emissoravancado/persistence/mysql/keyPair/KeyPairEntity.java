package br.ufsc.labsec.emissoravancado.persistence.mysql.keyPair;

import br.ufsc.labsec.emissoravancado.persistence.mysql.certificate.CertificateEntity;
import java.io.Serializable;
import javax.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "key_pair")
public class KeyPairEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "b64_private_key", nullable = false)
    private String b64PrivateKey;

    @Basic
    @Column(name = "b64_public_key", nullable = false)
    private String b64PublicKey;

    @OneToOne(mappedBy = "key")
    private CertificateEntity certificate;

    public KeyPairEntity(String b64PrivateKey, String b64PublicKey) {
        this.b64PrivateKey = b64PrivateKey;
        this.b64PublicKey = b64PublicKey;
    }
}
