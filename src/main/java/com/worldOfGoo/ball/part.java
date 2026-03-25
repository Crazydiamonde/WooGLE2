package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.assets.GameVersion;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class part extends EditorObject {

    public part(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        for (ball.BallState ballState : ball.BallState.values()) {

            int x = ballState.ordinal() % 5;
            int y = ballState.ordinal() / 5;

            addPart("image", x, y);
            addPart("pupil", x, y);

        }

    }

    @Override
    public String getName() {
        return getAttribute("name").stringValue();
    }

    private void addPart(String imageOrPupil, double _x, double _y) {

        addObjectComponent(new ImageComponent(this) {

            @Override
            public Image getImage() {
                if (getAttribute(imageOrPupil).listValue().length == 0) return null;
                return ResourceManager.getImage(getAsset().getResources(), getAttribute(imageOrPupil).listValue()[0], getVersion());
            }

            @Override
            public double getX() {
                double spacing = ((ball) getParent()).getSpacing();
                return InputField.getRange(getAttribute("x").stringValue(), 0.5) + _x * spacing;
            }

            @Override
            public void setX(double x) {
                double spacing = ((ball) getParent()).getSpacing();
                if (getAttribute("x").stringValue().contains(",")) {
                    String[] xs = getAttribute("x").stringValue().split(",");
                    double x1 = Double.parseDouble(xs[0]);
                    double x2 = Double.parseDouble(xs[1]);
                    double variance = (x2 - x1) / 2;
                    setAttribute("x", (x - variance - _x * spacing) + "," + (x + variance - _x * spacing));
                } else {
                    setAttribute("x", x - _x * spacing);
                }
            }

            @Override
            public double getY() {
                double spacing = ((ball) getParent()).getSpacing();
                return -InputField.getRange(getAttribute("y").stringValue(), 0.5) + _y * spacing;
            }

            @Override
            public void setY(double y) {
                double spacing = ((ball) getParent()).getSpacing();
                if (getAttribute("y").stringValue().contains(",")) {
                    String[] ys = getAttribute("y").stringValue().split(",");
                    double y1 = Double.parseDouble(ys[0]);
                    double y2 = Double.parseDouble(ys[1]);
                    double variance = (y2 - y1) / 2;
                    setAttribute("y", (-y - variance + _y * spacing) + "," + (-y + variance + _y * spacing));
                } else {
                    setAttribute("y", -y + _y * spacing);
                }
            }

            @Override
            public double getScaleX() {
                return getAttribute("scale").doubleValue();
            }

            @Override
            public void setScaleX(double scaleX) {
                setAttribute("scale", scaleX);
            }

            @Override
            public double getScaleY() {
                return getAttribute("scale").doubleValue();
            }

            @Override
            public void setScaleY(double scaleY) {
                setAttribute("scale", scaleY);
            }

            @Override
            public double getDepth() {
                return getAttribute("layer").doubleValue();
            }

            @Override
            public boolean isRotatable() {
                return false;
            }

            @Override
            public boolean isVisible() {
                if (AssetManager.getVisibility("parts") == 0) return false;
                if (getAttribute("state").stringValue().isEmpty()) return true;
                else for (String state : getAttribute("state").listValue()) {
                    if (state.equals(states[(int)_y * 5 + (int)_x])) return true;
                }
                return false;
            }
        });

        addObjectComponent(new RectangleComponent(this) {
            @Override
            public double getWidth() {
                double minX = InputField.getRange(getAttribute("x").stringValue(), 0);
                double maxX = InputField.getRange(getAttribute("x").stringValue(), 1);
                return Math.abs(maxX - minX);
            }

            @Override
            public double getHeight() {
                double minY = -InputField.getRange(getAttribute("y").stringValue(), 1);
                double maxY = -InputField.getRange(getAttribute("y").stringValue(), 0);
                return Math.abs(maxY - minY);
            }

            @Override
            public double getX() {
                double spacing = ((ball) getParent()).getSpacing();
                double minX = InputField.getRange(getAttribute("x").stringValue(), 0);
                double maxX = InputField.getRange(getAttribute("x").stringValue(), 1);
                return (minX + maxX) / 2 + _x * spacing;
            }

            @Override
            public double getY() {
                double spacing = ((ball) getParent()).getSpacing();
                double minY = -InputField.getRange(getAttribute("y").stringValue(), 1);
                double maxY = -InputField.getRange(getAttribute("y").stringValue(), 0);
                return (minY + maxY) / 2 + _y * spacing;
            }

            @Override
            public void setX(double x) {
                double spacing = ((ball) getParent()).getSpacing();
                double width = getWidth();
                if (width == 0) {
                    setAttribute("x", x - _x * spacing);
                } else {
                    double minX = x - width / 2 - _x * spacing;
                    double maxX = x + width / 2 - _x * spacing;
                    setAttribute("x", minX + "," + maxX);
                }
            }

            @Override
            public void setY(double y) {
                double spacing = ((ball) getParent()).getSpacing();
                double height = getHeight();
                if (height == 0) {
                    setAttribute("y", -y + _y * spacing);
                } else {
                    double minY = y - height / 2 - _y * spacing;
                    double maxY = y + height / 2 - _y * spacing;
                    setAttribute("y", -maxY + "," + -minY);
                }
            }

            @Override
            public void setWidth(double width) {
                double spacing = ((ball) getParent()).getSpacing();
                double x = getX();
                if (width == 0) {
                    setAttribute("x", x - _x * spacing);
                } else {
                    double minX = x - width / 2 - _x * spacing;
                    double maxX = x + width / 2 - _x * spacing;
                    setAttribute("x", minX + "," + maxX);
                }
            }

            @Override
            public void setHeight(double height) {
                double spacing = ((ball) getParent()).getSpacing();
                double y = getY();
                if (height == 0) {
                    setAttribute("y", y - _y * spacing);
                } else {
                    double minY = y - height / 2 - _y * spacing;
                    double maxY = y + height / 2 - _y * spacing;
                    setAttribute("y", -maxY + "," + -minY);
                }
            }

            @Override
            public double getDepth() {
                return 1.0;
            }

            @Override
            public Depth.Layer getGlobalLayer() {
                return Depth.Layer.SCENE_FG;
            }

            @Override
            public double getEdgeSize() {
                return 0.5;
            }

            @Override
            public Paint getBorderColor() {
                return Color.LIME;
            }

            @Override
            public boolean isEdgeOnly() {
                return true;
            }

            @Override
            public Paint getColor() {
                return Color.TRANSPARENT;
            }

            @Override
            public boolean isVisible() {
                if (AssetManager.getVisibility("bounds") == 0) return false;
                if (getAttribute("state").stringValue().isEmpty()) return true;
                else for (String state : getAttribute("state").listValue()) {
                    if (state.equals(states[(int)_y * 5 + (int)_x])) return true;
                }
                return false;
            }

            @Override
            public boolean isRotatable() {
                return false;
            }
        });

        addObjectComponent(new RectangleComponent(this) {
            @Override
            public double getWidth() {
                double minX = InputField.getRange(getAttribute("xrange").stringValue(), 0);
                double maxX = InputField.getRange(getAttribute("xrange").stringValue(), 1);
                return Math.abs(maxX - minX);
            }

            @Override
            public double getHeight() {
                double minY = -InputField.getRange(getAttribute("yrange").stringValue(), 1);
                double maxY = -InputField.getRange(getAttribute("yrange").stringValue(), 0);
                return Math.abs(maxY - minY);
            }

            @Override
            public double getX() {
                double spacing = ((ball) getParent()).getSpacing();
                double minX = InputField.getRange(getAttribute("xrange").stringValue(), 0);
                double maxX = InputField.getRange(getAttribute("xrange").stringValue(), 1);
                return (minX + maxX) / 2 + _x * spacing;
            }

            @Override
            public double getY() {
                double spacing = ((ball) getParent()).getSpacing();
                double minY = -InputField.getRange(getAttribute("yrange").stringValue(), 1);
                double maxY = -InputField.getRange(getAttribute("yrange").stringValue(), 0);
                return (minY + maxY) / 2 + _y * spacing;
            }

            @Override
            public void setX(double x) {
                double spacing = ((ball) getParent()).getSpacing();
                double width = getWidth();
                if (width == 0) {
                    setAttribute("xrange", x - _x * spacing);
                } else {
                    double minX = x - width / 2 - _x * spacing;
                    double maxX = x + width / 2 - _x * spacing;
                    setAttribute("xrange", minX + "," + maxX);
                }
            }

            @Override
            public void setY(double y) {
                double spacing = ((ball) getParent()).getSpacing();
                double height = getHeight();
                if (height == 0) {
                    setAttribute("yrange", -y + _y * spacing);
                } else {
                    double minY = y - height / 2 - _y * spacing;
                    double maxY = y + height / 2 - _y * spacing;
                    setAttribute("yrange", -maxY + "," + -minY);
                }
            }

            @Override
            public void setWidth(double width) {
                double spacing = ((ball) getParent()).getSpacing();
                double x = getX();
                if (width == 0) {
                    setAttribute("xrange", x - _x * spacing);
                } else {
                    double minX = x - width / 2 - _x * spacing;
                    double maxX = x + width / 2 - _x * spacing;
                    setAttribute("xrange", minX + "," + maxX);
                }
            }

            @Override
            public void setHeight(double height) {
                double spacing = ((ball) getParent()).getSpacing();
                double y = getY();
                if (height == 0) {
                    setAttribute("yrange", y - _y * spacing);
                } else {
                    double minY = y - height / 2 - _y * spacing;
                    double maxY = y + height / 2 - _y * spacing;
                    setAttribute("yrange", -maxY + "," + -minY);
                }
            }

            @Override
            public double getDepth() {
                return 1.0;
            }

            @Override
            public Depth.Layer getGlobalLayer() {
                return Depth.Layer.SCENE_FG;
            }

            @Override
            public double getEdgeSize() {
                return 0.5;
            }

            @Override
            public Paint getBorderColor() {
                return Color.YELLOW;
            }

            @Override
            public boolean isEdgeOnly() {
                return true;
            }

            @Override
            public Paint getColor() {
                return Color.TRANSPARENT;
            }

            @Override
            public boolean isVisible() {
                if (AssetManager.getVisibility("bounds") == 0) return false;
                if (!getAttribute("eye").booleanValue()) return false;
                if (getAttribute("state").stringValue().isEmpty()) return true;
                else for (String state : getAttribute("state").listValue()) {
                    if (state.equals(states[(int)_y * 5 + (int)_x])) return true;
                }
                return false;
            }

            @Override
            public boolean isRotatable() {
                return false;
            }
        });

    }

    private static final String[] states = new String[] {
            "standing",
            "walking",
            "climbing",
            "falling",
            "dragging",
            "attached",
            "detaching",
            "sleeping",
            "pipe",
            "tank",
            "stuck",
            "stuck_attached",
            "stuck_detached",
    };

}
