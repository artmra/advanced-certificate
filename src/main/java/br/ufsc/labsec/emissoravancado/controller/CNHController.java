/* (C)2022 */
package br.ufsc.labsec.emissoravancado.controller;

import br.ufsc.labsec.emissoravancado.components.enums.PemEnum;
import br.ufsc.labsec.emissoravancado.service.CNHService;
import br.ufsc.labsec.emissoravancado.service.KeyService;
import java.security.KeyPair;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/cnh")
public class CNHController {

    private final CNHService cnhService;
    private final KeyService keyService;

    @Autowired
    public CNHController(CNHService cnhService, KeyService keyService) {
        this.cnhService = cnhService;
        this.keyService = keyService;
    }

    @SneakyThrows
    @PostMapping("/issue")
    public ResponseEntity<String> issueCertificate(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            // todo: retornar erro
        }
        String s = this.cnhService.issueAdvancedCertificate(file);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + "imagine-um-cert.pdf" + "\"")
                .body(s);
    }

//    @SneakyThrows
//    @PostMapping("/issue")
//    public ResponseEntity<Resource> issueCertificate(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            // todo: retornar erro
//        }
//        this.cnhService.issueAdvancedCertificate(file);
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(file.getContentType()))
//                .header(
//                        HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + "imagine-um-cert.pdf" + "\"")
//                .body(file.getResource());
//    }
}
