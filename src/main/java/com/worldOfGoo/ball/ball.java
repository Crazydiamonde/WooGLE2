package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.TextComponent;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.gameData.font._Font;
import com.woogleFX.assets.GameVersion;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ball extends EditorObject {

    enum BallState {
        standing,
        walking,
        climbing,
        falling,
        dragging,
        attached,
        detaching,
        sleeping,
        pipe,
        tank,
        stuck,
        stuck_attached,
        stuck_detaching,
    }


    public ball(EditorObject _parent, GameVersion version) {
        super(_parent, version);
    }


    public double getSpacing() {
        return Double.parseDouble(getAttribute("shape").listValue()[1]) * 4.5;
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        for (BallState ballState : BallState.values()) {

            int finalX = ballState.ordinal() % 5;
            int finalY = ballState.ordinal() / 5;

            addObjectComponent(new CircleComponent(this) {
                public double getRadius() {
                    return Double.parseDouble(getAttribute("shape").listValue()[1]) / 2;
                }
                public double getX() {
                    return finalX * getSpacing();
                }
                public double getY() {
                    return finalY * getSpacing();
                }
                public double getDepth() {
                    return 10000;
                }
                public double getEdgeSize() {
                    return 0.5;
                }
                public Paint getBorderColor() {
                    return Color.BLUE;
                }
                public boolean isEdgeOnly() {
                    return true;
                }
                public Paint getColor() {
                    return Color.TRANSPARENT;
                }
                public boolean isVisible() {
                    return AssetManager.getVisibility("bounds") == 1;
                }
            });

            String stateText = ballState.toString();

            addObjectComponent(new TextComponent(this) {
                public _Font getFont() {
                    return null;
                }
                public Font getOtherFont() {
                    return new Font(getSpacing() / 8);
                }
                public String getText() {
                    return stateText;
                }
                public double getX() {
                    Text text1 = new Text(stateText);
                    text1.setFont(new Font(getSpacing() / 8));
                    double width = text1.getLayoutBounds().getWidth();
                    return finalX * getSpacing() - width / 2;
                }
                public double getY() {
                    return (finalY - 0.2) * getSpacing();
                }
                public double getDepth() {
                    return 0;
                }
                public Paint getColor() {
                    return Color.BLACK;
                }
                public boolean isVisible() {
                    return AssetManager.getVisibility("labels") == 1;
                }
            });

        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return (Class<? extends EditorObject>[]) new Class[] {
                detachstrand.class,
                marker.class,
                part.class,
                particles.class,
                shadow.class,
                sinvariance.class,
                sound.class,
                splat.class,
                strand.class
        };
    }

}
