package br.ufsc.labsec.emissoravancado.persistence.mysql.keyPair;

import br.ufsc.labsec.emissoravancado.persistence.mysql.certificate.CertificateEntity;
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
@Table(name = "key_pair", schema = "emissor_avancado", catalog = "")
public class KeyPairEntity implements Serializable {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Lob
    @Column(name = "b64_encrypted_private_key", nullable = false)
    private String b64EncryptedPrivateKey;

    @Basic
    @Lob
    @Column(name = "b64_public_key", nullable = false)
    private String b64PublicKey;

    @OneToOne(mappedBy = "key")
    private CertificateEntity certificate;

    public KeyPairEntity(String b64PrivateKey, String b64PublicKey) {
        this.b64EncryptedPrivateKey = b64PrivateKey;
        this.b64PublicKey = b64PublicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyPairEntity that = (KeyPairEntity) o;
        return id == that.id
                && Objects.equals(b64EncryptedPrivateKey, that.b64EncryptedPrivateKey)
                && Objects.equals(b64PublicKey, that.b64PublicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, b64EncryptedPrivateKey, b64PublicKey);
    }
}
