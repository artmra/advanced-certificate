package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.CNHInfo;
import br.ufsc.labsec.emissoravancado.components.LegacyCnhFormatInfoMapping;
import br.ufsc.labsec.emissoravancado.components.NewCnhFormatInfoMapping;
import java.awt.image.BufferedImage;
import java.util.List;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TesseractService {
    private String tesseractDatapath;
    private final String tesseractLanguage = "por";
    private final ITesseract instance;

    {
        instance = new Tesseract();
        instance.setDatapath(tesseractDatapath);
        instance.setLanguage(tesseractLanguage);
    }

    @Autowired
    public TesseractService(@Value("${cnh.verifier}") String tesseractDatapath) {
        this.tesseractDatapath = tesseractDatapath;
    }

    public CNHInfo extractData(List<BufferedImage> images) {
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

    private CNHInfo extractDataFromLegacyFormat(
            BufferedImage firstImage, BufferedImage secondImage) {
        try {
            var builder = CNHInfo.builder();
            // extract info from first page
            builder.name(instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getNameArea()));
            builder.docInfo(
                    instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getIdDocInfoArea()));
            builder.cpf(instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getCpfArea()));
            builder.birthDate(
                    instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getBirthDateArea()));
            builder.fatherName(
                    instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getFatherNameArea()));
            builder.motherName(
                    instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getMotherNameArea()));
            builder.cnh(
                    instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getRegisterNumberArea()));
            builder.validity(
                    instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getValidityArea()));
            builder.firstCNHDate(
                    instance.doOCR(
                            firstImage,
                            LegacyCnhFormatInfoMapping.getFirstDriverLicenceDateArea()));
            // extract info from second page
            builder.issuePlace(
                    instance.doOCR(secondImage, LegacyCnhFormatInfoMapping.getIssuePlaceArea()));
            builder.issueDate(
                    instance.doOCR(secondImage, LegacyCnhFormatInfoMapping.getIssueDateArea()));
            return builder.build();
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }

    private CNHInfo extractDataFromNewFormat(BufferedImage firstImage, BufferedImage secondImage) {
        try {
            var builder = CNHInfo.builder();
            // extract info from first page
            builder.name(instance.doOCR(firstImage, NewCnhFormatInfoMapping.getNameArea()));
            builder.docInfo(instance.doOCR(firstImage, NewCnhFormatInfoMapping.getIdDocInfoArea()));
            builder.cpf(instance.doOCR(firstImage, NewCnhFormatInfoMapping.getCpfArea()));
            builder.birthDate(
                    instance.doOCR(firstImage, NewCnhFormatInfoMapping.getBirthInfoArea()));
            builder.fatherName(
                    instance.doOCR(firstImage, NewCnhFormatInfoMapping.getFatherNameArea()));
            builder.motherName(
                    instance.doOCR(firstImage, NewCnhFormatInfoMapping.getMotherNameArea()));
            builder.cnh(
                    instance.doOCR(firstImage, NewCnhFormatInfoMapping.getRegisterNumberArea()));
            builder.nationality(
                    instance.doOCR(firstImage, NewCnhFormatInfoMapping.getNationalityArea()));
            builder.validity(instance.doOCR(firstImage, NewCnhFormatInfoMapping.getValidityArea()));
            builder.firstCNHDate(
                    instance.doOCR(
                            firstImage, NewCnhFormatInfoMapping.getFirstDriverLicenceDateArea()));
            builder.issueDate(
                    instance.doOCR(firstImage, NewCnhFormatInfoMapping.getIssueDateArea()));
            // extract info from second page
            builder.issuePlace(
                    instance.doOCR(secondImage, NewCnhFormatInfoMapping.getIssuePlaceArea()));
            return builder.build();
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }
}
