package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.enums.KeyTypeEnum;
import br.ufsc.labsec.emissoravancado.components.enums.PemEnum;
import br.ufsc.labsec.emissoravancado.persistence.mysql.keyPair.KeyPairEntity;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
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
    private static final String PBE_ALGORITHM = "PBEWithSHA1AndDESede";
    private static final int ITERATION_COUNT = 1000; // hash iteration count

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

    public String convertKeyToB64(Key key, String keyPassword)
            throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException,
                    IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException,
                    InvalidParameterSpecException, BadPaddingException, InvalidKeyException,
                    NoSuchProviderException {
        byte[] encodedKey =
                key instanceof PrivateKey
                        ? encryptKey((PrivateKey) key, keyPassword)
                        : key.getEncoded();
        return Base64.getEncoder().encodeToString(encodedKey);
    }

    public KeyPairEntity createKeyPairEntity(String keyPassword)
            throws NoSuchAlgorithmException, IOException, NoSuchPaddingException,
                    InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
                    InvalidAlgorithmParameterException, InvalidKeySpecException,
                    InvalidParameterSpecException, NoSuchProviderException {

        KeyPair keyPair = this.generateKeyPair();
        String b64EncryptedPrivateKey = this.convertKeyToB64(keyPair.getPrivate(), keyPassword);
        String b64PublicKey = this.convertKeyToB64(keyPair.getPublic(), keyPassword);
        return new KeyPairEntity(b64EncryptedPrivateKey, b64PublicKey);
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

    private byte[] encryptKey(PrivateKey privateKey, String keyPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
                    InvalidAlgorithmParameterException, InvalidKeyException,
                    InvalidParameterSpecException, IOException, IllegalBlockSizeException,
                    BadPaddingException {
        byte[] salt = new byte[8];
        new SecureRandom().nextBytes(salt);
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(keyPassword.toCharArray());
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PBE_ALGORITHM);
        SecretKey pbeKey = secretKeyFactory.generateSecret(pbeKeySpec);
        Cipher cipher = Cipher.getInstance(PBE_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParameterSpec);
        byte[] encryptedKeyBytes = cipher.doFinal(privateKey.getEncoded());
        AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(PBE_ALGORITHM);
        algorithmParameters.init(pbeParameterSpec);
        EncryptedPrivateKeyInfo encinfo =
                new EncryptedPrivateKeyInfo(algorithmParameters, encryptedKeyBytes);
        return encinfo.getEncoded();
    }

    public PrivateKey decryptPrivateKey(String b64EncryptedPrivateKey, String keyPassword)
            throws InvalidKeySpecException, NoSuchAlgorithmException,
                    InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException,
                    IOException {
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo =
                new EncryptedPrivateKeyInfo(Base64.getDecoder().decode(b64EncryptedPrivateKey));
        Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
        PBEKeySpec pbeKeySpec = new PBEKeySpec(keyPassword.toCharArray());
        SecretKeyFactory secretKeyFactory =
                SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
        Key privateKey = secretKeyFactory.generateSecret(pbeKeySpec);
        AlgorithmParameters algorithmParameters = encryptedPrivateKeyInfo.getAlgParameters();
        cipher.init(Cipher.DECRYPT_MODE, privateKey, algorithmParameters);
        KeySpec keySpec = encryptedPrivateKeyInfo.getKeySpec(cipher);
        KeyFactory keyFactory = KeyFactory.getInstance(this.keyAlgorithm.getName());
        return keyFactory.generatePrivate(keySpec);
    }
}
