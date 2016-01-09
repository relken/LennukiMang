package LennukiMang;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Lennuk extends ImageView {
    public static int fromX, toX;

    public Lennuk(Image image) {
        setFitWidth(150);
        setPreserveRatio(true);
        setTranslateX(-500);
        setTranslateY(-500);
        setImage(image);
    }
}

