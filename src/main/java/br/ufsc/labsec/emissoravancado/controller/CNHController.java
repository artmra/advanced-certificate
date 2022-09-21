package br.ufsc.labsec.emissoravancado.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/cnh")
public class CNHController {
    public CNHController() {}

    @PostMapping("/issue")
    public ResponseEntity <Resource> issueCertificate(@RequestParam("file") MultipartFile file) {
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "imagine-um-cert.pdf" + "\"")
                .body(file.getResource());
    }
}
