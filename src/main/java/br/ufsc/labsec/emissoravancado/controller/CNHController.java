/* (C)2022 */
package br.ufsc.labsec.emissoravancado.controller;

import br.ufsc.labsec.emissoravancado.dto.response.CNHServiceResponse;
import br.ufsc.labsec.emissoravancado.service.CNHService;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import lombok.SneakyThrows;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@RestController
@RequestMapping("/cnh")
public class CNHController {

    private final CNHService cnhService;

    @Autowired
    public CNHController(CNHService cnhService) {
        this.cnhService = cnhService;
    }

    @PostMapping("/issue")
    public ResponseEntity<CNHServiceResponse> issueCertificate(
            @RequestParam("file") MultipartFile file)
            throws MarshalException, InvalidAlgorithmParameterException, TesseractException,
                    CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException,
                    SAXException, UnrecoverableEntryException, ParserConfigurationException,
                    XMLSignatureException, TransformerException {
        if (file.isEmpty()) {
            // todo: loggar erro e retornar algo
        }
        CNHServiceResponse cnhServiceResponse = this.cnhService.issueAdvancedCertificate(file);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(cnhServiceResponse);
    }

    @SneakyThrows
    @PostMapping("/revoke")
    public void revokeCertificate() {}

    @SneakyThrows
    @PostMapping("/get-cert")
    public void getCertificate() {}

    @SneakyThrows
    @GetMapping("/get-cnh")
    public ResponseEntity<byte[]> getCnh(@RequestParam("serial-number") String serialNumber) {

        byte[] fileBytes = new byte[0];
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + "imagine-um-cert.pdf" + "\"")
                .body(fileBytes);
    }
}
