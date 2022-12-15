/* (C)2022 */
package br.ufsc.labsec.emissoravancado.controller;

import br.ufsc.labsec.emissoravancado.dto.response.CNHServiceIssueResponse;
import br.ufsc.labsec.emissoravancado.dto.response.CNHServiceRevokeResponse;
import br.ufsc.labsec.emissoravancado.dto.response.SimpleMessageResponse;
import br.ufsc.labsec.emissoravancado.exception.errors.FileMissingException;
import br.ufsc.labsec.emissoravancado.exception.errors.InternalErrorException;
import br.ufsc.labsec.emissoravancado.exception.errors.VerifierUnavailableException;
import br.ufsc.labsec.emissoravancado.persistence.mysql.certificate.CertificateEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.document.DocumentTypeEnum;
import br.ufsc.labsec.emissoravancado.persistence.mysql.dossier.DossierEntity;
import br.ufsc.labsec.emissoravancado.service.CNHService;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.NoSuchElementException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@RestController
@RequestMapping("/cnh")
public class CNHController {

    private final CNHService cnhService;
    private static final String FILE_MISSING = "Arquivo ausente no corpo da requisicao";
    private static final String NO_SUCH_ELEMENT =
            "Não há certificado emitido com o serial number informado";
    private static final String INTERNAL_ERROR =
            "Erro interno; entre em contato com o administrador do sistema";
    private static final String VERIFIER_UNAVAILABLE =
            "O documento de CNH não pode ser validado; entre em contato com o administrador do"
                    + " sistema";
    private static final String TESSERACT_ERROR = "Não foi possível extrair os dados do documento";

    @Autowired
    public CNHController(CNHService cnhService) {
        this.cnhService = cnhService;
    }

    @PostMapping("/issue")
    public ResponseEntity<CNHServiceIssueResponse> issueCertificate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("key-password") String keyPassword)
            throws MarshalException, InvalidAlgorithmParameterException, TesseractException,
                    CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException,
                    SAXException, UnrecoverableEntryException, ParserConfigurationException,
                    XMLSignatureException, TransformerException, NoSuchPaddingException,
                    IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException,
                    InvalidParameterSpecException, NoSuchProviderException, InvalidKeyException {
        if (file.isEmpty()) {
            throw new FileMissingException(FILE_MISSING);
        }
        CertificateEntity certificate = this.cnhService.issueAdvancedCertificate(file, keyPassword);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new CNHServiceIssueResponse(
                                certificate.getB64Cert(), certificate.getSerialNumber()));
    }

    @PostMapping("/revoke")
    public ResponseEntity<CNHServiceRevokeResponse> revokeCertificate(
            @RequestParam("serial-number") String serialNumber) {
        CertificateEntity revokedCertificate = this.cnhService.revoke(serialNumber);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new CNHServiceRevokeResponse(
                                true,
                                revokedCertificate.getSerialNumber(),
                                revokedCertificate.getB64Cert(),
                                revokedCertificate.getRevocationDate()));
    }

    @GetMapping("/get-cert")
    public ResponseEntity<CNHServiceIssueResponse> getCertificate(
            @RequestParam("serial-number") String serialNumber) {
        CertificateEntity certificate = this.cnhService.getIssuedCertificate(serialNumber);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new CNHServiceIssueResponse(
                                certificate.getB64Cert(), certificate.getSerialNumber()));
    }

    @GetMapping("/get-cnh")
    public ResponseEntity<byte[]> getCnh(@RequestParam("serial-number") String serialNumber)
            throws InternalErrorException {
        byte[] cnhPdf = this.cnhService.getCnhPdf(serialNumber);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + "imagine-um-cert.pdf" + "\"")
                .body(cnhPdf);
    }

    @GetMapping("/get-verifier-response")
    public ResponseEntity<String> getVerifierResponse(
            @RequestParam("serial-number") String serialNumber) throws InternalErrorException {
        String verifierResponse =
                this.cnhService.getDocumentJson(serialNumber, DocumentTypeEnum.VERIFIER_REPORT);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE))
                .body(verifierResponse);
    }

    @GetMapping("/get-extracted-cnh-info")
    public ResponseEntity<String> getExtractedCnhInfo(
            @RequestParam("serial-number") String serialNumber) throws InternalErrorException {
        String verifierResponse =
                this.cnhService.getDocumentJson(serialNumber, DocumentTypeEnum.EXTRACTED_CNH_INFO);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE))
                .body(verifierResponse);
    }

    @GetMapping("/get-dossier")
    public ResponseEntity<String> getDossier(@RequestParam("serial-number") String serialNumber)
            throws InternalErrorException {
        DossierEntity dossier = this.cnhService.getDossier(serialNumber);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_XML_VALUE))
                .body(dossier.getXml());
    }

    @ExceptionHandler(FileMissingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SimpleMessageResponse> handleFileMissingException(
            FileMissingException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(SimpleMessageResponse.getFormatedMessage(FILE_MISSING));
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SimpleMessageResponse> handleNoSuchElementException(
            NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(SimpleMessageResponse.getFormatedMessage(NO_SUCH_ELEMENT));
    }

    @ExceptionHandler({
        InternalErrorException.class,
        MarshalException.class,
        InvalidAlgorithmParameterException.class,
        CertificateException.class,
        IOException.class,
        NoSuchAlgorithmException.class,
        KeyStoreException.class,
        SAXException.class,
        UnrecoverableEntryException.class,
        ParserConfigurationException.class,
        XMLSignatureException.class,
        TransformerException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<SimpleMessageResponse> handleInternalErrorException(
            InternalErrorException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(SimpleMessageResponse.getFormatedMessage(INTERNAL_ERROR));
    }

    @ExceptionHandler(VerifierUnavailableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<SimpleMessageResponse> handleVerifierUnavailableException(
            InternalErrorException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(SimpleMessageResponse.getFormatedMessage(VERIFIER_UNAVAILABLE));
    }

    @ExceptionHandler(TesseractException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SimpleMessageResponse> handleTesseractException(
            InternalErrorException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(SimpleMessageResponse.getFormatedMessage(TESSERACT_ERROR));
    }
}
