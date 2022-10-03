/* (C)2022 */
package br.ufsc.labsec.emissoravancado.controller;

import br.ufsc.labsec.emissoravancado.service.CNHService;
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

    @Autowired
    public CNHController(CNHService cnhService) {
        this.cnhService = cnhService;
    }

    @PostMapping("/issue")
    public ResponseEntity<Resource> issueCertificate(@RequestParam("file") MultipartFile file) {
        this.cnhService.issueAdvancedCertificate(file);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + "imagine-um-cert.pdf" + "\"")
                .body(file.getResource());
    }
}
