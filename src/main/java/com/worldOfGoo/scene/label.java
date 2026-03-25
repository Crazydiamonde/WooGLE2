package com.worldOfGoo.scene;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.gameData.font._Font;
import com.woogleFX.editorObjects.objectComponents.TextComponent;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.text.string;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class label extends EditorObject {

    public label(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        addObjectComponent(new TextComponent(this) {

            @Override
            public Paint getColor() {
                return Color.WHITE;
            }

            @Override
            public _Font getFont() {
                return ResourceManager.getFont(AssetManager.getAsset().getResources(), getAttribute("font").stringValue(), AssetManager.getAsset().getVersion());
            }

            @Override
            public String getText() {
                for (EditorObject EditorObject : AssetManager.getAsset().getStrings().getChildren()) {
                    if (EditorObject instanceof string string) {
                        if (string.getAttribute("id").stringValue().equals(getAttribute("text").stringValue())) {
                            return string.getAttribute("text").stringValue();
                        }
                    }
                }
                return null;
            }

            @Override
            public double getX() {
                return getAttribute("x").doubleValue();
            }

            @Override
            public double getY() {
                return -getAttribute("y").doubleValue();
            }

            @Override
            public void setX(double x) {
                setAttribute("x", x);
            }

            @Override
            public void setY(double y) {
                setAttribute("y", -y);
            }

            @Override
            public double getDepth() {
                return 0;
            }

            @Override
            public double getScale() {
                return getAttribute("scale").doubleValue();
            }

            @Override
            public double getRotation() {
                return -Math.toRadians(getAttribute("rotation").doubleValue());
            }

            @Override
            public boolean isVisible() {
                return AssetManager.getVisibility("labels") == 1;
            }

        });

    }

    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
