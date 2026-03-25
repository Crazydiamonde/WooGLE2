package com.worldOfGoo2.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.TextComponent;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.gameData.animation.SimpleBinAnimation;
import com.woogleFX.gameData.font._Font;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.util.BinAnimationHelper;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Ball extends EditorObject {

    private enum BallState {
        Standing,
        Climbing,
        Falling,
        Dragging,
        Attached,
        State5,
        State6,
        State7,
        State8,
        State9,
        State10,
        State11,
        State12,
        State13,
        State14,
        State15,
    }

    public Ball(EditorObject parent, GameVersion version) {
        super(parent, version);

        addAttributeAdapter("shape", AttributeAdapter.childAttributeAdapter(this, "shape", "shape", InputField.NUMBER));
        addAttributeAdapter("blinkColor", AttributeAdapter.childAttributeAdapter(this, "blinkColor", "blinkColor", InputField.NUMBER));
        addAttributeAdapter("detonateParticleEffect", AttributeAdapter.childAttributeAdapter(this, "detonateParticleEffect", "detonateParticleEffect", InputField._2_PARTICLE_EFFECT_NAME));
        addAttributeAdapter("material", AttributeAdapter.childAttributeAdapter(this, "material", "material", InputField.STRING));
        addAttributeAdapter("popSoundId", AttributeAdapter.childAttributeAdapter(this, "popSoundId", "popSoundId", InputField.STRING));
        addAttributeAdapter("fireworksParticleEffect", AttributeAdapter.childAttributeAdapter(this, "fireworksParticleEffect", "fireworksParticleEffect", InputField.STRING));
        addAttributeAdapter("trailParticleEffect", AttributeAdapter.childAttributeAdapter(this, "trailParticleEffect", "trailParticleEffect", InputField.STRING));
        addAttributeAdapter("spawnType", AttributeAdapter.childAttributeAdapter(this, "spawnType", "spawnType", InputField.NUMBER));
        addAttributeAdapter("strandType", AttributeAdapter.childAttributeAdapter(this, "strandType", "strandType", InputField.NUMBER));
        addAttributeAdapter("bodyPart", AttributeAdapter.childAttributeAdapter(this, "bodyPart", "bodyPart", InputField.STRING));
        addAttributeAdapter("flashAnimation", AttributeAdapter.childAttributeAdapter(this, "flashAnimation", "flashAnimation", InputField.STRING));

        //addAttributeAdapter("shadowImageId", AttributeAdapter.childAttributeAdapter(this, "shadowImageId", "shadowImageId", InputField.STRING));
        addAttributeAdapter("strandImageId", AttributeAdapter.childAttributeAdapter(this, "strandImageId", "strandImageId", InputField.STRING));
        addAttributeAdapter("strandInactiveImageId", AttributeAdapter.childAttributeAdapter(this, "strandInactiveImageId", "strandInactiveImageId", InputField.STRING));
        addAttributeAdapter("strandInactiveOverlayImageId", AttributeAdapter.childAttributeAdapter(this, "strandInactiveOverlayImageId", "strandInactiveOverlayImageId", InputField.STRING));
        addAttributeAdapter("strandBurntImageId", AttributeAdapter.childAttributeAdapter(this, "strandBurntImageId", "strandBurntImageId", InputField.STRING));
        addAttributeAdapter("strandBackgroundImageId", AttributeAdapter.childAttributeAdapter(this, "strandBackgroundImageId", "strandBackgroundImageId", InputField.STRING));
        addAttributeAdapter("detachStrandImageId", AttributeAdapter.childAttributeAdapter(this, "detachStrandImageId", "detachStrandImageId", InputField.STRING));
        addAttributeAdapter("dragMarkerImageId", AttributeAdapter.childAttributeAdapter(this, "dragMarkerImageId", "dragMarkerImageId", InputField.STRING));
        addAttributeAdapter("detachMarkerImageId", AttributeAdapter.childAttributeAdapter(this, "detachMarkerImageId", "detachMarkerImageId", InputField.STRING));
        addAttributeAdapter("stainLiquidType", AttributeAdapter.childAttributeAdapter(this, "stainLiquidType", "stainLiquidType", InputField.STRING));
        //addAttributeAdapter("splatImageIds", AttributeAdapter.childAttributeAdapter(this, "splatImageIds", "splatImageIds", InputField.STRING));
        addAttributeAdapter("stableFluidsDensityFactor", AttributeAdapter.childAttributeAdapter(this, "stableFluidsDensityFactor", "stableFluidsDensityFactor", InputField.STRING));
        addAttributeAdapter("stableFluidsDensityRange", AttributeAdapter.childAttributeAdapter(this, "stableFluidsDensityRange", "stableFluidsDensityRange", InputField.STRING));
        //addAttributeAdapter("popSpawnItems", AttributeAdapter.childAttributeAdapter(this, "popSpawnItems", "popSpawnItems", InputField.STRING));
        addAttributeAdapter("popSpawnItemCountRange", AttributeAdapter.childAttributeAdapter(this, "popSpawnItemCountRange", "popSpawnItemCountRange", InputField.STRING));
        addAttributeAdapter("popSpawnItemRadiusRange", AttributeAdapter.childAttributeAdapter(this, "popSpawnItemRadiusRange", "popSpawnItemRadiusRange", InputField.STRING));
        addAttributeAdapter("popSpawnItemScaleRange", AttributeAdapter.childAttributeAdapter(this, "popSpawnItemScaleRange", "popSpawnItemScaleRange", InputField.STRING));
        addAttributeAdapter("strandShatterItem", AttributeAdapter.childAttributeAdapter(this, "strandShatterItem", "strandShatterItem", InputField.STRING));
        addAttributeAdapter("strandShatterParticleEffect", AttributeAdapter.childAttributeAdapter(this, "strandShatterParticleEffect", "strandShatterParticleEffect", InputField.STRING));
        //addAttributeAdapter("thrusterStableFluidsImage", AttributeAdapter.childAttributeAdapter(this, "thrusterStableFluidsImage", "thrusterStableFluidsImage", InputField.STRING));
        addAttributeAdapter("markerColor", AttributeAdapter.childAttributeAdapter(this, "markerColor", "markerColor", InputField.STRING));
        //addAttributeAdapter("deathParticleEffect", AttributeAdapter.childAttributeAdapter(this, "deathParticleEffect", "deathParticleEffect", InputField.STRING));
        //addAttributeAdapter("laserGradientStart", AttributeAdapter.childAttributeAdapter(this, "laserGradientStart", "laserGradientStart", InputField.STRING));
        //addAttributeAdapter("laserGradientEnd", AttributeAdapter.childAttributeAdapter(this, "laserGradientEnd", "laserGradientEnd", InputField.STRING));
        //addAttributeAdapter("laserOverrideImage", AttributeAdapter.childAttributeAdapter(this, "laserOverrideImage", "laserOverrideImage", InputField.STRING));


    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {

                int finalX = x;
                int finalY = y;

                addObjectComponent(new CircleComponent(this) {
                    @Override
                    public double getRadius() {
                        return getAttribute("width").doubleValue() / 2;
                    }

                    @Override
                    public void setRadius(double radius) {
                        setAttribute("width", radius * 2);
                    }

                    @Override
                    public double getX() {
                        return finalX;
                    }

                    @Override
                    public double getY() {
                        return finalY;
                    }

                    @Override
                    public double getDepth() {
                        return -10000;
                    }

                    @Override
                    public double getEdgeSize() {
                        return 0.005;
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
                        return Color.BLACK;
                    }
                });


                String stateText = BallState.values()[finalY * 4 + finalX].toString();
                Text text1 = new Text(stateText);
                text1.setFont(new Font(0.2));
                double width = text1.getLayoutBounds().getWidth();

                addObjectComponent(new TextComponent(this) {
                    @Override
                    public _Font getFont() {
                        return null;
                    }

                    @Override
                    public Font getOtherFont() {
                        return new Font(0.2);
                    }

                    @Override
                    public String getText() {
                        return stateText;
                    }

                    @Override
                    public double getX() {
                        return finalX - width / 2;
                    }

                    @Override
                    public double getY() {
                        return finalY - 0.2;
                    }

                    @Override
                    public double getDepth() {
                        return 0;
                    }

                    @Override
                    public Paint getColor() {
                        return Color.BLACK;
                    }

                });

            }
        }

        String animation = getAttribute("flashAnimation").stringValue();
        if (!animation.isEmpty()) {
            SimpleBinAnimation flashAnim = ResourceManager.getFlashAnim(asset.getResources(), animation, GameVersion.VERSION_WOG2);
            if (flashAnim != null) BinAnimationHelper.addBinAnimationAsObjectPositions(this, flashAnim, "", new BinAnimationHelper.BinAnimationInterface() {
                public double getX() {
                    return 0;
                }
                public void setX(double x) {

                }
                public double getY() {
                    return 0;
                }
                public void setY(double y) {

                }
                public double getScaleX() {
                    return getChildren("ballParts").get(0).getAttribute("scale").doubleValue() / 100;
                }
                public void setScaleX(double scaleX) {
                    getChildren("ballParts").get(0).setAttribute("scale", scaleX * 100);
                }
                public double getScaleY() {
                    return getChildren("ballParts").get(0).getAttribute("scale").doubleValue() / 100;
                }
                public void setScaleY(double scaleY) {
                    getChildren("ballParts").get(0).setAttribute("scale", scaleY * 100);
                }
                public double getRotation() {
                    return 0;
                }
                public void setRotation(double rotation) {

                }
                public double getDepth() {
                    return 0;
                }
            });
        }

    }

    @Override
    public String getName() {
        return getAttribute("name").stringValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[] { Part.class, StateAnimation.class, SoundEvent.class, ParticleEffect.class };
    }

    @Override
    public String[] getPossibleChildrenTypeIDs() {
        return new String[] { "ballParts", "stateAnimations", "soundEvents", "particleEffects" };
    }

}
