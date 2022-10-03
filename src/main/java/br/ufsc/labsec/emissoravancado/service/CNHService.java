/* (C)2022 */
package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CNHService {

    @Value("${cnh.verifier}")
    private String uri;

    private final String uri_1 = "https://verificador.iti.gov.br/verifier-2.8.1/report";
    private final String uri_2 = "https://verificador.iti.br/report";
    private final String uri_3 = "https://pbad.labsec.ufsc.br/verifier-hom/report";
    private final RestTemplate restTemplate = new RestTemplate();

    public CNHService() {}

    public void issueAdvancedCertificate(MultipartFile file) {
        VerifierResponse verifierResponse = this.verifyPDF(file.getResource());
    }

    private VerifierResponse verifyPDF(Resource cnh) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("verify_incremental_updates", false);
        requestBody.add("extended_report", true);
        requestBody.add("report_type", "json");
        requestBody.add("signature_files[]", cnh);

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(requestBody, headers);
        ResponseEntity<VerifierResponse> response =
                this.restTemplate.postForEntity(this.uri, requestEntity, VerifierResponse.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // todo: tratar erro
        }
        return response.getBody();
    }

    private void extractData(Resource cnh) {
        System.out.println("bk");
    }
}
