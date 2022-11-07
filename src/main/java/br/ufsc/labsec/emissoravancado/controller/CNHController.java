/* (C)2022 */
package br.ufsc.labsec.emissoravancado.controller;

import br.ufsc.labsec.emissoravancado.service.CNHService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/cnh")
public class CNHController {

    private final CNHService cnhService;

    @Autowired
    public CNHController(CNHService cnhService) {
        this.cnhService = cnhService;
    }

    @SneakyThrows
    @PostMapping("/issue")
    public ResponseEntity<String> issueCertificate(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            // todo: loggar erro e retornar algo
        }
        file.getResource();
        String s = this.cnhService.issueAdvancedCertificate(file);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + "imagine-um-cert.pdf" + "\"")
                .body(s);
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
