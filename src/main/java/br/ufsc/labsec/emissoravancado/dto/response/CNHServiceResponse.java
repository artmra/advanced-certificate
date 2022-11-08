package br.ufsc.labsec.emissoravancado.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CNHServiceResponse {
    private String certB64;
    private String certSerialNumber;
}
