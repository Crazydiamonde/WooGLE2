package com.worldOfGoo2.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects._2_Positionable;
import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.engine.undoHandling.userActions.ObjectCreationAction;
import com.woogleFX.engine.undoHandling.userActions.ObjectDestructionAction;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.gameData.animation.SimpleBinAnimation;
import com.woogleFX.assets.wog2.WOG2Ball.WOG2Ball;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.wog2.WOG2Level.WOG2Level;
import com.woogleFX.assets.AssetLoader;
import com.worldOfGoo2.util.BallInstanceHelper;
import com.worldOfGoo2.util.BinAnimationHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class _2_Level_BallInstance extends _2_Positionable {

    private _2_Level_TerrainGroup currentGroup = null;
    public _2_Level_TerrainGroup getCurrentGroup() {
        return currentGroup;
    }
    public void setCurrentGroup(_2_Level_TerrainGroup currentGroup) {
        this.currentGroup = currentGroup;
    }


    private WOG2Ball ball = null;
    public WOG2Ball getBall() {
        return ball;
    }
    private void updateBall() {
        String type = getAttribute("type").stringValue();
        try {
            ball = WOG2Ball.assetSelector.openInstance(type, getVersion());
        } catch (IOException e) {
            ErrorAlarm.show(e);
        }
        if (ball == null) {
            String invalidBallDescription = "Ball: " + type + " (version " + getVersion() + ")";
            if (!AssetLoader.failedResources.contains(invalidBallDescription))
                AssetLoader.failedResources.add(invalidBallDescription);
        }
    }


    private final ArrayList<Strand> strands = new ArrayList<>();
    public void addStrand(Strand strand) {
        strands.add(strand);
    }
    public void removeStrand(Strand strand) {
        strands.remove(strand);
    }
    public boolean containsStrand(Strand strand) {
        return strands.contains(strand);
    }
    public boolean hasStrands() {
        return !strands.isEmpty();
    }


    private final long randomSeed;
    public long getRandomSeed() {
        return randomSeed;
    }


    public _2_Level_BallInstance(EditorObject parent, GameVersion version) {
        super(parent, version);

        randomSeed = (long)(Math.random() * 10000000);

        addAttributeAdapter("typeEnum", BallInstanceHelper.ballTypeAttributeAdapter(this, "type", "typeEnum", null));
        addAttributeAdapter("terrainGroup", new AttributeAdapter("terrainGroup") {
            private final EditorAttribute attribute = new EditorAttribute("terrainGroup", InputField.NUMBER, _2_Level_BallInstance.this);
            
            @Override
            public EditorAttribute getValue() {
                attribute.setValue(getAttribute2("terrainGroup").stringValue());
                return attribute;
            }

            @Override
            public void setValue(String value) {
                if (currentGroup != null) {
                    currentGroup.removeBall(_2_Level_BallInstance.this);
                    currentGroup.update();
                }
                
                int newValue = Integer.parseInt(value);
                setAttribute2("terrainGroup", newValue);
                
                _2_Level level = ((WOG2Level) AssetManager.getAsset()).getLevel();
                List<EditorObject> terrainGroups = level.getChildren("terrainGroups");
                
                if (newValue >= 0 && newValue < terrainGroups.size()) {
                    currentGroup = (_2_Level_TerrainGroup)terrainGroups.get(newValue);
                    currentGroup.addBall(_2_Level_BallInstance.this);
                    currentGroup.update();
                }
            }
            
        });
    }


    public boolean isConnected(_2_Level_BallInstance other) {
        if (this == other) return false;
        for (Strand s : strands.toArray(new Strand[0])) if (s.getGoo1() == other || s.getGoo2() == other) return true;
        return false;
    }


    public void updateTerrainGroup() {
        if (currentGroup != null) currentGroup.update();
    }
    
    @Override
    public String getName() {
        String id = getAttribute("uid").stringValue();
        String type = getAttribute("type").stringValue();
        return id + ", " + type;
    }

    private void registerConnectedStrands() {

        String id = getAttribute("uid").stringValue();
        for (EditorObject object : getAsset().getObjects()) if (object instanceof Strand strand) {
            if (id.equals(strand.getAttribute("ball1UID").stringValue())) strand.setGoo1(this);
            else if (id.equals(strand.getAttribute("ball2UID").stringValue())) strand.setGoo2(this);
            else continue;
            addStrand(strand);
            strand.update();
        }
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        updateBall();

        getAttribute("discovered").addChangeListener((observable, oldValue, newValue) -> update());
        getAttribute("interactive").addChangeListener((observable, oldValue, newValue) -> update());
        getAttribute2("typeEnum").addChangeListener((observable, oldValue, newValue) -> updateBall());

        getAttribute("pos").addChangeListener((observable, oldValue, newValue) -> updateTerrainGroup());

        registerConnectedStrands();

        refreshObjectComponents();

    }

    @Override
    public void update() {

        registerConnectedStrands();

        refreshObjectComponents();

    }


    private void refreshObjectComponents() {

        clearObjectComponents();

        addObjectComponents(BallInstanceHelper.generateBallObjectComponents(this));


        if (ball != null) {

            String animation = ball.getBall().getChildren("flashAnimation").get(0).getAttribute("flashAnimationId").stringValue();
            if (!animation.isEmpty()) {
                String state = "";
                if (getAttribute("type").stringValue().equals("LauncherL2B") || getAttribute("type").stringValue().equals("LauncherL2L")) {
                    if (getAttribute("discovered").booleanValue()) {
                        if (getAttribute("interactive").booleanValue()) state = "idle";
                        else state = "npc_idle";
                    } else {
                        if (getAttribute("interactive").booleanValue()) state = "sleep";
                        else state = "npc_sleep";
                    }
                }
                double scale = ball.getBall().getChildren("ballParts").get(0).getAttribute("scale").doubleValue() / 100.0;
                SimpleBinAnimation flashAnim = ResourceManager.getFlashAnim(getBall().getResources(), animation, GameVersion.VERSION_WOG2);
                if (flashAnim != null) BinAnimationHelper.addBinAnimationAsObjectPositions(this, flashAnim, state, new BinAnimationHelper.BinAnimationInterface() {
                    public double getX() {
                        return getPosition().getX();
                    }
                    public void setX(double x) {
                        setPosition(x, getPosition().getY());
                    }
                    public double getY() {
                        return -getPosition().getY();
                    }
                    public void setY(double y) {
                        setPosition(getPosition().getX(), -y);
                    }
                    public double getScaleX() {
                        return scale;
                    }
                    public void setScaleX(double scaleX) {

                    }
                    public double getScaleY() {
                        return scale;
                    }
                    public void setScaleY(double scaleY) {

                    }
                    public double getRotation() {
                        return -getAttribute("angle").doubleValue();
                    }
                    public void setRotation(double rotation) {
                        setAttribute("angle", -rotation);
                    }
                    public double getDepth() {
                        return 0.000001;
                    }
                });
            }

        }

    }


    public boolean visibilityFunction() {

        if (AssetManager.getVisibility("goos") == 0) return false;

        if (!getAttribute("type").stringValue().equals("Terrain")) return true;

        int terrainGroup = getAttribute("terrainGroup").intValue();
        if (terrainGroup < 0 || terrainGroup >= WOG2Level.comboBoxList.size()) return true;
        else return WOG2Level.comboBoxList.get(terrainGroup);

    }

    @Override
    public List<ObjectCreationAction> onCreate() {
        ObjectAdder.fixGooBall(this);
        return super.onCreate();
    }

    @Override
    public List<ObjectDestructionAction> onDelete() {
        List<ObjectDestructionAction> outActions = super.onDelete();

        if (currentGroup != null) currentGroup.removeBall(this);

        WOG2Level level = (WOG2Level) AssetManager.getAsset();
        for (EditorObject object : level.getObjects()) if (object instanceof Strand strand) {
            if (this != strand.getGoo1() && this != strand.getGoo2()) continue;

            outActions.add(new ObjectDestructionAction(strand));

            if (this == strand.getGoo1()) strand.setGoo1(null);
            else strand.setGoo2(null);

        }
        
        return outActions;

    }

}

