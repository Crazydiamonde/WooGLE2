package com.woogleFX.engine.inputEvents;

import com.woogleFX.assets.Asset;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.FXContainers;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.engine.AssetManager;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;

public class MouseWheelMovedManager {

    /** Called whenever the mouse wheel is scrolled. */
    public static void mouseWheelMoved(ScrollEvent e) {

        Asset asset = AssetManager.getAsset();
        if (asset == null) return;

        // If the mouse was scrolled out the editor window, return
        SplitPane splitPane = FXContainers.getSplitPane();
        double canvasWidth = splitPane.getDividerPositions()[0] * splitPane.getWidth();
        if (e.getX() > canvasWidth) return;

        // Calculate the new translation and scale.
        double amt = Math.pow(1.25, (e.getDeltaY() / 40));

        double oldTranslateX = asset.getOffsetX();
        double oldTranslateY = asset.getOffsetY();

        double oldScaleX = asset.getZoom();
        double oldScaleY = asset.getZoom();

        double mouseX = e.getX();
        double mouseY = e.getY() - FXCanvas.getMouseYOffset();

        if (asset.isScaleTooFar(oldScaleX * amt, oldScaleY * amt)) return;

        double newScaleX = oldScaleX * amt;
        double newScaleY = oldScaleY * amt;

        double newTranslateX = ((oldTranslateX - mouseX) * amt + mouseX);
        double newTranslateY = ((oldTranslateY - mouseY) * amt + mouseY);

        // Transform the canvas according to the updated translation and scale.
        Renderer.t = new Affine();
        Renderer.t.appendTranslation(newTranslateX, newTranslateY);
        Renderer.t.appendScale(newScaleX, newScaleY);

        asset.setOffsetX(newTranslateX);
        asset.setOffsetY(newTranslateY);
        asset.setZoom(newScaleX);

    }

}
