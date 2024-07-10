package com.worldOfGoo.scene;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.TextComponent;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.gameData.font.FontReader;
import com.woogleFX.gameData.font._Font;
import com.woogleFX.gameData.particle.ParticleGraphicsInstance;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Particles extends EditorObject {

    private final ArrayList<ArrayList<ParticleGraphicsInstance>> drawing = new ArrayList<>();
    public ArrayList<ArrayList<ParticleGraphicsInstance>> getDrawing() {
        return drawing;
    }


    private final ArrayList<Double> counts = new ArrayList<>();
    public ArrayList<Double> getCounts() {
        return counts;
    }


    public Particles(EditorObject _parent, GameVersion version) {
        super(_parent, "particles", version);

        addAttribute("effect",  InputField.PARTICLES)                      .assertRequired();
        addAttribute("depth",   InputField.NUMBER)  .setDefaultValue("-20").assertRequired();
        addAttribute("pos",     InputField.POSITION).setDefaultValue("0,0");
        addAttribute("pretick", InputField.NUMBER)  .setDefaultValue("0");
        addAttribute("enabled", InputField.FLAG);

        setMetaAttributes(MetaEditorAttribute.parse("effect,pos,depth,pretick,enabled,"));

        addObjectComponent(new TextComponent() {
            public _Font getFont() {
                if (FileManager.getGameDir(GameVersion.NEW) != null) {
                    try {
                        return ResourceManager.getFont(null, "FONT_SIGNPOST", GameVersion.NEW);
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                } else {
                    try {
                        return ResourceManager.getFont(null, "FONT_SIGNPOST", GameVersion.OLD);
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                }
            }

            public String getText() {
                return getAttribute("effect").stringValue();
            }

            public double getX() {
                return getAttribute("pos").positionValue().getX();
            }

            public void setX(double x) {
                setAttribute("pos", x + "," + getAttribute("pos").positionValue().getY());
            }

            public double getY() {
                return -getAttribute("pos").positionValue().getY() - 15;
            }

            public void setY(double y) {
                setAttribute("pos", getAttribute("pos").positionValue().getX() + ", " + -y);
            }

            public double getDepth() {
                return Depth.MECHANICS;
            }

            public double getScale() {
                return 0.3;
            }

            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowParticles() && LevelManager.getLevel().getVisibilitySettings().isShowLabels();
            }

        });


        addObjectComponent(new CircleComponent() {
            @Override
            public double getRadius() {
                return 4;
            }

            public double getX() {
                return getAttribute("pos").positionValue().getX();
            }

            public void setX(double x) {
                setAttribute("pos", x + "," + getAttribute("pos").positionValue().getY());
            }

            public double getY() {
                return -getAttribute("pos").positionValue().getY();
            }

            public void setY(double y) {
                setAttribute("pos", getAttribute("pos").positionValue().getX() + ", " + -y);
            }

            public double getDepth() {
                return Depth.MECHANICS;
            }

            @Override
            public double getEdgeSize() {
                return 1;
            }

            @Override
            public Paint getBorderColor() {
                return Color.rgb(191, 0, 255, 1.0);
            }

            @Override
            public boolean isEdgeOnly() {
                return false;
            }

            @Override
            public Paint getColor() {
                return Color.rgb(191, 0, 255, 0.5);
            }

            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowParticles();
            }

            public boolean isResizable() {
                return false;
            }

            public boolean isRotatable() {
                return false;
            }

        });

    }


    @Override
    public String getName() {
        return getAttribute("effect").stringValue();
    }

}
