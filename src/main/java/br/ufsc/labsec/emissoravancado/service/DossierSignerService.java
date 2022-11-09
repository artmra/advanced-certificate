package br.ufsc.labsec.emissoravancado.service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

@Service
public class DossierSignerService {

    private static final String DOM = "DOM";
    private static final String JKS = "JKS";
    private static final String SHA_256 = "SHA-256";
    private static final String DOCUMENTS_KEY = "DigestDocumentos";
    private static final String PDF_CNH_B64_SHA_265_KEY = "CNH";
    private static final String EXTRACTED_CNH_INFO_B64_SHA_265_KEY = "InformacoesExtraidasCNH";
    private static final String CERT_B64_SHA_265_KEY = "CertificadoEmitido";
    private static final String VERIFIER_KEY = "VerificadorDeDocumentos";
    private static final String VERIFIER_ADDRESS_KEY = "URL";
    private static final String VERIFIER_REPORT_B64_SHA_265_KEY = "Report";
    private final String verifierAddress;
    private final String keyStoreFilename;
    private final String keyStorePassword;
    private final String keyEntry;
    private final String keyEntryPassword;

    public DossierSignerService(
            @Value("${dossier-service.key-store-filename}") String keyStoreFilename,
            @Value("${dossier-service.key-entry-password}") String keyEntryPassword,
            @Value("${dossier-service.key-entry}") String keyEntry,
            @Value("${dossier-service.key-store-password}") String keyStorePassword,
            @Value("${verifier-service.address}") String verifierAddress) {
        this.keyStoreFilename = keyStoreFilename;
        this.keyStorePassword = keyStorePassword;
        this.keyEntry = keyEntry;
        this.keyEntryPassword = keyEntryPassword;
        this.verifierAddress = verifierAddress;
    }

    public String createBaseDossier(
            String extractedcnhInfoB64, String cnhPdfB64, String verifierB64, String certB64)
            throws ParserConfigurationException, TransformerException, NoSuchAlgorithmException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element documentsB64 = document.createElement(DOCUMENTS_KEY);

        documentsB64.appendChild(
                createElement(document, PDF_CNH_B64_SHA_265_KEY, digestValue(cnhPdfB64)));
        documentsB64.appendChild(
                createElement(
                        document,
                        EXTRACTED_CNH_INFO_B64_SHA_265_KEY,
                        digestValue(extractedcnhInfoB64)));
        documentsB64.appendChild(createElement(document, CERT_B64_SHA_265_KEY, certB64));

        Element verifierElement = document.createElement(VERIFIER_KEY);

        verifierElement.appendChild(createElement(document, VERIFIER_ADDRESS_KEY, verifierAddress));
        verifierElement.appendChild(
                createElement(document, VERIFIER_REPORT_B64_SHA_265_KEY, digestValue(verifierB64)));

        documentsB64.appendChild(verifierElement);

        document.appendChild(documentsB64);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(byteArrayOutputStream);
        transformer.transform(domSource, streamResult);
        String baseReport = byteArrayOutputStream.toString(StandardCharsets.UTF_8);

        return baseReport;
    }

    private Element createElement(Document document, String xmlKey, String value) {
        Element element = document.createElement(xmlKey);
        element.appendChild(document.createTextNode(value));
        return element;
    }

    private String digestValue(String value) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance(SHA_256);
        byte[] digest = instance.digest(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }

    public String sign(String dossierXml)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, KeyStoreException,
                    IOException, CertificateException, UnrecoverableEntryException,
                    ParserConfigurationException, SAXException, MarshalException,
                    XMLSignatureException, TransformerException {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance(DOM);
        Reference ref =
                fac.newReference(
                        "",
                        fac.newDigestMethod(DigestMethod.SHA256, null),
                        Collections.singletonList(
                                fac.newTransform(
                                        Transform.ENVELOPED, (TransformParameterSpec) null)),
                        null,
                        null);

        SignedInfo si =
                fac.newSignedInfo(
                        fac.newCanonicalizationMethod(
                                CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                        fac.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                        Collections.singletonList(ref));

        KeyStore keyStore = KeyStore.getInstance(JKS);
        keyStore.load(new FileInputStream(this.keyStoreFilename), keyStorePassword.toCharArray());
        KeyStore.PrivateKeyEntry keyEntry =
                (KeyStore.PrivateKeyEntry)
                        keyStore.getEntry(
                                this.keyEntry,
                                new KeyStore.PasswordProtection(keyEntryPassword.toCharArray()));
        X509Certificate cert = (X509Certificate) keyEntry.getCertificate();

        KeyInfoFactory keyInfoFactory = fac.getKeyInfoFactory();
        List x509Content = new ArrayList();
        x509Content.add(cert.getSubjectX500Principal().getName());
        x509Content.add(cert);
        X509Data x509Data = keyInfoFactory.newX509Data(x509Content);
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Document document =
                documentBuilderFactory
                        .newDocumentBuilder()
                        .parse(IOUtils.toInputStream(dossierXml, StandardCharsets.UTF_8));

        DOMSignContext dsc =
                new DOMSignContext(keyEntry.getPrivateKey(), document.getDocumentElement());

        XMLSignature signature = fac.newXMLSignature(si, keyInfo);

        signature.sign(dsc);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));
        String result = byteArrayOutputStream.toString();
        return result;
    }
}
