/* (C)2022 */
package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.CNHInfo;
import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponse;
import br.ufsc.labsec.emissoravancado.errorHandlers.VerifierResponseErrorHandler;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

    private String uri;
    private TesseractService ocrService;
    private PDFBoxService pdfBoxService;

    private final HawaCaService hawaCaService;
    private final RestTemplate restTemplate = new RestTemplate();

    {
        restTemplate.setErrorHandler(new VerifierResponseErrorHandler());
    }

    @Autowired
    public CNHService(
            TesseractService ocrService,
            PDFBoxService pdfBoxService,
            @Value("${cnh.verifier}") String uri,
            HawaCaService hawaCaService) {
        this.ocrService = ocrService;
        this.pdfBoxService = pdfBoxService;
        this.uri = uri;
        this.hawaCaService = hawaCaService;
    }

    public void issueAdvancedCertificate(MultipartFile file) {
        VerifierResponse verifierResponse = this.verifyPDF(file.getResource());
        try {
            byte[] bytes = file.getBytes();
            List<BufferedImage> pdImages = this.pdfBoxService.extractImages(bytes);
            CNHInfo cnhInfo = this.ocrService.extractData(pdImages);
            String certificate = this.hawaCaService.issueCertificateWithoutCsr(cnhInfo);
            //            this.pdfBoxService.saveImages(pdImages,
            // resource.getFilename().replace(".pdf", ""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private VerifierResponse verifyPDF(Resource cnh) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("verify_incremental_updates", false);
        requestBody.add("extended_report", true);
        requestBody.add("report_type", "json");
        requestBody.add("signature_files[]", cnh);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<VerifierResponse> response =
                this.restTemplate.postForEntity(this.uri, request, VerifierResponse.class);
        return response.getBody();
    }
}
