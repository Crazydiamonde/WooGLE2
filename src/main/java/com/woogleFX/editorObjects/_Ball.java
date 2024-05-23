package com.woogleFX.editorObjects;

import com.woogleFX.functions.LevelLoader;
import com.woogleFX.file.resourceManagers.GlobalResourceManager;
import com.woogleFX.structures.GameVersion;
import com.worldOfGoo.ball.Part;

import java.util.ArrayList;

public class _Ball {

    private GameVersion version;
    public GameVersion getVersion() {
        return version;
    }
    public void setVersion(GameVersion version) {
        this.version = version;
    }


    public final ArrayList<EditorObject> objects;
    public ArrayList<EditorObject> getObjects() {
        return objects;
    }


    public final ArrayList<EditorObject> resources;
    public ArrayList<EditorObject> getResources() {
        return resources;
    }


    private String shapeType;
    public String getShapeType() {
        return shapeType;
    }
    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
    }


    private double shapeSize;
    public double getShapeSize() {
        return shapeSize;
    }
    public void setShapeSize(double shapeSize) {
        this.shapeSize = shapeSize;
    }


    private double shapeSize2;
    public double getShapeSize2() {
        return shapeSize2;
    }
    public void setShapeSize2(double shapeSize2) {
        this.shapeSize2 = shapeSize2;
    }


    private double shapeVariance;
    public double getShapeVariance() {
        return shapeVariance;
    }
    public void setShapeVariance(double shapeVariance) {
        this.shapeVariance = shapeVariance;
    }


    public _Ball(ArrayList<EditorObject> _objects, ArrayList<EditorObject> _resources){
        this.objects = _objects;
        this.resources = _resources;

        String input2 = objects.get(0).getAttribute("shape").stringValue();

        setShapeType(input2.substring(0, input2.indexOf(",")));

        String input3 = input2.substring(input2.indexOf(",") + 1);

        double size1 = input3.contains(",") ? Double.parseDouble(input3.substring(0, input3.indexOf(","))) : Double.parseDouble(input3);

        if (getShapeType().equals("circle")) {
            if (input3.contains(",")) {
                setShapeSize(size1);
                setShapeVariance(Double.parseDouble(input3.substring(input3.indexOf(",") + 1)));
            } else {
                setShapeSize(Double.parseDouble(input3));
                setShapeVariance(0);
            }
        } else {
            setShapeSize2(size1);
            String input4 = input3.substring(input3.indexOf(",") + 1);
            if (input4.contains(",")) {
                setShapeSize(Double.parseDouble(input4.substring(0, input4.indexOf(","))));
                setShapeVariance(Double.parseDouble(input4.substring(input4.indexOf(",") + 1)));
            } else {
                setShapeSize(Double.parseDouble(input4));
                setShapeVariance(0);
            }
        }
    }


    public void makeImages(GameVersion version) {
        for (EditorObject obj : objects) {
            if (obj.getType().equals("part")) {
                String word = "";
                for (int i = 0; i < obj.getAttribute("image").stringValue().length(); i++) {
                    String singleChar = obj.getAttribute("image").stringValue().substring(i, i + 1);
                    if (singleChar.equals(",")) {
                        try {
                            ((Part) obj).getImages().add(GlobalResourceManager.getImage(word, version));
                        } catch (Exception e) {
                            LevelLoader.failedResources.add(("From ball \"" + getObjects().get(0).getAttribute("name").stringValue() + "\": Image \"" + word + "\" (version " + (version == GameVersion.OLD ? "1.3" : "1.5") + ")"));
                        }
                        word = "";
                    } else {
                        word += singleChar;
                    }
                }
                try {
                    ((Part) obj).getImages().add(GlobalResourceManager.getImage(word, version));
                } catch (Exception e) {
                    LevelLoader.failedResources.add(("From ball \"" + getObjects().get(0).getAttribute("name").stringValue() + "\": Image \"" + word + "\" (version " + (version == GameVersion.OLD ? "1.3" : "1.5") + ")"));
                }

                if (!obj.getAttribute("pupil").stringValue().isEmpty()) {
                    try {
                        ((Part) obj).setPupilImage(GlobalResourceManager.getImage(obj.getAttribute("pupil").stringValue(), version));
                    } catch (Exception e) {
                        LevelLoader.failedResources.add(("From ball \"" + getObjects().get(0).getAttribute("name").stringValue() + "\": Image \"" + obj.getAttribute("pupil").stringValue() + "\" (version " + (version == GameVersion.OLD ? "1.3" : "1.5") + ")"));
                    }
                }
            }
        }
    }

}
