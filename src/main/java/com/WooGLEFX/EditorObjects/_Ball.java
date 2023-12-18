package com.WooGLEFX.EditorObjects;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WorldOfGoo.Ball.Part;

import java.util.ArrayList;

public class _Ball {

    private double version;

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    private double boundingBoxX;
    private double boundingBoxY;

    public double getBoundingBoxX() {
        return boundingBoxX;
    }

    public double getBoundingBoxY() {
        return boundingBoxY;
    }

    public ArrayList<EditorObject> objects;
    public ArrayList<EditorObject> resources;

    public ArrayList<EditorObject> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<EditorObject> objects) {
        this.objects = objects;
    }

    public ArrayList<EditorObject> getResources() {
        return resources;
    }

    public void setResources(ArrayList<EditorObject> resources) {
        this.resources = resources;
    }

    private String shapeType;
    private double shapeSize;
    private double shapeSize2;
    private double shapeVariance;

    public String getShapeType() {
        return shapeType;
    }

    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
    }

    public double getShapeSize() {
        return shapeSize;
    }

    public void setShapeSize(double shapeSize) {
        this.shapeSize = shapeSize;
    }

    public double getShapeSize2() {
        return shapeSize2;
    }

    public void setShapeSize2(double shapeSize2) {
        this.shapeSize2 = shapeSize2;
    }

    public double getShapeVariance() {
        return shapeVariance;
    }

    public void setShapeVariance(double shapeVariance) {
        this.shapeVariance = shapeVariance;
    }

    public _Ball(ArrayList<EditorObject> _objects, ArrayList<EditorObject> _resources){
        this.objects = _objects;
        this.resources = _resources;

        String input2 = objects.get(0).getAttribute("shape");

        setShapeType(input2.substring(0, input2.indexOf(",")));

        String input3 = input2.substring(input2.indexOf(",") + 1);

        if (getShapeType().equals("circle")) {
            if (input3.contains(",")) {
                setShapeSize(Double.parseDouble(input3.substring(0, input3.indexOf(","))));
                setShapeVariance(Double.parseDouble(input3.substring(input3.indexOf(",") + 1)));
            } else {
                setShapeSize(Double.parseDouble(input3));
                setShapeVariance(0);
            }
        } else {
            setShapeSize2(Double.parseDouble(input3.substring(0, input3.indexOf(","))));
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

    public void makeImages(double version){
        for (EditorObject obj : objects){
            if (obj.getRealName().equals("part")){
                String word = "";
                for (int i = 0; i < obj.getAttribute("image").length(); i++){
                    String singleChar = obj.getAttribute("image").substring(i, i + 1);
                    if (singleChar.equals(",")){
                        try {
                            ((Part) obj).getImages().add(GlobalResourceManager.getImage(word, version));
                        } catch (Exception e) {
                            Main.failedResources.add(("From ball \"" + getObjects().get(0).getAttribute("name") + "\": Image \"" + word + "\" (version " + version + ")"));
                        }
                        word = "";
                    } else {
                        word += singleChar;
                    }
                }
                try {
                    ((Part) obj).getImages().add(GlobalResourceManager.getImage(word, version));
                } catch (Exception e) {
                    Main.failedResources.add(("From ball \"" + getObjects().get(0).getAttribute("name") + "\": Image \"" + word + "\" (version " + version + ")"));
                }

                if (!obj.getAttribute("pupil").equals("")){
                    try {
                        ((Part) obj).setPupilImage(GlobalResourceManager.getImage(obj.getAttribute("pupil"), version));
                    } catch (Exception e) {
                        Main.failedResources.add(("From ball \"" + getObjects().get(0).getAttribute("name") + "\": Image \"" + obj.getAttribute("pupil") + "\" (version " + version + ")"));
                    }
                }
            }
        }
    }

}
