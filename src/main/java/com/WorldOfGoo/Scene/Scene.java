package com.WorldOfGoo.Scene;

import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.canvas.GraphicsContext;

public class Scene extends EditorObject {

    public Scene(EditorObject _parent) {
        super(_parent, "scene", "scene\\scene");

        addAttribute("minx", InputField.NUMBER)          .setDefaultValue("-500");
        addAttribute("miny", InputField.NUMBER)          .setDefaultValue("0");
        addAttribute("maxx", InputField.NUMBER)          .setDefaultValue("500");
        addAttribute("maxy", InputField.NUMBER)          .setDefaultValue("1000");
        addAttribute("backgroundcolor", InputField.COLOR).setDefaultValue("0,0,0").assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("backgroundcolor,minx,miny,maxx,maxy,"));

    }


    @Override
    public String[] getPossibleChildren() {
        return new String[]{ "SceneLayer", "button", "buttongroup", "circle", "compositegeom", "hinge", "label", "line", "linearforcefield", "motor", "particles", "radialforcefield", "rectangle", "slider" };
    }


    public void draw(GraphicsContext graphicsContext) {
        double sceneLeft = getAttribute("minx").doubleValue();
        double sceneTop = -getAttribute("maxy").doubleValue();
        double sceneRight = getAttribute("maxx").doubleValue();
        double sceneBottom = -getAttribute("miny").doubleValue();

        graphicsContext.setStroke(Renderer.selectionOutline2);
        graphicsContext.setLineWidth(0.9 * LevelManager.getLevel().getZoom());
        graphicsContext.setLineDashes(3 * LevelManager.getLevel().getZoom());
        graphicsContext.setLineDashOffset(0);
        graphicsContext.strokeRect(sceneLeft * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX(), sceneTop * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY(), (sceneRight - sceneLeft) * LevelManager.getLevel().getZoom(), (sceneBottom - sceneTop) * LevelManager.getLevel().getZoom());
        graphicsContext.setStroke(Renderer.selectionOutline);
        graphicsContext.setLineWidth(LevelManager.getLevel().getZoom());
        graphicsContext.setLineDashOffset(3 * LevelManager.getLevel().getZoom());
        graphicsContext.strokeRect(sceneLeft * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX(), sceneTop * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY(), (sceneRight - sceneLeft) * LevelManager.getLevel().getZoom(), (sceneBottom - sceneTop) * LevelManager.getLevel().getZoom());
        graphicsContext.setLineDashes(0);
    }

}
