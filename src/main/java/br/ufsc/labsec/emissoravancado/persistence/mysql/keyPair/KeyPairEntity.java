package br.ufsc.labsec.emissoravancado.persistence.mysql.keyPair;

import br.ufsc.labsec.emissoravancado.persistence.mysql.certificate.CertificateEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "key_pair")
public class KeyPairEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "b64_private_key", nullable = false)
    private String b64_private_key;

    @Basic
    @Column(name = "b64_public_key", nullable = false)
    private String b64_public_key;

    @OneToOne(mappedBy = "key")
    private CertificateEntity certificate;

}
