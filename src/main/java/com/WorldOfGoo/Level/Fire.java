package com.WorldOfGoo.Level;

import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WorldOfGoo.Scene.Compositegeom;
import com.WorldOfGoo.Scene.Particles;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class Fire extends EditorObject {

    private Particles particleEffect;

    public Fire(EditorObject _parent) {
        super(_parent);
        setRealName("fire");
        addAttribute("depth", "0", InputField.NUMBER, true);
        addAttribute("particles", "", InputField.ANY, true);
        addAttribute("x", "0", InputField.NUMBER, true);
        addAttribute("y", "0", InputField.NUMBER, true);
        addAttribute("radius", "50", InputField.NUMBER, true);
        setNameAttribute(getAttribute2("particles"));
        setMetaAttributes(MetaEditorAttribute.parse("x,y,radius,particles,depth,"));
    }

    @Override
    public void update() {
        if (Main.getLevel().isShowParticles()) {
            for (EditorObject particle : Main.getParticles()) {
                if (particle.getAttribute("name") != null && particle.getAttribute("name").equals(getAttribute("particles"))) {
                    particleEffect = new Particles(null);
                    particleEffect.setAttribute("pos", getAttribute("x") + "," + getAttribute("y"));
                    particleEffect.setAttribute("depth", getAttribute("depth"));
                    particleEffect.setAttribute("effect", getAttribute("particles"));
                    particleEffect.update();
                }
            }
        }
        setChangeListener("x", (observableValue, s, t1) -> particleEffect.setAttribute("pos", getAttribute("x") + "," + getAttribute("y")));
        setChangeListener("y", (observableValue, s, t1) -> particleEffect.setAttribute("pos", getAttribute("x") + "," + getAttribute("y")));
    }

    @Override
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {
        if (particleEffect != null && Main.getLevel().isShowParticles()) {
            particleEffect.draw(graphicsContext, imageGraphicsContext);
        }
        if (Main.getLevel().isShowGraphics()) {
            double x2 = Double.parseDouble(getAttribute("x"));
            double y2 = Double.parseDouble(getAttribute("y"));

            double radius = Double.parseDouble(getAttribute("radius"));

            double screenX = (x2) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX() - radius * Main.getLevel().getZoom();
            double screenY = (-y2) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY() - radius * Main.getLevel().getZoom();

            graphicsContext.setFill(Renderer.transparentRed);
            graphicsContext.fillOval(screenX + Main.getLevel().getZoom() / 2, screenY + Main.getLevel().getZoom() / 2, (radius - 0.5) * 2 * Main.getLevel().getZoom(), (radius - 0.5) * 2 * Main.getLevel().getZoom());
            graphicsContext.setStroke(Renderer.solidRed);
            graphicsContext.setLineWidth(Main.getLevel().getZoom());
            graphicsContext.strokeOval(screenX + Main.getLevel().getZoom() / 2, screenY + Main.getLevel().getZoom() / 2, (radius - 0.5) * 2 * Main.getLevel().getZoom(), (radius - 0.5) * 2 * Main.getLevel().getZoom());

            if (this == Main.getSelected()) {
                graphicsContext.setStroke(Renderer.selectionOutline2);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashes(3);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.strokeRect(screenX, screenY, radius * 2 * Main.getLevel().getZoom(), radius * 2 * Main.getLevel().getZoom());
                graphicsContext.setStroke(Renderer.selectionOutline);
                graphicsContext.setLineWidth(1);
                graphicsContext.setLineDashOffset(3);
                graphicsContext.strokeRect(screenX, screenY, radius * 2 * Main.getLevel().getZoom(), radius * 2 * Main.getLevel().getZoom());
                graphicsContext.setLineDashes(0);

                graphicsContext.strokeRect(screenX - 4, screenY + radius * Main.getLevel().getZoom() - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * Main.getLevel().getZoom() - 4, screenY + radius * 2 * Main.getLevel().getZoom() - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * Main.getLevel().getZoom() - 4, screenY - 4, 8, 8);
                graphicsContext.strokeRect(screenX + radius * 2 * Main.getLevel().getZoom() - 4, screenY + radius * Main.getLevel().getZoom() - 4, 8, 8);
            }
        }
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {
        if (Main.getLevel().isShowGeometry()) {
            double x2 = Double.parseDouble(getAttribute("x"));
            double y2 = Double.parseDouble(getAttribute("y"));

            double radius = Double.parseDouble(getAttribute("radius"));

            if ((mX2 - x2) * (mX2 - x2) + (mY2 + y2) * (mY2 + y2) < radius * radius) {
                DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                dragSettings.setInitialSourceX(mX2 - x2);
                dragSettings.setInitialSourceY(mY2 + y2);
                return dragSettings;
            } else {
                return new DragSettings(DragSettings.NONE);
            }
        } else {
            return new DragSettings(DragSettings.NONE);
        }
    }

    @Override
    public DragSettings mouseIntersectingCorners(double mX2, double mY2) {
        if (Main.getLevel().isShowGeometry()) {
            double x2 = Double.parseDouble(getAttribute("x"));
            double y2 = Double.parseDouble(getAttribute("y"));

            double radius = Double.parseDouble(getAttribute("radius"));

            Point2D left = new Point2D(x2 - radius, y2);
            Point2D top = new Point2D(x2, y2 + radius);
            Point2D right = new Point2D(x2 + radius, y2);
            Point2D bottom = new Point2D(x2, y2 - radius);
            top = new Point2D((top.getX()), (-top.getY()));
            left = new Point2D((left.getX()), (-left.getY()));
            right = new Point2D((right.getX()), (-right.getY()));
            bottom = new Point2D((bottom.getX()), (-bottom.getY()));
            double distance = 4 / Main.getLevel().getZoom();

            DragSettings resizeSettings = new DragSettings(DragSettings.RESIZE);

            boolean resize = false;

            if (mX2 > left.getX() - distance && mX2 < left.getX() + distance && mY2 > left.getY() - distance && mY2 < left.getY() + distance) {
                resize = true;
            }
            if (mX2 > top.getX() - distance && mX2 < top.getX() + distance && mY2 > top.getY() - distance && mY2 < top.getY() + distance) {
                resize = true;
            }
            if (mX2 > right.getX() - distance && mX2 < right.getX() + distance && mY2 > right.getY() - distance && mY2 < right.getY() + distance) {
                resize = true;
            }
            if (mX2 > bottom.getX() - distance && mX2 < bottom.getX() + distance && mY2 > bottom.getY() - distance && mY2 < bottom.getY() + distance) {
                resize = true;
            }

            if (resize) {
                resizeSettings.setInitialSourceX(0);
                resizeSettings.setInitialSourceY(0);
                resizeSettings.setAnchorX(0);
                resizeSettings.setAnchorY(0);
                return resizeSettings;
            }
        }
        return new DragSettings(DragSettings.NONE);
    }

    @Override
    public void resizeFromMouse(double mouseX, double mouseY, double resizeDragSourceX, double resizeDragSourceY, double resizeDragAnchorX, double resizeDragAnchorY) {
        double x2 = Double.parseDouble(getAttribute("x"));
        double y2 = -Double.parseDouble(getAttribute("y"));
        setAttribute("radius", Math.sqrt((mouseX - x2) * (mouseX - x2) + (mouseY - y2) * (mouseY - y2)));
    }

}
