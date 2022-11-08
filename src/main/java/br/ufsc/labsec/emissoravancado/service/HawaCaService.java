package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.CNHInfo;
import br.ufsc.labsec.emissoravancado.components.enums.HawaCaEndpoints;
import br.ufsc.labsec.emissoravancado.errorHandlers.VerifierResponseErrorHandler;
import br.ufsc.labsec.valueobject.crypto.noncmc.CertificateResponse;
import br.ufsc.labsec.valueobject.dto.CertificateApplicationDTO;
import br.ufsc.labsec.valueobject.dto.NoCsrIssuingRequest;
import br.ufsc.labsec.valueobject.xmlmapping.submission.CertificateIntentionEnum;
import br.ufsc.labsec.valueobject.xmlmapping.submission.CertificatePoliciesEnum;
import br.ufsc.labsec.valueobject.xmlmapping.submission.CertificateProfileEnum;
import br.ufsc.labsec.valueobject.xmlmapping.submission.ValidationRequirements;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HawaCaService {
    private final String hawaAddress;
    private final String city;
    private final String state;
    private final KeyService keyService;
    private final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private final RestTemplate restTemplate = new RestTemplate();

    {
        restTemplate.setErrorHandler(new VerifierResponseErrorHandler());
    }

    public HawaCaService(
            @Value("${hawa-ca-service.address}") String hawaAddress,
            @Value("${hawa-ca-service.city}") String city,
            @Value("${hawa-ca-service.state}") String state,
            KeyService keyService) {
        this.hawaAddress = hawaAddress;
        this.city = city;
        this.state = state;
        this.keyService = keyService;
    }

    private NoCsrIssuingRequest createNoCsrIssuingRequest(
            CNHInfo cnhInfo, String encodedPublicKey) {
        NoCsrIssuingRequest noCsrIssuingRequest = new NoCsrIssuingRequest();
        noCsrIssuingRequest.setCertificateApplicationDTO(createCertificateApplicationDTO(cnhInfo));
        noCsrIssuingRequest.setEncodedPublicKey(encodedPublicKey);
        return noCsrIssuingRequest;
    }

    private CertificateApplicationDTO createCertificateApplicationDTO(CNHInfo cnhInfo) {
        CertificateApplicationDTO certificateApplicationDTO = new CertificateApplicationDTO();
        certificateApplicationDTO.setName(cnhInfo.getName());
        certificateApplicationDTO.setCity(city);
        certificateApplicationDTO.setState(state);
        certificateApplicationDTO.setCertificateType(CertificatePoliciesEnum.A1_A);
        certificateApplicationDTO.setBirthday(cnhInfo.getBirthDate());
        certificateApplicationDTO.setCpf(cnhInfo.getCpf());
        certificateApplicationDTO.setCertificateProfile(CertificateProfileEnum.NATURAL_PERSON);
        certificateApplicationDTO.setCertificateIntention(CertificateIntentionEnum.SOFTWARE);
        certificateApplicationDTO.setValidationRequirements(ValidationRequirements.NONE);
        return certificateApplicationDTO;
    }

    public CertificateResponse issueCertificateWithoutCsr(CNHInfo cnhInfo)
            throws NoSuchAlgorithmException, IOException {
        KeyPair keyPair = keyService.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        String b64PubKey = keyService.convertKeyToB64(publicKey);
        NoCsrIssuingRequest noCsrIssuingRequest = createNoCsrIssuingRequest(cnhInfo, b64PubKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(GSON.toJson(noCsrIssuingRequest), headers);
        ResponseEntity<CertificateResponse> response =
                this.restTemplate.postForEntity(
                        this.hawaAddress.concat(HawaCaEndpoints.NO_CMC_SIGN_NO_CSR.getEndpoint()),
                        request,
                        CertificateResponse.class);

        return response.getBody();
    }

    public String convertCertPEMToB64(X509CertificateHolder certificateHolder) throws IOException {
        return Base64.getEncoder().encodeToString(certificateHolder.getEncoded());
    }

    public X509CertificateHolder convertResponseToCertHolder(CertificateResponse certificateResponse) throws IOException {
        ByteArrayInputStream pemStream =
                new ByteArrayInputStream(
                        certificateResponse.getCertificatePem().getBytes(StandardCharsets.UTF_8));
        Reader pemReader = new BufferedReader(new InputStreamReader(pemStream));
        PEMParser pemParser = new PEMParser(pemReader);
        return  (X509CertificateHolder) pemParser.readObject();
    }
}
