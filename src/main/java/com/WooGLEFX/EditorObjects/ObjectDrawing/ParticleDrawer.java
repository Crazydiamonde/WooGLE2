package com.WooGLEFX.EditorObjects.ObjectDrawing;

import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.EditorObjects.ParticleGraphicsInstance;
import com.WooGLEFX.Engine.SelectionManager;
import com.WooGLEFX.File.ResourceManagers.ParticleManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.Fire;
import com.WorldOfGoo.Particle.Ambientparticleeffect;
import com.WorldOfGoo.Particle.Particleeffect;
import com.WorldOfGoo.Particle._Particle;
import com.WorldOfGoo.Scene.Particles;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;

import java.util.ArrayList;

public class ParticleDrawer {


    /*
    private static void drawParticleLabel(GraphicsContext graphicsContext, Particles particleObject) {

        Font font = Font.font("Arial", FontWeight.BOLD, 30 * LevelManager.getLevel().getZoom());

        Position pos = particleObject.getAttribute("pos").positionValue();
        double screenX2 = pos.getX() * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
        double screenY2 = -pos.getY() * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

        Text text = new Text(particleObject.getAttribute("effect").stringValue());
        text.setFont(font);
        double width = text.getLayoutBounds().getWidth();
        double height = text.getLayoutBounds().getHeight();

        graphicsContext.setFill(Paint.valueOf("A81CFF"));
        graphicsContext.setFont(font);
        graphicsContext.fillText(particleObject.getAttribute("effect").stringValue(), screenX2 - width / 2, screenY2 - height / 2);

        if (particleObject == SelectionManager.getSelected()) {

            graphicsContext.setLineWidth(1);
            graphicsContext.setLineDashes(3);

            graphicsContext.setStroke(Renderer.selectionOutline2);
            graphicsContext.setLineDashOffset(0);
            graphicsContext.strokeRect(screenX2 - width / 2, screenY2 - height * 1.25, width, height);

            graphicsContext.setStroke(Renderer.selectionOutline);
            graphicsContext.setLineDashOffset(3);
            graphicsContext.strokeRect(screenX2 - width / 2, screenY2 - height * 1.25, width, height);

            graphicsContext.restore();

        }

    }
    */

}
