package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponse;
import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponseWithMultiSubAltName;
import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponseWithSingleSubAltName;
import br.ufsc.labsec.emissoravancado.errorHandlers.VerifierResponseErrorHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class VerifierService {

    private final String uri;
    private static final String VERIFY_INCREMENTAL_UPDATES = "verify_incremental_updates";
    private static final String EXTENDED_REPORT = "extended_report";
    private static final String REPORT_TYPE = "report_type";
    private static final String JSON = "json";
    private static final String SIGNATURE_FILES = "signature_files[]";
    private final RestTemplate restTemplate = new RestTemplate();

    private final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    {
        restTemplate.setErrorHandler(new VerifierResponseErrorHandler());
    }

    public VerifierService(@Value("${verifier-service.address}") String uri) {
        this.uri = uri;
    }

    public VerifierResponse verifyPDF(Resource cnh) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();

        requestBody.add(VERIFY_INCREMENTAL_UPDATES, false);
        requestBody.add(EXTENDED_REPORT, true);
        requestBody.add(REPORT_TYPE, JSON);
        requestBody.add(SIGNATURE_FILES, cnh);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response =
                this.restTemplate.postForEntity(this.uri, request, String.class);
        VerifierResponse verifierResponseFromJson = getVerifierResponseFromJson(response.getBody());
        checkSignatureAndValidity(verifierResponseFromJson);
        return verifierResponseFromJson;
    }

    public VerifierResponse getVerifierResponseFromJson(String json) {
        VerifierResponse verifierResponse;
        try {
            verifierResponse = GSON.fromJson(json, VerifierResponseWithMultiSubAltName.class);
        } catch (JsonSyntaxException ex) {
            verifierResponse = GSON.fromJson(json, VerifierResponseWithSingleSubAltName.class);
        }
        return verifierResponse;
    }

    public void checkSignatureAndValidity(VerifierResponse verifierResponse) {
        if (verifierResponse instanceof VerifierResponseWithMultiSubAltName) {
            var parsedResponse = (VerifierResponseWithMultiSubAltName) verifierResponse;
            List<VerifierResponseWithMultiSubAltName.ReportCertificate> certificates =
                    parsedResponse
                            .getReport()
                            .getSignatures()
                            .getSignature()
                            .getCertification()
                            .getSigner()
                            .getCertificate();
            for (var certificate : certificates) {
                if (!certificate.isValidSignature())
                    throw new RuntimeException(
                            "A cadeia de certificação possui um certificado com assinatura"
                                    + " inválida");
                if (certificate.isExpired())
                    throw new RuntimeException(
                            "A cadeia de certificação possui um certificado expirado");
                if (certificate.isRevoked())
                    throw new RuntimeException(
                            "A cadeia de certificação possui um certificado revogado");
            }
            return;
        }
        var parsedResponse = (VerifierResponseWithSingleSubAltName) verifierResponse;
        List<VerifierResponseWithSingleSubAltName.ReportCertificate> certificates =
                parsedResponse
                        .getReport()
                        .getSignatures()
                        .getSignature()
                        .getCertification()
                        .getSigner()
                        .getCertificate();
        for (var certificate : certificates) {
            if (!certificate.isValidSignature())
                throw new RuntimeException(
                        "A cadeia de certificação possui um certificado com assinatura inválida");
            if (certificate.isExpired())
                throw new RuntimeException(
                        "A cadeia de certificação possui um certificado expirado");
            if (certificate.isRevoked())
                throw new RuntimeException(
                        "A cadeia de certificação possui um certificado revogado");
        }
    }
}
