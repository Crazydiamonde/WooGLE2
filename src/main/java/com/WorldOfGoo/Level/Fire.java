package com.WorldOfGoo.Level;

import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WorldOfGoo.Scene.Compositegeom;
import com.WorldOfGoo.Scene.Particles;
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

}
