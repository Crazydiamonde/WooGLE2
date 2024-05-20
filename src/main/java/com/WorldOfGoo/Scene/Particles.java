package com.WorldOfGoo.Scene;

import java.util.ArrayList;

import com.WooGLEFX.EditorObjects.ObjectPosition;
import com.WooGLEFX.EditorObjects.ParticleGraphicsInstance;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.File.ResourceManagers.ParticleManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Particle.Ambientparticleeffect;
import com.WorldOfGoo.Particle.Particleeffect;
import com.WorldOfGoo.Particle._Particle;
import javafx.scene.image.Image;

public class Particles extends EditorObject {


    private final ArrayList<ArrayList<ParticleGraphicsInstance>> drawing = new ArrayList<>();
    public ArrayList<ArrayList<ParticleGraphicsInstance>> getDrawing() {
        return drawing;
    }


    private final ArrayList<Integer> counts = new ArrayList<>();
    public ArrayList<Integer> getCounts() {
        return counts;
    }


    public Particles(EditorObject _parent) {
        super(_parent, "particles", "scene\\particles");

        addAttribute("effect",  InputField.PARTICLES)                      .assertRequired();
        addAttribute("depth",   InputField.NUMBER)  .setDefaultValue("-20").assertRequired();
        addAttribute("pos",     InputField.POSITION).setDefaultValue("0,0");
        addAttribute("pretick", InputField.NUMBER)  .setDefaultValue("0");
        addAttribute("enabled", InputField.FLAG);

        addObjectPosition(new ObjectPosition(ObjectPosition.RECTANGLE_HOLLOW) {
            public double getX() {
                return getAttribute("pos").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("pos", x + "," + -getY());
            }
            public double getY() {
                return -getAttribute("pos").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("pos", getX() + "," + -y);
            }

            // TODO label width and height

        });

        setMetaAttributes(MetaEditorAttribute.parse("effect,pos,depth,pretick,enabled,"));

    }





    @Override
    public String getName() {
        return getAttribute("effect").stringValue();
    }



    // private Point2D lineIntersection(double x1, double y1, double m1, double x2, double y2, double m2) {
    //     double x = (m1 * x1 - m2 * x2 + y2 - y1) / (m1 - m2);
    //     return new Point2D(x, m1 * (x - x1) + y1);
    // }

    /*
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {


    }

    public DragSettings mouseIntersection(double mX2, double mY2) {
        if (LevelManager.getLevel().isShowParticles()) {
            Position pos = getPosition("pos");

            double x2 = pos.getX();
            double y2 = -pos.getY();

            Font font = Font.font("Arial", FontWeight.BOLD, 30);
            Text text = new Text(getString("effect"));
            text.setFont(font);
            double width1 = text.getLayoutBounds().getWidth();
            double height1 = text.getLayoutBounds().getHeight();

            if (width1 != 0 && height1 != 0) {
                if (mX2 > x2 - width1 / 2 && mX2 < x2 + width1 / 2 && mY2 > y2 - height1 * 1.25 && mY2 < y2 - height1 * 0.25) {
                    DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                    dragSettings.setInitialSourceX(mX2 - pos.getX());
                    dragSettings.setInitialSourceY(mY2 + pos.getY());
                    return dragSettings;
                }
            }
        }
        return DragSettings.NULL;
    }

     */

}
