package br.ufsc.labsec.emissoravancado.components;

import lombok.Getter;

import java.awt.*;

public class NewCnhFormatInfoMapping {
    @Getter
    private final static Rectangle nameArea = new Rectangle(171, 187, 599, 31);
    @Getter
    private final static Rectangle firstDriverLicenceDateArea = new Rectangle(785, 189, 158, 30);
    @Getter
    private final static Rectangle birthInfoArea = new Rectangle(442, 243, 500, 31);
    @Getter
    private final static Rectangle issueDateArea = new Rectangle(441, 299, 160, 30);
    @Getter
    private final static Rectangle validityArea = new Rectangle(617, 300, 152, 27);
    @Getter
    private final static Rectangle idDocInfoArea = new Rectangle(442, 354, 502, 30);
    @Getter
    private final static Rectangle cpfArea = new Rectangle(441, 410, 181, 29);
    @Getter
    private final static Rectangle registerNumberArea = new Rectangle(638, 411, 166, 29);
    @Getter
    private final static Rectangle nationalityArea = new Rectangle(441, 464, 501, 32);
    @Getter
    private final static Rectangle fatherNameArea = new Rectangle(442, 518, 499, 68);
    @Getter
    private final static Rectangle motherNameArea = new Rectangle(442, 587, 499, 68);
    @Getter
    private final static Rectangle issuePlaceArea = new Rectangle(160, 515, 474, 28);
}
