package br.ufsc.labsec.emissoravancado.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

@Service
public class PDFBoxService {

    public List<BufferedImage> extractImages(byte[] cnhBytes) throws IOException {
        List<BufferedImage> images = new ArrayList<>();
        PDDocument cnh = PDDocument.load(cnhBytes);
        for (PDPage page : cnh.getPages()) {
            PDResources resources = page.getResources();
            for (COSName name : resources.getXObjectNames()) {
                PDXObject xObject = resources.getXObject(name);
                if (xObject instanceof PDImageXObject) {
                    PDImageXObject pdfImage = (PDImageXObject) xObject;
                    images.add(pdfImage.getImage());
                }
            }
        }
        cnh.close();
        return images;
    }

    public void saveImages(List<BufferedImage> images, String baseImageName) throws IOException {

        for (int i = 0; i < images.size(); i++) {
            BufferedImage image = images.get(i);
            ImageIO.write(image, "png", new File(baseImageName + "-" + i + ".png"));
        }
    }
}
