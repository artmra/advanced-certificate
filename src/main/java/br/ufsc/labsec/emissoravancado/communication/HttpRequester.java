package br.ufsc.labsec.emissoravancado.communication;

import lombok.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;


public class HttpRequester {

    @Getter
    private final String uri;
    private final String uri_1 = "https://verificador.iti.gov.br/verifier-2.8.1/report";
    private final String uri_2 = "https://verificador.iti.br/report";
    private final String uri_3 = "https://pbad.labsec.ufsc.br/verifier-hom/report";
    private final RestTemplate restTemplate;

    public HttpRequester() {
        this.uri = uri_3;
        this.restTemplate = new RestTemplate();
    }

    public void post(Resource file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("verify_incremental_updates", false);
        body.add("extended_report", true);
        body.add("report_type", "json");
        body.add("signature_files", file);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = this.restTemplate.postForEntity(this.uri,requestEntity, String.class);
        System.out.println("bk");
    }
}
