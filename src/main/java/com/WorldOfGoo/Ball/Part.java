package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Part extends EditorObject {

    private ArrayList<Image> images = new ArrayList<>();

    private Image pupilImage = null;

    public Image getPupilImage() {
        return pupilImage;
    }

    public void setPupilImage(Image pupilImage) {
        this.pupilImage = pupilImage;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public Part(EditorObject _parent) {
        super(_parent);
        addAttribute("name", "", InputField.ANY, true);
        addAttribute("layer", "", InputField.NUMBER, true);
        addAttribute("x", "", InputField.NUMBER, true);
        addAttribute("y", "", InputField.NUMBER, true);
        addAttribute("image", "", InputField.ANY, true);
        addAttribute("scale", "1", InputField.NUMBER, true);
        addAttribute("rotate", "", InputField.FLAG, false);
        addAttribute("stretch", "", InputField.NUMBER, false);
        addAttribute("xrange", "", InputField.POSITION, false);
        addAttribute("yrange", "", InputField.POSITION, false);
        addAttribute("eye", "", InputField.FLAG, false);
        addAttribute("pupil", "", InputField.IMAGE, false);
        addAttribute("pupilinset", "", InputField.NUMBER, false);
        addAttribute("state", "", InputField.ANY, false);
    }
}
