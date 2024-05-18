package com.WorldOfGoo.Ball;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Part extends EditorObject {

    private final ArrayList<Image> images = new ArrayList<>();
    public ArrayList<Image> getImages() {
        return images;
    }


    private Image pupilImage = null;
    public Image getPupilImage() {
        return pupilImage;
    }
    public void setPupilImage(Image pupilImage) {
        this.pupilImage = pupilImage;
    }


    public Part(EditorObject _parent) {
        super(_parent, "part");

        addAttribute("name",       InputField.ANY)                        .assertRequired();
        addAttribute("layer",      InputField.NUMBER)                     .assertRequired();
        addAttribute("x",          InputField.NUMBER)                     .assertRequired();
        addAttribute("y",          InputField.NUMBER)                     .assertRequired();
        addAttribute("image",      InputField.ANY)                        .assertRequired();
        addAttribute("scale",      InputField.NUMBER).setDefaultValue("1").assertRequired();
        addAttribute("rotate",     InputField.FLAG);
        addAttribute("stretch",    InputField.NUMBER);
        addAttribute("xrange",     InputField.POSITION);
        addAttribute("yrange",     InputField.POSITION);
        addAttribute("eye",        InputField.FLAG);
        addAttribute("pupil",      InputField.IMAGE);
        addAttribute("pupilinset", InputField.NUMBER);
        addAttribute("state",      InputField.ANY);

    }

}
