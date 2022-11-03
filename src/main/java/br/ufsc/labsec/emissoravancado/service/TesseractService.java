package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.CNHInfo;
import br.ufsc.labsec.emissoravancado.components.LegacyCnhFormatInfoMapping;
import br.ufsc.labsec.emissoravancado.components.NewCnhFormatInfoMapping;
import java.awt.*;
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
    private final String tesseractDatapath;
    private final String tesseractLanguage = "por";
    private final ITesseract instance;

    @Autowired
    public TesseractService(@Value("${tesseract.datapath}") String tesseractDatapath) {
        this.tesseractDatapath = tesseractDatapath;
        this.instance = new Tesseract();
        this.instance.setDatapath(this.tesseractDatapath);
        this.instance.setLanguage(this.tesseractLanguage);
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
                // todo: loggar erro e retornar algo
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
        builder.birthDate(doOCR(firstImage, LegacyCnhFormatInfoMapping.getBirthDateArea()));
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
        builder.birthDate(extractBirthDate(birthData));
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

    private String formatCPF(String cpf) {
        return cpf.replace(".", "").replace("-", "");
    }
}
