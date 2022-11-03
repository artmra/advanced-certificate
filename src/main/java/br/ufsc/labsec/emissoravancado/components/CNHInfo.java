package br.ufsc.labsec.emissoravancado.components;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CNHInfo {
    private final String name;
    private final String docInfo;
    private final String cpf;
    private final String birthDate;
    private final String birthData;
    private final String fatherName;
    private final String motherName;
    private final String cnh;
    private final String validity;
    private final String firstCNHDate;
    private final String issuePlace;
    private final String issueDate;
    private final String nationality;
}
