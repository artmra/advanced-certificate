package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.CNHInfo;
import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponseWithMultiSubAltName;
import br.ufsc.labsec.emissoravancado.dto.response.VerifierResponseWithSingleSubAltName;
import br.ufsc.labsec.emissoravancado.persistence.mysql.document.DocumentEntity;
import br.ufsc.labsec.emissoravancado.persistence.mysql.document.DocumentTypeEnum;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

@Service
public class DocumentEncodeAndDecodeService {
    private static final Base64.Encoder B64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder B64_DECODER = Base64.getDecoder();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public String encodeToB64(File cnh) throws IOException {
        byte[] documentBytes = FileUtils.readFileToByteArray(cnh);
        return B64_ENCODER.encodeToString(documentBytes);
    }

    public String encodeToB64(
            VerifierResponseWithMultiSubAltName verifierResponseWithMultiSubAltName) {
        String json = GSON.toJson(verifierResponseWithMultiSubAltName);
        return B64_ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    public String encodeToB64(
            VerifierResponseWithSingleSubAltName verifierResponseWithSingleSubAltName) {
        String json = GSON.toJson(verifierResponseWithSingleSubAltName);
        return B64_ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    public String encodeToB64(CNHInfo cnhInfo) {
        String json = GSON.toJson(cnhInfo);
        return B64_ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    public File loadCNHFileFromB64(DocumentEntity document) throws IOException {
        DocumentTypeEnum documentTypeEnum = DocumentTypeEnum.valueOf(document.getDocumentType());
        if (documentTypeEnum != DocumentTypeEnum.CNH)
            throw new RuntimeException("Tipo de documento inválido");
        byte[] documentBytes = B64_DECODER.decode(document.getB64Encoded());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(documentBytes);
        Path tempFile =
                Files.createTempFile(
                        document.getFilename(), DocumentTypeEnum.getFileSufix(documentTypeEnum));
        File file = new File(tempFile.toUri());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        IOUtils.copy(byteArrayInputStream, fileOutputStream);
        return file;
    }

    public String loadJsonFromB64(DocumentEntity document) {
        DocumentTypeEnum documentTypeEnum = DocumentTypeEnum.valueOf(document.getDocumentType());
        if (documentTypeEnum == DocumentTypeEnum.CNH_INFO
                || documentTypeEnum == DocumentTypeEnum.VERIFIER_REPORT) {
            byte[] decodedDocumentBytes = B64_DECODER.decode(document.getB64Encoded());
            return new String(decodedDocumentBytes);
        }
        throw new RuntimeException("Tipo de documento inválido");
    }
}
