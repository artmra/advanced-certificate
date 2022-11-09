package br.ufsc.labsec.emissoravancado.dto.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CNHServiceRevokeResponse {
    private boolean revoked;
    private String serialNumber;
    private Date revocationDate;
}
