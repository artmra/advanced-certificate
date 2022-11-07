package br.ufsc.labsec.emissoravancado.persistence.mysql.document;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DocumentTypeEnum {
    CNH("CNH"),
    VERIFIER_REPORT("VERIFIER_REPORT"),
    CNH_INFO("EXTRACTED_CNH_INFO");

    private final String documentType;

    public static String getFileSufix(DocumentTypeEnum documentTypeEnum) {
        switch (documentTypeEnum) {
            case CNH:
                return ".pdf";
            case CNH_INFO:
            case VERIFIER_REPORT:
                return ".json";
            default:
                throw new RuntimeException("Enum com valor não suportado");
        }
    }
}
