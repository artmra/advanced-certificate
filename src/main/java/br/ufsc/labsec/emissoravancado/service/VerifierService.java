package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponse;
import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponseWithMultiSubAltName;
import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponseWithSingleSubAltName;
import br.ufsc.labsec.emissoravancado.errorHandlers.VerifierResponseErrorHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
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

    private String uri;
    private final RestTemplate restTemplate = new RestTemplate();

    private final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    {
        restTemplate.setErrorHandler(new VerifierResponseErrorHandler());
    }

    public VerifierService(@Value("${cnh.verifier}") String uri) {
        this.uri = uri;
    }

    public VerifierResponse verifyPDF(Resource cnh) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("verify_incremental_updates", false);
        requestBody.add("extended_report", true);
        requestBody.add("report_type", "json");
        requestBody.add("signature_files[]", cnh);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response =
                this.restTemplate.postForEntity(this.uri, request, String.class);
        VerifierResponse verifierResponse;
        try {
            verifierResponse =
                    GSON.fromJson(response.getBody(), VerifierResponseWithMultiSubAltName.class);
        } catch (JsonSyntaxException ex) {
            verifierResponse =
                    GSON.fromJson(response.getBody(), VerifierResponseWithSingleSubAltName.class);
        }
        return verifierResponse;
    }
}
