/* (C)2022 */
package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.CNHInfo;
import br.ufsc.labsec.emissoravancado.dto.response.CNHServiceResponse;
import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import br.ufsc.labsec.emissoravancado.persistence.mysql.certificate.CertificateRepository;
import br.ufsc.labsec.emissoravancado.persistence.mysql.client.ClientEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.client.ClientRepository;
import br.ufsc.labsec.emissoravancado.persistence.mysql.document.DocumentRepository;
import br.ufsc.labsec.emissoravancado.persistence.mysql.dossier.DossierRepository;
import br.ufsc.labsec.emissoravancado.persistence.mysql.keyPair.KeyPairRepository;
import br.ufsc.labsec.valueobject.crypto.noncmc.CertificateResponse;
import net.sourceforge.tess4j.TesseractException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CNHService {

    private TesseractService ocrService;
    private PDFBoxService pdfBoxService;
    private VerifierService verifierService;

    private ClientRepository clientRepository;
    private DocumentRepository documentRepository;
    private CertificateRepository certificateRepository;
    private DossierRepository dossierRepository;
    private KeyPairRepository keyPairRepository;
    private final HawaCaService hawaCaService;

    @Autowired
    public CNHService(
            TesseractService ocrService,
            PDFBoxService pdfBoxService,
            VerifierService verifierService,
            ClientRepository clientRepository, HawaCaService hawaCaService) {
        this.ocrService = ocrService;
        this.pdfBoxService = pdfBoxService;
        this.verifierService = verifierService;
        this.clientRepository = clientRepository;
        this.hawaCaService = hawaCaService;
    }

    public CNHServiceResponse issueAdvancedCertificate(MultipartFile file)
            throws NoSuchAlgorithmException, IOException, TesseractException {
        // verificar assinatura
        VerifierResponse verifierResponse = this.verifierService.verifyPDF(file.getResource());
        // tenta extrair as imagens do pdf com pdfBox; se algo der errado retornar erro
        List<BufferedImage> pdImages = this.pdfBoxService.extractImages(file.getBytes());
        // extrai os dados das imagens ussando tesseract; se algo der errado retornar erro
        CNHInfo cnhInfo = this.ocrService.extractData(pdImages);
        // verifica se o usuário já existe no sistema; caso não cria o usuário e prossegue
        Optional<ClientEntity> byCpf = this.clientRepository.findByCpf(cnhInfo.getCpf());
        ClientEntity client = byCpf.orElseGet(() -> this.clientRepository.save(new ClientEntity(cnhInfo.getCpf(), "")));
        // gera chave para emitir certificado
        // tenta emitir certificado com hawa; se algo der errado retornar erro
        // gera dossie e o assina;
        // persiste certificado
        // persiste chave
        // persiste documentos
        // atualiza a coluna lastSerialNumber do usuário
        // cria objeto de resposta e retorna o certificado

        CertificateResponse certificateResponse = this.hawaCaService.issueCertificateWithoutCsr(cnhInfo);
        X509CertificateHolder certificateHolder = this.hawaCaService.convertResponseToCertHolder(certificateResponse);
        String certB64 = this.hawaCaService.convertCertPEMToB64(certificateHolder);
        return new CNHServiceResponse(certB64, certificateHolder.getSerialNumber().toString());
    }
}
