package br.ufsc.labsec.emissoravancado.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class VerifierResponseWithMultiSubAltName extends VerifierResponse {
    private Report report;

    @Data
    public static class Report {
        private ReportVerifier date;
        private String generalStatus;
        private int number;
        private Pas pas;
        private Software software;
        private Lpas lpas;
        private InitialReport initialReport;
        private boolean onlyVerifyAnchored;
        private boolean extendedReport;
        private Conformity conformity;
        private ReportSignatures signatures;
    }

    @Data
    public static class ReportVerifier {
        private String sourceOfDate;
        private String verificationDate;
    }

    @Data
    public static class Pas {
        private Pa pa;
    }

    @Data
    public static class Pa {
        private String valid;
        private String period;
        private boolean expired;
        private String validOnLpa;
        private boolean online;
        private String oid;
        private boolean revoked;
        private String error;
    }

    @Data
    public static class Software {
        private String name;
        private String sourceFileHash;
        private String version;
        private String sourceFile;
    }

    @Data
    public static class Lpas {
        private Lpa lpa;
    }

    @Data
    public static class Lpa {
        private boolean valid;
        private String period;
        private boolean expired;
        private String name;
        private boolean online;
        private int version;
    }

    @Data
    public static class InitialReport {
        private int qtdAnchorsSign;
        private int qtdSignatures;
        private String fileType;
    }

    @Data
    public static class Conformity {
        private NewConformity newConformity;
    }

    @Data
    public static class NewConformity {
        private String reference;
        private String link;
    }

    @Data
    public static class ReportSignatures {
        private ReportSignature signature;
    }

    @Data
    public static class ReportSignature {
        private boolean containsMandatedCertificates;
        private boolean attributeValid;
        private String errorMessages;
        private WarningMessages warningMessages;
        private boolean hasInvalidUpdates;
        private String signaturePolicy;
        private Integrity integrity;
        private String signatureType;
        private ReportAttributes attributes;
        private PaRules paRules;
        private String signingTime;
        private Certification certification;
    }

    @Data
    public static class WarningMessages {
        private String warningMessage;
    }

    @Data
    public static class Integrity {
        private boolean schema;
        private String messageDigest;
        private String references;
        private String asymmetricCipher;
        private String schemaPattern;
        private String hash;
    }

    @Data
    public static class ReportAttributes {
        private RequiredAttributes requiredAttributes;
        private String ignoredAttributes;
        private OptionalAttributes optionalAttributes;
        private String extraAttributes;
    }

    @Data
    public static class RequiredAttributes {
        private List<RequiredAttribute> requiredAttribute;
    }

    @Data
    public static class RequiredAttribute {
        private String name;
        private String status;
        private String alertMessage;
    }

    @Data
    public static class OptionalAttributes {
        private OptionalAttribute optionalAttribute;
    }

    @Data
    public static class OptionalAttribute {
        private String name;
        private String status;
        private String alertMessage;
    }

    @Data
    public static class PaRules {
        private String prohibited;
        private String mandatedCertificateInfo;
        private String required;
    }

    @Data
    public static class Certification {
        private String timeStamps;
        private ReportSigner signer;
    }

    @Data
    public static class ReportSigner {
        private String certPathMessage;
        private Extensions extensions;
        private String validSignature;
        private String form;
        private List<ReportCertificate> certificate;
        private String certPathValid;
        private boolean present;
        private String subjectName;
    }

    @Data
    public static class Extensions {
        private SubjectAlternativeNames subjectAlternativeNames;
    }

    @Data
    public static class SubjectAlternativeNames {
        private List<GeneralName> generalName;
    }

    @Data
    public static class GeneralName {
        private String name;
        private String value;
    }

    @Data
    public static class ReportCertificate {
        private String notAfter;
        private boolean validSignature;
        private String serialNumber;
        private boolean expired;
        private String issuerName;
        private boolean online;
        private boolean revoked;
        private Crl crl;
        private String notBefore;
        private String subjectName;
    }

    @Data
    public static class Crl {
        private boolean validSignature;
        private String serialNumber;
        private String issuerName;
        private boolean online;
        private CrlDate dates;
    }

    @Data
    public static class CrlDate {
        private String notBefore;
        private String subjectName;
    }
}
