package br.ufsc.labsec.emissoravancado.persistence.mysql.key;

import javax.persistence.*;
import lombok.*;

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
    @Column(name = "b64_encoded_private_key", nullable = false)
    private String b64EncodedPrivateKey;

    @Basic
    @Column(name = "b64_encoded_public_key", nullable = false)
    private String b64EncodedPublicKey;
}
