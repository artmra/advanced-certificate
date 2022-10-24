package br.ufsc.labsec.emissoravancado.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PemEnum {
    PRIVATE_KEY("PRIVATE KEY"),
    PUBLIC_KEY("PUBLIC KEY");

    @Getter private final String header;
}
