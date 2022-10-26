package br.ufsc.labsec.emissoravancado.components;

import java.awt.*;
import lombok.Getter;

public class LegacyCnhFormatInfoMapping {
    @Getter private static final Rectangle nameArea = new Rectangle(79, 96, 373, 15);
    @Getter private static final Rectangle idDocInfoArea = new Rectangle(241, 128, 210, 17);
    @Getter private static final Rectangle cpfArea = new Rectangle(241, 159, 118, 19);
    @Getter private static final Rectangle birthDateArea = new Rectangle(367, 159, 86, 16);
    @Getter private static final Rectangle fatherNameArea = new Rectangle(241, 196, 212, 35);
    @Getter private static final Rectangle motherNameArea = new Rectangle(241, 231, 212, 35);
    @Getter private static final Rectangle registerNumberArea = new Rectangle(77, 316, 155, 17);
    @Getter private static final Rectangle validityArea = new Rectangle(241, 316, 97, 17);

    @Getter
    private static final Rectangle firstDriverLicenceDateArea = new Rectangle(346, 315, 107, 18);

    @Getter private static final Rectangle issueDateArea = new Rectangle(345, 205, 108, 18);
    @Getter private static final Rectangle issuePlaceArea = new Rectangle(77, 205, 260, 19);
}
