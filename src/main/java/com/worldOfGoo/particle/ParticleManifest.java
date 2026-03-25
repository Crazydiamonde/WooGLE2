package com.worldOfGoo.particle;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.assets.GameVersion;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ParticleManifest extends EditorObject {

    public ParticleManifest(EditorObject parent, GameVersion version) {
        super(parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new RectangleComponent(this) {

            @Override
            public double getWidth() {
                return 1000;
            }

            @Override
            public double getHeight() {
                return 1000;
            }

            @Override
            public double getX() {
                return 0;
            }

            @Override
            public double getY() {
                return 0;
            }

            @Override
            public double getDepth() {
                return 0;
            }

            @Override
            public Depth.Layer getGlobalLayer() {
                return Depth.Layer.SCENE_BG;
            }

            @Override
            public double getEdgeSize() {
                return 1.0;
            }

            @Override
            public Paint getBorderColor() {
                return Color.BLACK;
            }

            @Override
            public boolean isEdgeOnly() {
                return true;
            }

            @Override
            public Paint getColor() {
                return Color.TRANSPARENT;
            }

            @Override
            public boolean isSelectable() {
                return false;
            }

        });

    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[] { particleeffect.class, ambientparticleeffect.class };
    }

}
