package br.ufsc.labsec.emissoravancado.components.enums;

import lombok.Getter;

public enum HawaCaEndpoints {
    NO_CMC_SIGN_NO_CSR("/no-cmc/sign-no-csr"),
    NO_CMC_REVOKE("/no-cmc/revoke");

    @Getter private final String endpoint;

    HawaCaEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }
}
