package br.ufsc.labsec.emissoravancado.components.enums;

import lombok.Getter;

public enum HawaCaEndpoints {
    NO_CMC_SIGN_NO_CSR("/no-cmc/sign-no-csr");

    @Getter private final String endpoint;

    HawaCaEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }
}
