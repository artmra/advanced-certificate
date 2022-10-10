package br.ufsc.labsec.emissoravancado.service;

import br.ufsc.labsec.emissoravancado.components.LegacyCnhFormatInfoMapping;
import br.ufsc.labsec.emissoravancado.components.NewCnhFormatInfoMapping;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Service
public class TesseractService {
    @Value("tesseract.datapath")
    private String tesseractDatapath;
    private final ITesseract instance;

    {
        instance = new Tesseract();
        instance.setDatapath(tesseractDatapath);
        instance.setLanguage("por");
    }

    public void extractData(List<BufferedImage> images) {
        switch (images.size()) {
            case 3:
                this.extractDataFromLegacyFormat(images.get(0), images.get(1));
                break;
            case 4:
                this.extractDataFromLegacyFormat(images.get(1), images.get(2));
                break;
            case 5:
                this.extractDataFromNewFormat(images.get(1), images.get(2));
                break;
            default:
                throw  new RuntimeException("Formato de CNH digital não suportado");
        }
    }

    private void extractDataFromLegacyFormat(BufferedImage firstImage, BufferedImage secondImage) {
        try {
            // extract info from first page
            System.out.println("nome: " + instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getNameArea()));
            System.out.println("docInfo: " + instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getIdDocInfoArea()));
            System.out.println("cpf: " + instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getCpfArea()));
            System.out.println("data nascimento: " + instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getBirthDateArea()));
            System.out.println("nome pai: " + instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getFatherNameArea()));
            System.out.println("nome mae: " + instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getMotherNameArea()));
            System.out.println("numero cnh: " + instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getRegisterNumberArea()));
            System.out.println("validade: " + instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getValidityArea()));
            System.out.println("data primeira habilitacao: " + instance.doOCR(firstImage, LegacyCnhFormatInfoMapping.getFirstDriverLicenceDateArea()));
            // extract info from second page
            System.out.println("lugar emissao: " + instance.doOCR(secondImage, LegacyCnhFormatInfoMapping.getIssuePlaceArea()));
            System.out.println("data emissao: " + instance.doOCR(secondImage, LegacyCnhFormatInfoMapping.getIssueDateArea()));
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }

    }
    private void extractDataFromNewFormat(BufferedImage firstImage, BufferedImage secondImage) {
        try {
            // extract info from first page
            System.out.println("nome: " + instance.doOCR(firstImage, NewCnhFormatInfoMapping.getNameArea()));
            System.out.println("docInfo: " + instance.doOCR(firstImage, NewCnhFormatInfoMapping.getIdDocInfoArea()));
            System.out.println("cpf: " + instance.doOCR(firstImage, NewCnhFormatInfoMapping.getCpfArea()));
            System.out.println("Informacoes data nascimento: " + instance.doOCR(firstImage, NewCnhFormatInfoMapping.getBirthInfoArea()));
            System.out.println("nome pai: " + instance.doOCR(firstImage, NewCnhFormatInfoMapping.getFatherNameArea()));
            System.out.println("nome mae: " + instance.doOCR(firstImage, NewCnhFormatInfoMapping.getMotherNameArea()));
            System.out.println("numero cnh: " + instance.doOCR(firstImage, NewCnhFormatInfoMapping.getRegisterNumberArea()));
            System.out.println("nacionalidade: " + instance.doOCR(firstImage, NewCnhFormatInfoMapping.getNationalityArea()));
            System.out.println("validade: " + instance.doOCR(firstImage, NewCnhFormatInfoMapping.getValidityArea()));
            System.out.println("data primeira habilitacao: " + instance.doOCR(firstImage, NewCnhFormatInfoMapping.getFirstDriverLicenceDateArea()));
            System.out.println("data emissao: " + instance.doOCR(firstImage, NewCnhFormatInfoMapping.getIssueDateArea()));
            // extract info from second page
            System.out.println("lugar emissao: " + instance.doOCR(secondImage, NewCnhFormatInfoMapping.getIssuePlaceArea()));
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }

    }


}
