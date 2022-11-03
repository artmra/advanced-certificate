/* (C)2022 */
package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.CNHInfo;
import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CNHService {

    private TesseractService ocrService;
    private PDFBoxService pdfBoxService;
    private VerifierService verifierService;
    private final HawaCaService hawaCaService;

    @Autowired
    public CNHService(
            TesseractService ocrService,
            PDFBoxService pdfBoxService,
            VerifierService verifierService,
            HawaCaService hawaCaService) {
        this.ocrService = ocrService;
        this.pdfBoxService = pdfBoxService;
        this.verifierService = verifierService;
        this.hawaCaService = hawaCaService;
    }

    public String issueAdvancedCertificate(MultipartFile file)
            throws NoSuchAlgorithmException, IOException, TesseractException {
        VerifierResponse verifierResponse = this.verifierService.verifyPDF(file.getResource());
        List<BufferedImage> pdImages = this.pdfBoxService.extractImages(file.getBytes());
        CNHInfo cnhInfo = this.ocrService.extractData(pdImages);
        return this.hawaCaService.issueCertificateWithoutCsr(cnhInfo);
    }
}
