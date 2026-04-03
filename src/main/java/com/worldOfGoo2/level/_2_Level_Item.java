package com.worldOfGoo2.level;

import com.woogleFX.assets.wog2.WOG2Item.WOG2Item;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.ImageUtility;
import com.woogleFX.editorObjects._2_Positionable;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import com.woogleFX.editorObjects.attributes.dataTypes.Color;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.TextComponent;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.gameData.animation.SimpleBinAnimation;
import com.woogleFX.gameData.font._Font;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.items._2_Item;
import com.worldOfGoo2.items._Object;
import com.worldOfGoo2.util.BallInstanceHelper;
import com.worldOfGoo2.util.BinAnimationHelper;
import com.worldOfGoo2.util.ItemHelper;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.*;

public class _2_Level_Item extends _2_Positionable {

    private _2_Item item;
    public _2_Item getItem() {
        return item;
    }


    private final Map<Integer, Integer> randomizationIndices = new HashMap<>();


    public EditorAttribute getUserVariable(String name) {
        for (EditorObject child : getChildren())
            if (child instanceof UserVariable && child.getName().equals(name))
                return child.getAttribute("value");
        return null;
    }


    private final AttributeAdapter[] attributeAdapters2;


    public _2_Level_Item(EditorObject parent, GameVersion version) {
        super(parent, version);

        addAttributeAdapter("scale", AttributeAdapter.pointAttributeAdapter(this, "scale", "scale"));

        EditorAttribute temp = new EditorAttribute("type", InputField._2_ITEM_TYPE, this).assertRequired();
        addAttributeAdapter("type", new AttributeAdapter("type") {

            @Override
            public EditorAttribute getValue() {

                if (getAttribute2("type").stringValue().isEmpty()) return temp;
                temp.setValue(ItemHelper.getItemActualName(getAttribute2("type").stringValue()));
                return temp;

            }

            @Override
            public void setValue(String value) {
                temp.setValue(value);
                _2_Item item1;
                try {
                    item1 = WOG2Item.assetSelector.openInstance(value, GameVersion.VERSION_WOG2).getItem();
                    if (item1 == null) return;
                } catch (IOException e) {
                    ErrorAlarm.show(e);
                    return;
                }
                setAttribute2("type", item1.getAttribute("uuid").stringValue());
                updateImage();
                refreshUserVariables();
                if (Arrays.stream(getObjectComponents()).anyMatch(e -> AssetManager.getAsset().isSelected(e))) {
                    FXPropertiesView.changeTableView(AssetManager.getAsset().getSelectedObjects());
                }
            }

        });

        attributeAdapters2 = getAttributeAdapters().values().toArray(new AttributeAdapter[0]);

    }


    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        EditorObject scale = getChildren("scale").get(0);
        scale.getAttribute("x").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("scale", newValue + "," + getAttribute2("scale").positionValue().getY()));
        scale.getAttribute("y").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("scale", getAttribute2("scale").positionValue().getX() + "," + newValue));
        setAttribute2("scale", scale.getAttribute("x").stringValue() + "," + scale.getAttribute("y").stringValue());

        randomizationIndices.put(-1, 0);
        for (EditorObject object : getChildren("objects")) {
            int randGroup = object.getAttribute("randomizationGroup").intValue();
            randomizationIndices.merge(randGroup, 1, Integer::sum);
            randomizationIndices.merge(-1, 1, Integer::sum);
        }
        randomizationIndices.replaceAll((k, v) -> (int) (Math.random() * randomizationIndices.get(k)));

        updateImage();

        refreshUserVariables();

    }


    private void refreshUserVariables() {
        EditorObject obj = this;

        EditorAttribute[] attributes = new EditorAttribute[24];
        System.arraycopy(getAttributes(), 0, attributes, 0, 24);
        setAttributes(attributes);

        getAttributeAdapters().clear();
        addAttributeAdapter("pos", attributeAdapters2[0]);
        addAttributeAdapter("scale", attributeAdapters2[1]);
        addAttributeAdapter("type", attributeAdapters2[2]);

        ArrayList<String> values = new ArrayList<>();
        for (EditorObject userVariable : getChildren("userVariables")) {
            values.add(userVariable.getAttribute("value").stringValue());
            getChildren().remove(userVariable);
        }

        MetaEditorAttribute userVariablesAttribute = getMetaAttributes().get(10);
        userVariablesAttribute.getChildren().clear();

        if (item == null) return;

        int i = 0;
        for (EditorObject ignored : item.getChildren("userVariables")) {
            EditorObject userVariable2 = ObjectCreator.create(UserVariable.class, this, "userVariables", GameVersion.VERSION_WOG2);
            if (i < values.size()) userVariable2.setAttribute("value", values.get(i));
            i++;
        }

        List<EditorObject> userVariables = getItem().getChildren("userVariables");
        
        i = 0;
        for (EditorObject child : getChildren("userVariables")) {

            EditorAttribute[] newAttributes = new EditorAttribute[getAttributes().length + 1];
            System.arraycopy(getAttributes(), 0, newAttributes, 0, getAttributes().length);
            newAttributes[getAttributes().length] = new EditorAttribute(child.getName(), InputField._2_CHILD_HIDDEN, this);
            
            if (userVariables.get(i).getAttribute("type").intValue() == 4) {
                EditorAttribute attribute = new EditorAttribute(child.getName(), InputField._2_BALL_TYPE_USERVAR, child);
                addAttributeAdapter(child.getName(),
                    BallInstanceHelper.ballTypeAttributeAdapter(child, child.getName(), "value", attribute));
                
                child.addAttributeAdapter(child.getName(),
                    BallInstanceHelper.ballTypeAttributeAdapter(child, child.getName(), "value", attribute));
                
                attribute.addChangeListener((observable, oldValue, newValue) -> {
                    setAttribute(child.getName(), newValue);
                });
            } else {
                addAttributeAdapter(child.getName(), new AttributeAdapter(child.getName()) {
                    @Override
                    public EditorAttribute getValue() {
                        EditorAttribute editorAttribute = new EditorAttribute(child.getName(), InputField.STRING, obj);
                        editorAttribute.setValue(child.getAttribute("value").stringValue());
                        return editorAttribute;
                    }
    
                    @Override
                    public void setValue(String value) {
                        child.setAttribute("value", value);
                    }
                });
            }

            MetaEditorAttribute userVariableAttribute = new MetaEditorAttribute();
            userVariableAttribute.setName(child.getName());
            userVariablesAttribute.getChildren().add(userVariableAttribute);
            i++;
        }

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }


    private static double lerp(double a, double b, double c) {
        return a + (b - a) * c;
    }


    private static float reverseInterpolate(float a, float b, float c) {
        if (b > a) {
            return (c - a) / (b - a);
        } else if (b == a) {
            return a;
        } else {
            return (c - b) / (a - b);
        }
    }

    @Override
    public void update() {
        updateImage();
    }


    private void updateImage() {

        if (AssetManager.getAsset() == null) return;

        if (!getAttribute2("type").stringValue().isEmpty()) {
            try {
                WOG2Item wog2item = WOG2Item.assetSelector.openInstance(getAttribute("type").stringValue(), GameVersion.VERSION_WOG2);
                if (wog2item == null) return;
                item = wog2item.getItem();
            } catch (IOException e) {
                ErrorAlarm.show(e);
                return;
            }
            refreshObjectPositions();
        }

    }


    private static ArrayList<_Object> orderPartsByLayer(ArrayList<EditorObject> objects) {

        ArrayList<_Object> orderedParts = new ArrayList<>();

        for (EditorObject EditorObject : objects) {

            if (EditorObject instanceof _Object part) {

                double layer = part.getAttribute("depthOffset").doubleValue();
                int i = 0;

                while (i < orderedParts.size() && orderedParts.get(i).getAttribute("depthOffset").doubleValue() <= layer) i++;

                orderedParts.add(i, part);

            }

        }

        return orderedParts;

    }


    public void refreshObjectPositions() {

        clearObjectComponents();

        boolean ok = false;
        if (item != null) {
            for (_Object part : orderPartsByLayer(item.getChildren())) {
                if (addPartAsObjectPosition(part)) ok = true;
            }
        }

        if (!ok) {

            addObjectComponent(new TextComponent(this) {

                @Override
                public Paint getColor() {
                    return javafx.scene.paint.Color.WHITE;
                }

                @Override
                public _Font getFont() {
                    return null;
                }

                @Override
                public Font getOtherFont() {
                    return new Font("Consolas", 0.5);
                }

                @Override
                public String getText() {
                    return getAttribute("type").stringValue();
                }

                @Override
                public double getX() {
                    return getPosition().getX() + 0.2;
                }

                @Override
                public double getY() {
                    return -getPosition().getY() + 0.16875;
                }

                @Override
                public double getDepth() {
                    return Depth.ITEMS;
                }

                @Override
                public boolean isVisible() {
                    return shouldShow() && AssetManager.getVisibility("graphics") == 1;
                }

                @Override
                public boolean isResizable() {
                    return false;
                }

                @Override
                public boolean isRotatable() {
                    return false;
                }
            });
            addObjectComponent(new CircleComponent(this) {
                @Override
                public Paint getColor() {
                    return new javafx.scene.paint.Color(1.0, 1.0, 1.0, 1.0);
                }

                @Override
                public double getEdgeSize() {
                    return 0.1;
                }

                @Override
                public Paint getBorderColor() {
                    return new javafx.scene.paint.Color(1.0, 1.0, 1.0, 1.0);
                }

                @Override
                public boolean isEdgeOnly() {
                    return false;
                }

                @Override
                public double getRadius() {
                    return 0.1;
                }

                @Override
                public double getX() {
                    return getPosition().getX();
                }

                @Override
                public void setX(double x) {
                    setPosition(x, getPosition().getY());
                }

                @Override
                public double getY() {
                    return -getPosition().getY();
                }

                @Override
                public void setY(double y) {
                    setPosition(getPosition().getX(), -y);
                }

                @Override
                public double getDepth() {
                    return Depth.ITEMS;
                }

                @Override
                public boolean isVisible() {
                    return shouldShow() && AssetManager.getVisibility("graphics") == 1;
                }

                @Override
                public boolean isResizable() {
                    return false;
                }

                @Override
                public boolean isRotatable() {
                    return false;
                }
            });
            addObjectComponent(new CircleComponent(this) {
                @Override
                public Paint getColor() {
                    return new javafx.scene.paint.Color(1.0, 1.0, 1.0, 1.0);
                }

                @Override
                public double getEdgeSize() {
                    return 0.0125;
                }

                @Override
                public Paint getBorderColor() {
                    return new javafx.scene.paint.Color(0.0, 0.0, 0.0, 1.0);
                }

                @Override
                public boolean isEdgeOnly() {
                    return false;
                }

                @Override
                public double getRadius() {
                    return 0.1;
                }

                @Override
                public double getX() {
                    return getPosition().getX();
                }

                @Override
                public void setX(double x) {
                    setPosition(x, getPosition().getY());
                }

                @Override
                public double getY() {
                    return -getPosition().getY();
                }

                @Override
                public void setY(double y) {
                    setPosition(getPosition().getX(), -y);
                }

                @Override
                public double getDepth() {
                    return Depth.ITEMS;
                }

                @Override
                public boolean isVisible() {
                    return shouldShow() && AssetManager.getVisibility("graphics") == 1;
                }

                @Override
                public boolean isResizable() {
                    return false;
                }

                @Override
                public boolean isRotatable() {
                    return false;
                }
            });

        }

        if (getItem() != null) {

            String animation = getItem().getAttribute("animationName").stringValue();
            if (!animation.isEmpty()) {
                SimpleBinAnimation flashAnim = ResourceManager.getFlashAnim(null, animation, GameVersion.VERSION_WOG2);
                if (flashAnim != null) BinAnimationHelper.addBinAnimationAsObjectPositions(this, flashAnim, getItem().getAttribute("animationAlias").stringValue(), new BinAnimationHelper.BinAnimationInterface() {
                    public double getX() {
                        double localX = item.getChild("animationLocalPosition").getAttribute("x").doubleValue();
                        double localY = -item.getChild("animationLocalPosition").getAttribute("y").doubleValue();
                        double scaleX = getChild("scale").getAttribute("x").doubleValue() * item.getChild("animationLocalScale").getAttribute("x").doubleValue();
                        double scaleY = getChild("scale").getAttribute("y").doubleValue() * item.getChild("animationLocalScale").getAttribute("y").doubleValue();
                        double rotation = -getAttribute("rotation").doubleValue();
                        return getPosition().getX() + localX * scaleX * Math.cos(rotation) - localY * scaleY * Math.sin(rotation);
                    }
                    public void setX(double x) {
                        double localX = item.getChild("animationLocalPosition").getAttribute("x").doubleValue();
                        double localY = -item.getChild("animationLocalPosition").getAttribute("y").doubleValue();
                        double scaleX = getChild("scale").getAttribute("x").doubleValue() * item.getChild("animationLocalScale").getAttribute("x").doubleValue();
                        double scaleY = getChild("scale").getAttribute("y").doubleValue() * item.getChild("animationLocalScale").getAttribute("y").doubleValue();
                        double rotation = -getAttribute("rotation").doubleValue();
                        setPosition(x - localX * scaleX * Math.cos(rotation) + localY * scaleY * Math.sin(rotation), getPosition().getY());
                    }
                    public double getY() {
                        double localX = item.getChild("animationLocalPosition").getAttribute("x").doubleValue();
                        double localY = -item.getChild("animationLocalPosition").getAttribute("y").doubleValue();
                        double scaleX = getChild("scale").getAttribute("x").doubleValue() * item.getChild("animationLocalScale").getAttribute("x").doubleValue();
                        double scaleY = getChild("scale").getAttribute("y").doubleValue() * item.getChild("animationLocalScale").getAttribute("y").doubleValue();
                        double rotation = -getAttribute("rotation").doubleValue();
                        return -getPosition().getY() + localX * scaleX * Math.sin(rotation) + localY * scaleY * Math.cos(rotation);
                    }
                    public void setY(double y) {
                        double localX = item.getChild("animationLocalPosition").getAttribute("x").doubleValue();
                        double localY = -item.getChild("animationLocalPosition").getAttribute("y").doubleValue();
                        double scaleX = getChild("scale").getAttribute("x").doubleValue() * item.getChild("animationLocalScale").getAttribute("x").doubleValue();
                        double scaleY = getChild("scale").getAttribute("y").doubleValue() * item.getChild("animationLocalScale").getAttribute("y").doubleValue();
                        double rotation = -getAttribute("rotation").doubleValue();
                        setPosition(getPosition().getX(), -y + localX * scaleX * Math.sin(rotation) + localY * scaleY * Math.cos(rotation));
                    }
                    public double getScaleX() {
                        return getChild("scale").getAttribute("x").doubleValue() * item.getChild("animationLocalScale").getAttribute("x").doubleValue() * 0.01;
                    }
                    public void setScaleX(double scaleX) {
                        getChild("scale").setAttribute("x", scaleX / (item.getChild("animationLocalScale").getAttribute("x").doubleValue() * 0.01));
                    }
                    public double getScaleY() {
                        return getChild("scale").getAttribute("y").doubleValue() * item.getChild("animationLocalScale").getAttribute("y").doubleValue() * 0.01;
                    }
                    public void setScaleY(double scaleY) {
                        getChild("scale").setAttribute("y", scaleY / (item.getChild("animationLocalScale").getAttribute("y").doubleValue() * 0.01));
                    }
                    public double getRotation() {
                        return -getAttribute("rotation").doubleValue() - item.getAttribute("animationRotation").doubleValue();
                    }
                    public void setRotation(double rotation) {
                        setAttribute("rotation", -rotation - item.getAttribute("animationRotation").doubleValue());
                    }
                    public double getDepth() {
                        return getAttribute("depth").doubleValue();
                    }
                });
            } else {
                String animationAlias = getItem().getAttribute("animationAlias").stringValue();
                if (false && !animationAlias.isEmpty()) {
                    /* // TODO2:
                    for (SimpleBinAnimation binAnimation : AnimationManager.getBinAnimations()) if (animation.isEmpty() || binAnimation.name.equals(animation)) {
                        int i1 = 0;
                        for (SimpleBinAnimation.SimpleBinAnimationState ignored : binAnimation.states) {
                            if (binAnimation.stateAliasStringTableIndices.length <= i1) break;
                            StringBuilder stringBuilder = new StringBuilder();
                            int byteIndex = binAnimation.stringDefinitions[binAnimation.stateAliasStringTableIndices[i1]].stringTableIndex;
                            while (binAnimation.stringTable[byteIndex] != 0x00) {
                                stringBuilder.append((char) binAnimation.stringTable[byteIndex]);
                                byteIndex++;
                            }
                            if (ignored.globalIdHash == 403081035) {
                                BinAnimationHelper.addBinAnimationAsObjectPositions(this, binAnimation, "squidbody", new BinAnimationHelper.BinAnimationInterface() {
                                    public double getX() {
                                        double localX = item.getChild("animationLocalPosition").getAttribute("x").doubleValue();
                                        double localY = -item.getChild("animationLocalPosition").getAttribute("y").doubleValue();
                                        double scaleX = getChild("scale").getAttribute("x").doubleValue() * item.getChild("animationLocalScale").getAttribute("x").doubleValue();
                                        double scaleY = getChild("scale").getAttribute("y").doubleValue() * item.getChild("animationLocalScale").getAttribute("y").doubleValue();
                                        double rotation = -getAttribute("rotation").doubleValue();
                                        return getPosition().getX() + localX * scaleX * Math.cos(rotation) - localY * scaleY * Math.sin(rotation);
                                    }
                                    public void setX(double x) {
                                        double localX = item.getChild("animationLocalPosition").getAttribute("x").doubleValue();
                                        double localY = -item.getChild("animationLocalPosition").getAttribute("y").doubleValue();
                                        double scaleX = getChild("scale").getAttribute("x").doubleValue() * item.getChild("animationLocalScale").getAttribute("x").doubleValue();
                                        double scaleY = getChild("scale").getAttribute("y").doubleValue() * item.getChild("animationLocalScale").getAttribute("y").doubleValue();
                                        double rotation = -getAttribute("rotation").doubleValue();
                                        setPosition(x - localX * scaleX * Math.cos(rotation) + localY * scaleY * Math.sin(rotation), getPosition().getY());
                                    }
                                    public double getY() {
                                        double localX = item.getChild("animationLocalPosition").getAttribute("x").doubleValue();
                                        double localY = -item.getChild("animationLocalPosition").getAttribute("y").doubleValue();
                                        double scaleX = getChild("scale").getAttribute("x").doubleValue() * item.getChild("animationLocalScale").getAttribute("x").doubleValue();
                                        double scaleY = getChild("scale").getAttribute("y").doubleValue() * item.getChild("animationLocalScale").getAttribute("y").doubleValue();
                                        double rotation = -getAttribute("rotation").doubleValue();
                                        return -getPosition().getY() + localX * scaleX * Math.sin(rotation) + localY * scaleY * Math.cos(rotation);
                                    }
                                    public void setY(double y) {
                                        double localX = item.getChild("animationLocalPosition").getAttribute("x").doubleValue();
                                        double localY = -item.getChild("animationLocalPosition").getAttribute("y").doubleValue();
                                        double scaleX = getChild("scale").getAttribute("x").doubleValue() * item.getChild("animationLocalScale").getAttribute("x").doubleValue();
                                        double scaleY = getChild("scale").getAttribute("y").doubleValue() * item.getChild("animationLocalScale").getAttribute("y").doubleValue();
                                        double rotation = -getAttribute("rotation").doubleValue();
                                        setPosition(getPosition().getX(), -y + localX * scaleX * Math.sin(rotation) - localY * scaleY * Math.cos(rotation));
                                    }
                                    public double getScaleX() {
                                        return getChild("scale").getAttribute("x").doubleValue() * item.getChild("animationLocalScale").getAttribute("x").doubleValue() * 0.01;
                                    }
                                    public void setScaleX(double scaleX) {
                                        getChild("scale").setAttribute("x", scaleX / (item.getChild("animationLocalScale").getAttribute("x").doubleValue() * 0.01));
                                    }
                                    public double getScaleY() {
                                        return getChild("scale").getAttribute("y").doubleValue() * item.getChild("animationLocalScale").getAttribute("y").doubleValue() * 0.01;
                                    }
                                    public void setScaleY(double scaleY) {
                                        getChild("scale").setAttribute("y", scaleY / (item.getChild("animationLocalScale").getAttribute("y").doubleValue() * 0.01));
                                    }
                                    public double getRotation() {
                                        return -getAttribute("rotation").doubleValue() - item.getAttribute("animationRotation").doubleValue();
                                    }
                                    public void setRotation(double rotation) {
                                        setAttribute("rotation", -rotation - item.getAttribute("animationRotation").doubleValue());
                                    }
                                    public double getDepth() {
                                        return getAttribute("depth").doubleValue();
                                    }
                                });
                                break;
                            }
                            i1++;
                        }
                    }

                     */
                }
            }

        }

    }


    private boolean addPartAsObjectPosition(_Object part) {

        double partX = (item.getAttribute("variations").booleanValue()) ? 0 : part.getChildren("position").get(0).getAttribute("x").doubleValue();
        double partY = (item.getAttribute("variations").booleanValue()) ? 0 : -part.getChildren("position").get(0).getAttribute("y").doubleValue();
        double partScaleX = part.getChildren("scale").get(0).getAttribute("x").doubleValue();
        double partScaleY = part.getChildren("scale").get(0).getAttribute("y").doubleValue();
        double partRotation = part.getAttribute("rotation").doubleValue();

        // TODO2: build hitbox based on entire bounds of parts

        Image img = null;

        if (!part.getAttribute("name").stringValue().isEmpty()) {
            img = ResourceManager.getImage(null, part.getAttribute("name").stringValue(), GameVersion.VERSION_WOG2);
        }

        if (img != null) {
            double partPivotX = (part.getChildren("pivot").get(0).getAttribute("x").doubleValue() - 0.5) * img.getWidth() * 0.01;
            double partPivotY = (0.5 - part.getChildren("pivot").get(0).getAttribute("y").doubleValue()) * img.getHeight() * 0.01;

            long color = Long.parseLong(part.getAttribute("color").stringValue());

            Image finalImg = ImageUtility.colorize(img, new Color((int)((color & 0xFF000000L) >> 24), (int)((color & 0xFF0000) >> 16), (int)((color & 0xFF00) >> 8), (int)(color & 0xFF)));

            addObjectComponent(new ImageComponent(this) {
                @Override
                public double getX() {
                    double scaleX = getAttribute("scale").positionValue().getX();
                    double scaleY = getAttribute("scale").positionValue().getY();

                    double addX = (partX - partPivotX * partScaleX) * scaleX;
                    double addY = (partY - partPivotY * partScaleY) * scaleY;

                    double rotation = -getAttribute("rotation").doubleValue();

                    double x = getPosition().getX();
                    return x + addX * Math.cos(rotation) + addY * -Math.sin(rotation);
                }
                @Override
                public void setX(double x) {
                    double scaleX = getAttribute("scale").positionValue().getX();
                    double scaleY = getAttribute("scale").positionValue().getY();

                    double addX = (partX - partPivotX * partScaleX) * scaleX;
                    double addY = (partY - partPivotY * partScaleY) * scaleY;

                    double rotation = -getAttribute("rotation").doubleValue();

                    double y = getPosition().getY();
                    setPosition(x - (addX * Math.cos(rotation) + addY * -Math.sin(rotation)), y);
                }
                @Override
                public double getY() {
                    double scaleX = getAttribute("scale").positionValue().getX();
                    double scaleY = getAttribute("scale").positionValue().getY();

                    double addX = (partX - partPivotX * partScaleX) * scaleX;
                    double addY = (partY - partPivotY * partScaleY) * scaleY;

                    double rotation = -getAttribute("rotation").doubleValue();

                    double y = -getPosition().getY();
                    return y + addX * Math.sin(rotation) + addY * Math.cos(rotation);
                }
                @Override
                public void setY(double y) {
                    double scaleX = getAttribute("scale").positionValue().getX();
                    double scaleY = getAttribute("scale").positionValue().getY();

                    double addX = (partX - partPivotX * partScaleX) * scaleX;
                    double addY = (partY - partPivotY * partScaleY) * scaleY;

                    double rotation = -getAttribute("rotation").doubleValue();

                    double x = getPosition().getX();
                    setPosition(x, -(y - (addX * Math.sin(rotation) + addY * Math.cos(rotation))));
                }
                @Override
                public double getRotation() {
                    return -getAttribute("rotation").doubleValue() - partRotation;
                }
                @Override
                public void setRotation(double rotation) {
                    setAttribute("rotation", -rotation + partRotation);
                }
                @Override
                public double getScaleX() {
                    double scaleX = getAttribute("scale").positionValue().getX() * 0.01;
                    return partScaleX * scaleX;
                }
                @Override
                public double getScaleY() {
                    double scaleY = getAttribute("scale").positionValue().getY() * 0.01;
                    return partScaleY * scaleY;
                }
                @Override
                public void setScaleX(double _scaleX) {
                    double scaleY = getAttribute("scale").positionValue().getY();
                    setAttribute("scale", _scaleX * 100.0 / partScaleX + "," + scaleY);
                }
                @Override
                public void setScaleY(double _scaleY) {
                    double scaleX = getAttribute("scale").positionValue().getX();
                    setAttribute("scale", scaleX + "," + _scaleY * 100.0 / partScaleY);
                }
                @Override
                public double getDepth() {
                    return getAttribute("depth").doubleValue();
                }
                @Override
                public double getAlpha() {
                    return  (Long.parseLong(part.getAttribute("color").stringValue()) >> 24) / 255.0 * part.getAttribute("imageAlpha").doubleValue() * (part.getAttribute("invisible").booleanValue() ? 0.5 : 1);
                }
                @Override
                public Image getImage() {
                    return finalImg;
                }
                @Override
                public boolean isVisible() {
                    if (AssetManager.getVisibility("graphics") == 0) return false;
                    if (!shouldShow()) return false;
                    if (getAttribute("forcedRandomizationIndex").intValue() == -1) return true;
                    if (true) return item.getChildren("objects").indexOf(part) == getAttribute("forcedRandomizationIndex").intValue();
                    if (randomizationIndices.get(part.getAttribute("randomizationGroup").intValue()) == null) return false;
                    if (part.getAttribute("randomizationGroup").intValue() == getAttribute("forcedRandomizationIndex").intValue()) {
                        int index = 0;
                        for (EditorObject child : getChildren()) if (child instanceof _Object && child.getAttribute("randomizationGroup").intValue() == getAttribute("forcedRandomizationIndex").intValue()) {
                            if (child == part) break;
                            index++;
                        }
                        return (index == randomizationIndices.get(part.getAttribute("randomizationGroup").intValue()));
                    }
                    return false;
                }
                @Override
                public boolean isDraggable() {
                    return finalImg != null && finalImg.getWidth() > 2 && finalImg.getHeight() > 2;
                }
                @Override
                public boolean isResizable() {
                    return finalImg != null && finalImg.getWidth() > 2 && finalImg.getHeight() > 2;
                }
                @Override
                public boolean isRotatable() {
                    return finalImg != null && finalImg.getWidth() > 2 && finalImg.getHeight() > 2;
                }
            });

            return finalImg != null && finalImg.getWidth() > 2 && finalImg.getHeight() > 2;

        }

        return false;

    }


    public boolean shouldShow() {
        return true;

    }

}
