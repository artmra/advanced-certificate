package br.ufsc.labsec.emissoravancado.components;

import java.awt.*;
import lombok.Getter;

public class NewCnhFormatInfoMapping {
    @Getter private static final Rectangle nameArea = new Rectangle(171, 187, 599, 31);
    @Getter private static final Rectangle idDocInfoArea = new Rectangle(442, 354, 502, 30);
    @Getter private static final Rectangle cpfArea = new Rectangle(441, 410, 181, 29);
    @Getter private static final Rectangle birthInfoArea = new Rectangle(442, 243, 500, 31);
    @Getter private static final Rectangle fatherNameArea = new Rectangle(442, 518, 499, 68);
    @Getter private static final Rectangle motherNameArea = new Rectangle(442, 587, 499, 68);
    @Getter private static final Rectangle registerNumberArea = new Rectangle(638, 411, 166, 29);
    @Getter private static final Rectangle validityArea = new Rectangle(617, 300, 152, 27);

    @Getter
    private static final Rectangle firstDriverLicenceDateArea = new Rectangle(785, 189, 158, 30);

    @Getter private static final Rectangle issueDateArea = new Rectangle(441, 299, 160, 30);
    @Getter private static final Rectangle issuePlaceArea = new Rectangle(160, 515, 474, 28);
    @Getter private static final Rectangle nationalityArea = new Rectangle(441, 464, 501, 32);
}
