package br.ufsc.labsec.emissoravancado.persistence.mysql.client;

import br.ufsc.labsec.emissoravancado.persistence.mysql.certificate.CertificateEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "client", schema = "emissor_avancado")
public class ClientEntity implements Serializable {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id")
    private long id;
    @Basic
    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;
    @Basic
    @Column(name = "last_certificate_serial_number", nullable = false)
    private String lastCertificateSerialNumber;

    @OneToMany(mappedBy = "client")
    private Set<CertificateEntity> certificates;

    public ClientEntity(String cpf, String s) {
        this.cpf = cpf;
        this.lastCertificateSerialNumber = s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientEntity client = (ClientEntity) o;
        return id == client.id && Objects.equals(cpf, client.cpf) && Objects.equals(lastCertificateSerialNumber, client.lastCertificateSerialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cpf, lastCertificateSerialNumber);
    }
}



