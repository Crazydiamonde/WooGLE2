package com.worldOfGoo2.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.misc.ImageID;

public class Part extends EditorObject {

    public Part(EditorObject parent, GameVersion version) {
        super(parent, version);
        addAttributeAdapter("imageBackgroundIds", AttributeAdapter.childAttributeAdapter(this, "imageBackgroundIds", "imageBackgroundIds", InputField.STRING));
        addAttributeAdapter("states", new AttributeAdapter("states") {

            @Override
            public EditorAttribute getValue() {
                EditorAttribute editorAttribute = new EditorAttribute("states", InputField._2_LIST_NUMBER, Part.this);
                StringBuilder stringBuilder = new StringBuilder();
                for (EditorObject state : getChildren("states")) {
                    stringBuilder.append(state.getAttribute("ballState").intValue()).append(",");
                }
                if (!stringBuilder.isEmpty()) stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                editorAttribute.setValue(stringBuilder.toString());
                return editorAttribute;
            }

            @Override
            public void setValue(String value) {
                String[] states = value.split(",");
                for (EditorObject state : getChildren("states")) {
                    getChildren().remove(state);
                }
                for (String state : states) {
                    EditorObject stateObject = ObjectCreator.create(State.class, Part.this, "states", GameVersion.VERSION_WOG2);
                    stateObject.setAttribute("ballState", state);
                }
            }

        });
        addAttributeAdapter("pupilImageIds", AttributeAdapter.childAttributeAdapter(this, "pupilImageIds", "pupilImageIds", InputField.STRING));
        addAttributeAdapter("color", AttributeAdapter.childAttributeAdapter(this, "color", "color", InputField.NUMBER));
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        javafx.scene.image.Image image = null;
        for (EditorObject editorObject : getChildren("images"))
            if (editorObject instanceof Image image1) {
                javafx.scene.image.Image imageMaybe = ResourceManager.getImage(asset.getResources(), image1.getChild("imageId").getAttribute("imageId").stringValue(), GameVersion.VERSION_WOG2);
                if (imageMaybe != null) image = imageMaybe;
        }

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                addComponent(image, x, y, 1, y * 4 + x, false);
            }
        }

        if (!getAttribute("isEye").booleanValue()) return;

        javafx.scene.image.Image pupilImage = null;
        for (EditorObject editorObject : getChildren("pupilImageIds"))
            if (editorObject instanceof ImageID image1) {
                javafx.scene.image.Image pupilImg = ResourceManager.getImage(asset.getResources(), image1.getAttribute("imageId").stringValue(), GameVersion.VERSION_WOG2);
                if (pupilImg != null) pupilImage = pupilImg;
            }

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                addComponent(pupilImage, x, y, getAttribute("pupilScale").doubleValue(), y * 4 + x, true);
            }
        }

    }


    private EditorObject getBodyPart() {
        String bodyPartName = getParent().getChild("bodyPart").getAttribute("partName").stringValue();
        for (EditorObject editorObject : getParent().getChildren("ballParts")) {
            if (editorObject.getAttribute("name").stringValue().equals(bodyPartName)) return editorObject;
        }
        return null;
    }


    private void addComponent(javafx.scene.image.Image finalImage, double extraX, double extraY, double extraScale, int state, boolean isPupil) {

        addObjectComponent(new ImageComponent(this) {
            @Override
            public javafx.scene.image.Image getImage() {
                return finalImage;
            }

            @Override
            public double getX() {
                double minX = getAttribute("minX").doubleValue();
                double maxX = getAttribute("maxX").doubleValue();
                double minY = -getAttribute("minY").doubleValue();
                double maxY = -getAttribute("maxY").doubleValue();
                double x = (minX + maxX) / 2;
                double y = (minY + maxY) / 2;
                double theta = getAttribute("isRotating").booleanValue() ?
                        -getBodyPart().getAttribute("rotation").doubleValue() : 0;
                return x * Math.cos(theta) - y * Math.sin(theta) + extraX;
            }

            @Override
            public void setX(double _x) {
                double minX = getAttribute("minX").doubleValue();
                double maxX = getAttribute("maxX").doubleValue();
                double minY = -getAttribute("minY").doubleValue();
                double maxY = -getAttribute("maxY").doubleValue();
                double x = (minX + maxX) / 2;
                double theta = getAttribute("isRotating").booleanValue() ?
                        -getBodyPart().getAttribute("rotation").doubleValue() : 0;
                setAttribute("minX", minX + (_x - x) * Math.cos(-theta) - extraX);
                setAttribute("maxX", maxX + (_x - x) * Math.cos(-theta) - extraX);
                setAttribute("minY", -(minY + (_x - x) * Math.sin(-theta)));
                setAttribute("maxY", -(maxY + (_x - x) * Math.sin(-theta)));
            }

            @Override
            public double getY() {
                double minX = getAttribute("minX").doubleValue();
                double maxX = getAttribute("maxX").doubleValue();
                double minY = -getAttribute("minY").doubleValue();
                double maxY = -getAttribute("maxY").doubleValue();
                double x = (minX + maxX) / 2;
                double y = (minY + maxY) / 2;
                double theta = getAttribute("isRotating").booleanValue() ?
                        -getBodyPart().getAttribute("rotation").doubleValue() : 0;
                return x * Math.sin(theta) + y * Math.cos(theta) + extraY;
            }

            @Override
            public void setY(double _y) {
                double minX = getAttribute("minX").doubleValue();
                double maxX = getAttribute("maxX").doubleValue();
                double minY = -getAttribute("minY").doubleValue();
                double maxY = -getAttribute("maxY").doubleValue();
                double y = (minY + maxY) / 2;
                double theta = getAttribute("isRotating").booleanValue() ?
                        -getBodyPart().getAttribute("rotation").doubleValue() : 0;
                setAttribute("minX", minX + (_y - y) * -Math.sin(-theta));
                setAttribute("maxX", maxX + (_y - y) * -Math.sin(-theta));
                setAttribute("minY", -(minY + (_y - y) * Math.cos(-theta) - extraY));
                setAttribute("maxY", -(maxY + (_y - y) * Math.cos(-theta) - extraY));
            }

            @Override
            public double getScaleX() {
                double width = getParent().getAttribute("width").doubleValue();
                double scale = getAttribute("scale").doubleValue();
                if (getAttribute("scaleIsRelative").booleanValue())
                    scale *= width / ((ImageComponent)getBodyPart().getObjectComponents()[0]).getImage().getWidth();
                return scale * extraScale;
            }

            @Override
            public void setScaleX(double scaleX) {
                double width = getParent().getAttribute("width").doubleValue();
                double scale = 1;
                if (getAttribute("scaleIsRelative").booleanValue())
                    scale *= width / ((ImageComponent)getBodyPart().getObjectComponents()[0]).getImage().getWidth();
                setAttribute("scale", scaleX / scale / extraScale);
            }

            @Override
            public double getScaleY() {
                double height = getParent().getAttribute("height").doubleValue();
                double scale = getAttribute("scale").doubleValue();
                if (getAttribute("scaleIsRelative").booleanValue())
                    scale *= height / ((ImageComponent)getBodyPart().getObjectComponents()[0]).getImage().getHeight();
                return scale * extraScale;
            }

            @Override
            public void setScaleY(double scaleY) {
                double height = getParent().getAttribute("height").doubleValue();
                double scale = 1;
                if (getAttribute("scaleIsRelative").booleanValue())
                    scale *= height / ((ImageComponent)getBodyPart().getObjectComponents()[0]).getImage().getHeight();
                setAttribute("scale", scaleY / scale / extraScale);
            }

            @Override
            public double getRotation() {
                double extraRotation = (Part.this == getBodyPart()) ? 0 : getBodyPart().getAttribute("rotation").doubleValue();
                return -getAttribute("rotation").doubleValue() - extraRotation;
            }

            @Override
            public void setRotation(double rotation) {
                double extraRotation = (Part.this == getBodyPart()) ? 0 : getBodyPart().getAttribute("rotation").doubleValue();
                setAttribute("rotation", -rotation + extraRotation);
            }

            @Override
            public double getDepth() {
                return 0;
            }

            public boolean isVisible() {
                return getChildren("states").isEmpty() || getChildren("states").stream().anyMatch(e -> e.getAttribute("ballState").stringValue().equals(String.valueOf(state)));
            }

            @Override
            public boolean isSelectable() {
                return !isPupil;
            }

            @Override
            public boolean isResizable() {
                return !isPupil;
            }

            @Override
            public boolean isRotatable() {
                return !isPupil;
            }

        });

    }

    @Override
    public String getName() {
        return getAttribute("name").stringValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[] { Image.class };
    }

    @Override
    public String[] getPossibleChildrenTypeIDs() {
        return new String[] { "images" };
    }

}
