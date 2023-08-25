package com.WorldOfGoo.Scene;

import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Structures.EditorAttribute;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import javafx.scene.canvas.GraphicsContext;

public class Scene extends EditorObject {
    public Scene(EditorObject _parent) {
        super(_parent);
        setRealName("scene");
        addAttribute("minx", "-500", InputField.NUMBER, false);
        addAttribute("miny", "0", InputField.NUMBER, false);
        addAttribute("maxx", "500", InputField.NUMBER, false);
        addAttribute("maxy", "1000", InputField.NUMBER, false);
        addAttribute("backgroundcolor", "0,0,0", InputField.COLOR, true);
        setNameAttribute(new EditorAttribute(this, "", "", "", new InputField("", InputField.NULL), false));
        setMetaAttributes(MetaEditorAttribute.parse("backgroundcolor,minx,miny,maxx,maxy,"));
    }

    @Override
    public String[] getPossibleChildren() {
        return new String[]{"SceneLayer", "button", "buttongroup", "circle", "compositegeom", "hinge", "label", "line", "linearforcefield", "motor", "particles", "radialforcefield", "rectangle", "slider"};
    }


    @Override
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {
        double sceneLeft = Double.parseDouble(getAttribute("minx"));
        double sceneTop = -Double.parseDouble(getAttribute("maxy"));
        double sceneRight = Double.parseDouble(getAttribute("maxx"));
        double sceneBottom = -Double.parseDouble(getAttribute("miny"));

        graphicsContext.setStroke(Renderer.selectionOutline2);
        graphicsContext.setLineWidth(0.9 * Main.getLevel().getZoom());
        graphicsContext.setLineDashes(3 * Main.getLevel().getZoom());
        graphicsContext.setLineDashOffset(0);
        graphicsContext.strokeRect(sceneLeft * Main.getLevel().getZoom() + Main.getLevel().getOffsetX(), sceneTop * Main.getLevel().getZoom() + Main.getLevel().getOffsetY(), (sceneRight - sceneLeft) * Main.getLevel().getZoom(), (sceneBottom - sceneTop) * Main.getLevel().getZoom());
        graphicsContext.setStroke(Renderer.selectionOutline);
        graphicsContext.setLineWidth(Main.getLevel().getZoom());
        graphicsContext.setLineDashOffset(3 * Main.getLevel().getZoom());
        graphicsContext.strokeRect(sceneLeft * Main.getLevel().getZoom() + Main.getLevel().getOffsetX(), sceneTop * Main.getLevel().getZoom() + Main.getLevel().getOffsetY(), (sceneRight - sceneLeft) * Main.getLevel().getZoom(), (sceneBottom - sceneTop) * Main.getLevel().getZoom());
        graphicsContext.setLineDashes(0);
    }
}
