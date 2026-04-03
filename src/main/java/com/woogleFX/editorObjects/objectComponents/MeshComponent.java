package com.woogleFX.editorObjects.objectComponents;

import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.AssetManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.transform.Affine;

public abstract class MeshComponent extends ObjectComponent {

    public MeshComponent(EditorObject editorObject) {
        super(editorObject);
    }


    public record Face(double[] xPositions, double[] yPositions, int vertexCount) {}
    
    public abstract Face[] getMesh();

    public final void clearCachedMesh() {
        cachedMesh = null;
    }

    public abstract Image getImage();

    public abstract double getX();

    public abstract double getY();

    public abstract double getScaleX();

    public abstract double getScaleY();

    public abstract double getDepth();

    private Face[] cachedMesh;
    
    @Override
    public void draw(GraphicsContext graphicsContext) {
        if (cachedMesh == null)
            cachedMesh = getMesh();
        
        Image image = getImage();
        if (image == null) return;

        double offsetX = AssetManager.getAsset().getOffsetX();
        double offsetY = AssetManager.getAsset().getOffsetY();
        double zoom = AssetManager.getAsset().getZoom();

        graphicsContext.save();

        Affine t = graphicsContext.getTransform();
        t.appendTranslation(offsetX, offsetY);
        t.appendScale(zoom, zoom);
        graphicsContext.setTransform(t);

        graphicsContext.setFill(new ImagePattern(image, 0, 0, image.getWidth() * getScaleX(), image.getHeight() * getScaleY(), false));

        // fill triangles
        for (int i = 0; i < cachedMesh.length; i++) {
            graphicsContext.fillPolygon(cachedMesh[i].xPositions, cachedMesh[i].yPositions, cachedMesh[i].vertexCount);
        }
        
        graphicsContext.restore();

    }

    @Override
    public void drawSelectionOutline(GraphicsContext graphicsContext) {

    }

    @Override
    public DragSettings mouseIntersection(double mouseX, double mouseY) {
        return DragSettings.NULL;
    }

    @Override
    public DragSettings mouseIntersectingCorners(double mouseX, double mouseY) {
        return DragSettings.NULL;
    }

}
