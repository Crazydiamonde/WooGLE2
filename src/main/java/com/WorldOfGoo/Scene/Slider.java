package com.WorldOfGoo.Scene;

import com.WooGLEFX.EditorObjects.ObjectPosition;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

public class Slider extends EditorObject {

    public Slider(EditorObject _parent) {
        super(_parent, "slider", "scene\\slider");

        addAttribute("body1", InputField.ANY).assertRequired();
        addAttribute("body2", InputField.ANY).assertRequired();
        addAttribute("axis", InputField.POSITION).setDefaultValue("1,0").assertRequired();
        addAttribute("bounce", InputField.NUMBER);
        addAttribute("histop", InputField.NUMBER);
        addAttribute("lostop", InputField.NUMBER);
        addAttribute("stopcfm", InputField.NUMBER);
        addAttribute("stoperp", InputField.NUMBER);

        addObjectPosition(new ObjectPosition(ObjectPosition.POINT) {
            public double getX() {
                return getAttribute("anchor").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("anchor", x + "," + getY());
            }
            public double getY() {
                return getAttribute("anchor").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("anchor", getX() + "," + y);
            }

            // TODO erm whar???

        });

        setMetaAttributes(MetaEditorAttribute.parse("body1,body2,axis,Slider<bounce,histop,lostop,stopcfm,stoperp>"));

    }



    @Override
    public String getName() {
        String body1 = getAttribute("body1").stringValue();
        String body2 = getAttribute("body2").stringValue();
        return body1 + ", " + body2;
    }


    /*

    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext){

        if (LevelManager.getLevel().isShowGeometry()) {

            Position axis = getAttribute("axis").positionValue();
            double magnitude = Math.sqrt(axis.getX() * axis.getX() + axis.getY() * axis.getY());
            // double theta = Math.asin(axis.getY() / magnitude);
            // if (axis.getX() < 0) {
            //     theta *= -1;
            // }

            double dx = axis.getX() / magnitude;
            double dy = axis.getY() / magnitude;

            EditorObject geometry1 = null;

            EditorObject geometry2 = null;

            for (EditorObject maybeGeometry : LevelManager.getLevel().getScene()) {
                if (maybeGeometry instanceof Rectangle || maybeGeometry instanceof Circle) {
                    if (maybeGeometry.getAttribute("id").equals(getAttribute("body1"))) {
                        geometry1 = maybeGeometry;
                    } else if (maybeGeometry.getAttribute("id").equals(getAttribute("body2"))) {
                        geometry2 = maybeGeometry;
                    }
                }
            }

            if (geometry1 != null) {

                double size = 10 * LevelManager.getLevel().getZoom();

                double x = geometry1.getDouble("x");
                double y = geometry1.getDouble("y");

                double screenX = x * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double screenY = -y * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

                graphicsContext.save();

                //Affine t = graphicsContext.getTransform();
                //t.appendRotation(45, screenX, screenY);
                //graphicsContext.setTransform(t);

                graphicsContext.setLineWidth(LevelManager.getLevel().getZoom() * 5);
                graphicsContext.setStroke(Renderer.mechanics);

                graphicsContext.strokeRect(screenX - size / 2, screenY - size / 2, size, size);

                if (this == SelectionManager.getSelected()) {

                    graphicsContext.setStroke(Renderer.selectionOutline2);
                    graphicsContext.setLineWidth(1);
                    graphicsContext.setLineDashes(3);
                    graphicsContext.setLineDashOffset(0);
                    graphicsContext.strokeRect(screenX - size / 2, screenY - size / 2, size, size);
                    graphicsContext.setStroke(Renderer.selectionOutline);
                    graphicsContext.setLineWidth(1);
                    graphicsContext.setLineDashOffset(3);
                    graphicsContext.strokeRect(screenX - size / 2, screenY - size / 2, size, size);
                    graphicsContext.setLineDashes(0);

                }

                graphicsContext.restore();
            }

            if (geometry2 != null) {

                double size = 10 * LevelManager.getLevel().getZoom();

                double x = geometry2.getDouble("x");
                double y = geometry2.getDouble("y");

                if (geometry2.getParent() instanceof Compositegeom) {
                    x += geometry2.getParent().getDouble("x");
                    y += geometry2.getParent().getDouble("y");
                }

                double screenX = x * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double screenY = -y * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

                graphicsContext.save();

                //Affine t = graphicsContext.getTransform();
                //t.appendRotation(45, screenX, screenY);
                //graphicsContext.setTransform(t);

                graphicsContext.setFill(Renderer.mechanics);

                graphicsContext.fillRect(screenX - size * 2 / 2, screenY - size * 2 / 2, size * 2, size * 2);



                if (this == SelectionManager.getSelected()) {

                    graphicsContext.setStroke(Renderer.selectionOutline2);
                    graphicsContext.setLineWidth(1);
                    graphicsContext.setLineDashes(3);
                    graphicsContext.setLineDashOffset(0);
                    graphicsContext.strokeRect(screenX - size / 2, screenY - size / 2, size, size);
                    graphicsContext.setStroke(Renderer.selectionOutline);
                    graphicsContext.setLineWidth(1);
                    graphicsContext.setLineDashOffset(3);
                    graphicsContext.strokeRect(screenX - size / 2, screenY - size / 2, size, size);
                    graphicsContext.setLineDashes(0);

                }

                graphicsContext.restore();
                graphicsContext.save();

                double dst = Math.max(10000 * LevelManager.getLevel().getZoom(), 10000);

                double screenX2 = (x - dst * dx) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double screenY2 = (-y - dst * dy) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

                double screenX3 = (x + dst * dx) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double screenY3 = (-y + dst * dy) * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getZoom();

                graphicsContext.setLineWidth(LevelManager.getLevel().getZoom() * 5);
                graphicsContext.setStroke(Renderer.mechanics);

                graphicsContext.strokeLine(screenX2, screenY2, screenX3, screenY3);
                graphicsContext.restore();
            }
        }
    }

    public DragSettings mouseIntersection(double mX2, double mY2) {
        return DragSettings.NULL;
        /*

        if (LevelManager.getLevel().isShowGeometry()) {

            Position anchor = Position.parse(getAttribute("anchor"));
            double x = anchor.getX();
            double y = anchor.getY();

            double size = 7.5;
            double size2 = 2.5;

            Point2D rotated = rotate(new Point2D(mX2, mY2), Math.toRadians(45), new Point2D(x, -y));

            double mX = rotated.getX();
            double mY = rotated.getY();

            return ((mX > x - size && mX < x + size && mY > -y - size && mY < -y + size) && !(mX > x - size2 && mX < x + size2 && mY > -y - size2 && mY < -y + size2));
        } else {
            return false;
        }
    }
     */

}
