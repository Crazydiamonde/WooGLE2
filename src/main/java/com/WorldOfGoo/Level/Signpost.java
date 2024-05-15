package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Functions.LevelLoader;
import com.WooGLEFX.Functions.LevelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.Color;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WorldOfGoo.Scene.SceneLayer;

import javafx.beans.value.ChangeListener;

public class Signpost extends EditorObject {

    private static final Logger logger = LoggerFactory.getLogger(Signpost.class);


    public Signpost(EditorObject _parent) {
        super(_parent);
        setRealName("signpost");

        addAttribute("name",      InputField.ANY)                                  .assertRequired();
        addAttribute("depth",     InputField.NUMBER).setDefaultValue("0")          .assertRequired();
        addAttribute("x",         InputField.NUMBER).setDefaultValue("0")          .assertRequired();
        addAttribute("y",         InputField.NUMBER).setDefaultValue("0")          .assertRequired();
        addAttribute("scalex",    InputField.NUMBER).setDefaultValue("1")          .assertRequired();
        addAttribute("scaley",    InputField.NUMBER).setDefaultValue("1")          .assertRequired();
        addAttribute("rotation",  InputField.NUMBER).setDefaultValue("0")          .assertRequired();
        addAttribute("alpha",     InputField.NUMBER).setDefaultValue("1")          .assertRequired();
        addAttribute("colorize",  InputField.COLOR) .setDefaultValue("255,255,255").assertRequired();
        addAttribute("image",     InputField.IMAGE)                                .assertRequired();
        addAttribute("text",      InputField.TEXT)                                 .assertRequired();
        addAttribute("particles", InputField.PARTICLES);
        addAttribute("pulse",     InputField.ANY);

        addObjectPosition(new ObjectPosition(ObjectPosition.IMAGE) {
            public double getX() {
                return getDouble("x");
            }
            public void setX(double x) {
                setAttribute("x", x);
            }
            public double getY() {
                return -getDouble("y");
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getRotation() {
                return -Math.toRadians(getDouble("rotation"));
            }
            public void setRotation(double rotation) {
                setAttribute("rotation", -Math.toDegrees(rotation));
            }
            public double getWidth() {
                return getImage().getWidth() * Math.abs(getDouble("scalex"));
            }
            public void setWidth(double width) {
                setAttribute("scalex", width / getImage().getWidth());
            }
            public double getHeight() {
                return getImage().getHeight() * Math.abs(getDouble("scaley"));
            }
            public void setHeight(double height) {
                setAttribute("scaley", height / getImage().getHeight());
            }
        });

        setNameAttribute(getAttribute2("name"));
        setMetaAttributes(MetaEditorAttribute.parse("name,text,particles,pulse,Image<x,y,scalex,scaley,image,rotation,depth,alpha,colorize>"));

    }


    @Override
    public void update() {
        setNameAttribute(getAttribute2("name"));
        try {
            setImage(GlobalResourceManager.getImage(getAttribute("image"), getLevel().getVersion()));
            Color color = Color.parse(getAttribute("colorize"));
            setImage(SceneLayer.colorize(getImage(), color));
        } catch (Exception e) {
            if (!LevelLoader.failedResources.contains("From signpost: \"" + getAttribute("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")")) {
                LevelLoader.failedResources.add("From signpost: \"" + getAttribute("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")");
            }
            setImage(null);
        }

        ChangeListener<String> wizard = (observable, oldValue, newValue) -> {
            logger.trace("Image changed from " + oldValue + " to " + newValue);
            try {
                setImage(GlobalResourceManager.getImage(getAttribute("image"), getLevel().getVersion()));
                Color color = Color.parse(getAttribute("colorize"));
                setImage(SceneLayer.colorize(getImage(), color));
            } catch (Exception e) {
                if (!LevelLoader.failedResources.contains("From signpost: \"" + getString("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")")) {
                    LevelLoader.failedResources.add("From signpost: \"" + getString("image") + "\" (version " + LevelManager.getLevel().getVersion() + ")");
                }
                setImage(null);
            }
        };

        setChangeListener("image", wizard);
        setChangeListener("colorize", wizard);
    }


    @Override
    public boolean isVisible() {
        return LevelManager.getLevel().isShowGraphics();
    }

}
