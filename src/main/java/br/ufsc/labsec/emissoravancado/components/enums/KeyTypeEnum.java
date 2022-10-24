package br.ufsc.labsec.emissoravancado.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum KeyTypeEnum {
    RSA("RSA");

    @Getter public final String name;
}
