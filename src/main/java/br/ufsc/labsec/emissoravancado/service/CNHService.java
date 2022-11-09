/* (C)2022 */
package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.CNHInfo;
import br.ufsc.labsec.emissoravancado.dto.response.CNHServiceResponse;
import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponse;
import br.ufsc.labsec.emissoravancado.persistence.mysql.certificate.CertificateEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.certificate.CertificateRepository;
import br.ufsc.labsec.emissoravancado.persistence.mysql.client.ClientEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.client.ClientRepository;
import br.ufsc.labsec.emissoravancado.persistence.mysql.document.DocumentEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.document.DocumentRepository;
import br.ufsc.labsec.emissoravancado.persistence.mysql.document.DocumentTypeEnum;
import br.ufsc.labsec.emissoravancado.persistence.mysql.dossier.DossierEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.dossier.DossierRepository;
import br.ufsc.labsec.emissoravancado.persistence.mysql.keyPair.KeyPairEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.keyPair.KeyPairRepository;
import br.ufsc.labsec.valueobject.crypto.noncmc.CertificateResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Optional;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import net.sourceforge.tess4j.TesseractException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Service
public class CNHService {

    private TesseractService ocrService;
    private PDFBoxService pdfBoxService;
    private VerifierService verifierService;
    private KeyService keyService;
    private DossierSignerService dossierSignerService;
    private DocumentEncodeAndDecodeService documentEncodeAndDecodeService;
    private ClientRepository clientRepository;
    private DocumentRepository documentRepository;
    private CertificateRepository certificateRepository;
    private DossierRepository dossierRepository;
    private KeyPairRepository keyPairRepository;
    private final HawaCaService hawaCaService;
    private static final String FILENAME_TEMPLATE = "%s-%s";

    @Autowired
    public CNHService(
            TesseractService ocrService,
            PDFBoxService pdfBoxService,
            VerifierService verifierService,
            KeyService keyService,
            DossierSignerService dossierSignerService,
            DocumentEncodeAndDecodeService documentEncodeAndDecodeService,
            ClientRepository clientRepository,
            DocumentRepository documentRepository,
            CertificateRepository certificateRepository,
            DossierRepository dossierRepository,
            KeyPairRepository keyPairRepository,
            HawaCaService hawaCaService) {
        this.ocrService = ocrService;
        this.pdfBoxService = pdfBoxService;
        this.verifierService = verifierService;
        this.keyService = keyService;
        this.dossierSignerService = dossierSignerService;
        this.documentEncodeAndDecodeService = documentEncodeAndDecodeService;
        this.clientRepository = clientRepository;
        this.documentRepository = documentRepository;
        this.certificateRepository = certificateRepository;
        this.dossierRepository = dossierRepository;
        this.keyPairRepository = keyPairRepository;
        this.hawaCaService = hawaCaService;
    }

    public CNHServiceResponse issueAdvancedCertificate(MultipartFile file)
            throws IOException, TesseractException, NoSuchAlgorithmException,
                    ParserConfigurationException, TransformerException, MarshalException,
                    InvalidAlgorithmParameterException, UnrecoverableEntryException,
                    CertificateException, KeyStoreException, XMLSignatureException, SAXException {
        // verificar assinatura
        VerifierResponse verifierResponse = this.verifierService.verifyPDF(file.getResource());
        // tenta extrair as imagens do pdf com pdfBox; se algo der errado retornar erro
        List<BufferedImage> pdImages = this.pdfBoxService.extractImages(file.getBytes());
        // extrai os dados das imagens ussando tesseract; se algo der errado retornar erro
        CNHInfo extratedCnhInfo = this.ocrService.extractData(pdImages);
        // verifica se o usuário já existe no sistema; caso não cria o usuário e prossegue
        Optional<ClientEntity> byCpf = this.clientRepository.findByCpf(extratedCnhInfo.getCpf());
        ClientEntity client =
                byCpf.orElseGet(
                        () ->
                                this.clientRepository.save(
                                        new ClientEntity(extratedCnhInfo.getCpf(), "")));
        // gera chave para emitir certificado
        KeyPairEntity keyPair = keyService.createKeyPairEntity();
        // tenta emitir certificado com hawa; se algo der errado retornar erro
        CertificateResponse certificateResponse =
                this.hawaCaService.issueCertificateWithoutCsr(
                        extratedCnhInfo, keyPair.getB64PublicKey());
        // gera dossie e o assina;
        String cnhB64 = this.documentEncodeAndDecodeService.encodeToB64(file.getBytes());
        String extractedCnhInfoB64 =
                this.documentEncodeAndDecodeService.encodeToB64(extratedCnhInfo);
        String verifierResponseB64 =
                this.documentEncodeAndDecodeService.encodeToB64(verifierResponse);
        X509CertificateHolder certificateHolder =
                this.hawaCaService.convertResponseToCertHolder(certificateResponse);
        String certB64 = this.hawaCaService.convertCertPEMToB64(certificateHolder);

        String baseDossier =
                this.dossierSignerService.createBaseDossier(
                        extractedCnhInfoB64, cnhB64, verifierResponseB64, certB64);
        String signedXml = this.dossierSignerService.sign(baseDossier);
        // persiste dossie
        DossierEntity dossier = this.dossierRepository.save(new DossierEntity(signedXml));
        // persiste documentos
        DocumentEntity cnhPdfDoc =
                this.documentRepository.save(
                        new DocumentEntity(
                                cnhB64,
                                DocumentTypeEnum.CNH,
                                extratedCnhInfo.createFilename(
                                        FILENAME_TEMPLATE, DocumentTypeEnum.CNH),
                                dossier));
        DocumentEntity extractedInfoDoc =
                this.documentRepository.save(
                        new DocumentEntity(
                                extractedCnhInfoB64,
                                DocumentTypeEnum.CNH_INFO,
                                extratedCnhInfo.createFilename(
                                        FILENAME_TEMPLATE, DocumentTypeEnum.CNH_INFO),
                                dossier));
        DocumentEntity verifierResponseDoc =
                this.documentRepository.save(
                        new DocumentEntity(
                                verifierResponseB64,
                                DocumentTypeEnum.VERIFIER_REPORT,
                                extratedCnhInfo.createFilename(
                                        FILENAME_TEMPLATE, DocumentTypeEnum.VERIFIER_REPORT),
                                dossier));
        // persiste chave
        keyPair = this.keyPairRepository.save(keyPair);
        // persiste certificado
        CertificateEntity certificate =
                this.certificateRepository.save(
                        new CertificateEntity(
                                certB64,
                                false,
                                certificateHolder.getSerialNumber().toString(),
                                client,
                                dossier,
                                keyPair));
        // atualiza a coluna lastSerialNumber do usuário
        client.setLastCertificateSerialNumber(certificateHolder.getSerialNumber().toString());
        ClientEntity save = this.clientRepository.save(client);
        // cria objeto de resposta e retorna o certificado
        return new CNHServiceResponse(certB64, certificateHolder.getSerialNumber().toString());
    }
}
