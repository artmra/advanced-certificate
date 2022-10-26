package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.enums.KeyTypeEnum;
import br.ufsc.labsec.emissoravancado.components.enums.PemEnum;
import java.io.*;
import java.security.*;
import java.util.Base64;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeyService {

    private final int keySize;
    private final KeyTypeEnum keyAlgorithm;

    public KeyService(
            @Value("${key-service.key-size}") int keySize,
            @Value("${key-service.key-algorithm}") String keyAlgorithm) {
        this.keySize = keySize;
        this.keyAlgorithm = KeyTypeEnum.valueOf(keyAlgorithm.toUpperCase());
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator =
                KeyPairGenerator.getInstance(keyAlgorithm.getName(), new BouncyCastleProvider());
        keyPairGenerator.initialize(keySize, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    public String convertKeyToB64(Key key) throws IOException {
        byte[] encodedKey = key.getEncoded();
        return Base64.getEncoder().encodeToString(encodedKey);
    }

    public String writeKeyToPEMString(Key key, PemEnum header)
            throws IOException, RuntimeException {
        if (header != PemEnum.PRIVATE_KEY && header != PemEnum.PUBLIC_KEY) {
            throw new RuntimeException(
                    "O encoding de objetos que não sejam chaves não é suportado nesse método");
        }
        PemObject keyPem = new PemObject(header.getHeader(), key.getEncoded());
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        try {
            pemWriter.writeObject(keyPem);
        } finally {
            pemWriter.close();
        }
        return stringWriter.toString();
    }

    public PrivateKey loadPrivKeyFromPEMString(String privKeyFile) throws IOException {
        StringReader keyReader = new StringReader(privKeyFile);
        PEMParser pemParser = new PEMParser(keyReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());
        PrivateKey privateKey = converter.getPrivateKey(privateKeyInfo);

        return converter.getPrivateKey(privateKeyInfo);
    }

    public PublicKey loadPubKeyFromPEMString(String pubKeyFile) throws IOException {
        StringReader keyReader = new StringReader(pubKeyFile);
        PEMParser pemParser = new PEMParser(keyReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        SubjectPublicKeyInfo publicKeyInfo =
                SubjectPublicKeyInfo.getInstance(pemParser.readObject());

        return converter.getPublicKey(publicKeyInfo);
    }

    public String signCertificationRequestInfo(
            PrivateKey privateKey, PublicKey publicKey, String certReqInfoB64)
            throws InvalidKeyException, NoSuchAlgorithmException, IOException, SignatureException {
        // sign bytes of CERT_REQ_INFO_B64
        Signature signature = Signature.getInstance("SHA256WithRSA", new BouncyCastleProvider());
        signature.initSign(privateKey);
        CertificationRequestInfo certificationRequestInfo =
                CertificationRequestInfo.getInstance(Base64.getDecoder().decode(certReqInfoB64));
        signature.update(certificationRequestInfo.getEncoded());
        byte[] resultBytesCertReqInfo = signature.sign();
        // validate the result
        return verifySignature(
                signature,
                certificationRequestInfo.getEncoded(),
                publicKey,
                resultBytesCertReqInfo);
    }

    public String signCertificationRequestInfoDigest(
            PrivateKey privateKey, PublicKey publicKey, String certReqInfoDigestB64)
            throws InvalidKeyException, NoSuchAlgorithmException, IOException, SignatureException {
        // sign bytes of CERT_REQ_INFO_DIGEST_B64
        Signature signature = Signature.getInstance("RSA", new BouncyCastleProvider());
        signature.initSign(privateKey);
        DigestInfo digestInfo =
                DigestInfo.getInstance(Base64.getDecoder().decode(certReqInfoDigestB64));
        signature.update(digestInfo.getDigest());
        byte[] resultBytesCertReqInfoDigest = signature.sign();
        // validate the result
        return this.verifySignature(
                signature, digestInfo.getDigest(), publicKey, resultBytesCertReqInfoDigest);
    }

    public String verifySignature(
            Signature signature, byte[] originalContent, PublicKey publicKey, byte[] signedContent)
            throws InvalidKeyException, SignatureException {
        signature.initVerify(publicKey);
        signature.update(originalContent);
        if (signature.verify(signedContent)) {
            return new String(Base64.getEncoder().encode(signedContent));
        }

        throw new RuntimeException("A assinatura não pode ser validada.");
    }
}
