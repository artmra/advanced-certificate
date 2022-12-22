package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.CNHInfo;
import br.ufsc.labsec.emissoravancado.components.LegacyCnhFormatInfoMapping;
import br.ufsc.labsec.emissoravancado.components.NewCnhFormatInfoMapping;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TesseractService {
    private static final String TESSERACT_LANGUAGE = "por";
    private static final String USER_DEFINED_DPI = "user_defined_dpi";
    private static final String DEFAULT_DPI = "100";
    private static final String CPF_REGEX = "[0-9]{11}";
    private static final String DATE_REGEX = "(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/(19|20)[0-9]{2}";
    private final ITesseract instance;

    @Autowired
    public TesseractService(
            @Value("${tesseract-service.datapath}") String tesseractDatapath,
            @Value("${tesseract-service.ocr-engine-mode}") int ocrEngineMode,
            @Value("${tesseract-service.page-segmentation-mode}") int pageSegmentationMode) {
        this.instance = new Tesseract();
        this.instance.setVariable(USER_DEFINED_DPI, DEFAULT_DPI);
        this.instance.setOcrEngineMode(ocrEngineMode);
        this.instance.setPageSegMode(pageSegmentationMode);
        this.instance.setDatapath(tesseractDatapath);
        this.instance.setLanguage(TESSERACT_LANGUAGE);
    }

    public CNHInfo extractData(List<BufferedImage> images) throws TesseractException {
        switch (images.size()) {
            case 3:
                return this.extractDataFromLegacyFormat(images.get(0), images.get(1));
            case 4:
                return this.extractDataFromLegacyFormat(images.get(1), images.get(2));
            case 5:
                return this.extractDataFromNewFormat(images.get(1), images.get(2));
            default:
                throw new RuntimeException("Formato de CNH digital não suportado");
        }
    }

    private CNHInfo extractDataFromLegacyFormat(BufferedImage firstImage, BufferedImage secondImage)
            throws TesseractException {
        var builder = CNHInfo.builder();
        // extract info from first page
        builder.name(doOCR(firstImage, LegacyCnhFormatInfoMapping.getNameArea()));
        builder.docInfo(doOCR(firstImage, LegacyCnhFormatInfoMapping.getIdDocInfoArea()));
        builder.cpf(formatCPF(doOCR(firstImage, LegacyCnhFormatInfoMapping.getCpfArea())));
        String birthDate = doOCR(firstImage, LegacyCnhFormatInfoMapping.getBirthDateArea());
        validateDate(birthDate);
        builder.birthDate(birthDate);
        builder.fatherName(doOCR(firstImage, LegacyCnhFormatInfoMapping.getFatherNameArea()));
        builder.motherName(doOCR(firstImage, LegacyCnhFormatInfoMapping.getMotherNameArea()));
        builder.cnh(doOCR(firstImage, LegacyCnhFormatInfoMapping.getRegisterNumberArea()));
        builder.validity(doOCR(firstImage, LegacyCnhFormatInfoMapping.getValidityArea()));
        builder.firstCNHDate(
                doOCR(firstImage, LegacyCnhFormatInfoMapping.getFirstDriverLicenceDateArea()));
        // extract info from second page
        builder.issuePlace(doOCR(secondImage, LegacyCnhFormatInfoMapping.getIssuePlaceArea()));
        builder.issueDate(doOCR(secondImage, LegacyCnhFormatInfoMapping.getIssueDateArea()));
        return builder.build();
    }

    private CNHInfo extractDataFromNewFormat(BufferedImage firstImage, BufferedImage secondImage)
            throws TesseractException {
        var builder = CNHInfo.builder();
        // extract info from first page
        builder.name(doOCR(firstImage, NewCnhFormatInfoMapping.getNameArea()));
        builder.docInfo(doOCR(firstImage, NewCnhFormatInfoMapping.getIdDocInfoArea()));
        builder.cpf(formatCPF(doOCR(firstImage, NewCnhFormatInfoMapping.getCpfArea())));
        String birthData = doOCR(firstImage, NewCnhFormatInfoMapping.getBirthInfoArea());
        builder.birthData(birthData);
        String birthDate = extractBirthDate(birthData);
        validateDate(birthDate);
        builder.birthDate(birthDate);
        builder.fatherName(doOCR(firstImage, NewCnhFormatInfoMapping.getFatherNameArea()));
        builder.motherName(doOCR(firstImage, NewCnhFormatInfoMapping.getMotherNameArea()));
        builder.cnh(doOCR(firstImage, NewCnhFormatInfoMapping.getRegisterNumberArea()));
        builder.nationality(doOCR(firstImage, NewCnhFormatInfoMapping.getNationalityArea()));
        builder.validity(doOCR(firstImage, NewCnhFormatInfoMapping.getValidityArea()));
        builder.firstCNHDate(
                doOCR(firstImage, NewCnhFormatInfoMapping.getFirstDriverLicenceDateArea()));
        builder.issueDate(doOCR(firstImage, NewCnhFormatInfoMapping.getIssueDateArea()));
        // extract info from second page
        builder.issuePlace(doOCR(secondImage, NewCnhFormatInfoMapping.getIssuePlaceArea()));
        return builder.build();
    }

    private String extractBirthDate(String birthData) {
        return birthData.substring(0, 10);
    }

    private String doOCR(BufferedImage image, Rectangle dataArea) throws TesseractException {
        String s = instance.doOCR(image, dataArea);
        s = s.replace("\n", "");
        return s;
    }

    private String formatCPF(String cpf) throws TesseractException {
        String formatedCpf = cpf.replace(".", "").replace("-", "");
        validadeCPF(formatedCpf);
        return formatedCpf;
    }

    private void validadeCPF(String cpf) throws TesseractException {
        if (!Pattern.matches(CPF_REGEX, cpf)) {
            throw new TesseractException("Falha ao extrair o cpf do documento");
        }
    }

    private void validateDate(String date) throws TesseractException {
        if (!Pattern.matches(DATE_REGEX, date)) {
            throw new TesseractException("Falha ao extrair a data de nascimento do documento");
        }
    }

}
