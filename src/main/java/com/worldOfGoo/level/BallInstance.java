package com.worldOfGoo.level;

import com.woogleFX.assets.Asset;
import com.woogleFX.assets.wog1.level.WOG1Level;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.assets.wog1.ball.WOG1Ball;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.engine.undoHandling.userActions.ObjectDestructionAction;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.assets.AssetLoader;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.attributes.InputField;
import com.worldOfGoo.ball.part;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class BallInstance extends EditorObject {

    private WOG1Ball ball = null;
    public WOG1Ball getBall() {
        return ball;
    }


    private final long randomSeed;


    public BallInstance(EditorObject _parent, GameVersion version) {
        super(_parent, version);

        randomSeed = (long)(Math.random() * 10000000);
    }

    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);

        getAttribute("type").addChangeListener((observable, oldValue, newValue) -> setBallType(newValue));
        getAttribute("id").addChangeListener((observable, oldValue, newValue) -> updateStrands());
        getAttribute("discovered").addChangeListener((observable, oldValue, newValue) -> refreshObjectPositions());

        setBallType(getAttribute("type").stringValue());

    }

    @Override
    public String getName() {
        String id = getAttribute("id").stringValue();
        String type = getAttribute("type").stringValue();
        return id + ", " + type;
    }


    private void updateStrands() {

        if (AssetManager.getAsset() == null) return;

        for (EditorObject object : AssetManager.getAsset().getObjects()) if (object instanceof Strand strand) {

            String id = getAttribute("id").stringValue();
            String gb1 = strand.getAttribute("gb1").stringValue();
            String gb2 = strand.getAttribute("gb2").stringValue();

            if (id.equals(gb1)) strand.setGoo1(this);

            if (id.equals(gb2)) strand.setGoo2(this);

        }

        refreshObjectPositions();

    }


    private void setBallType(String type) {

        if (AssetManager.getAsset() == null) return;

        WOG1Ball previousBall = ball;

        try {
            ball = WOG1Ball.assetSelector.openInstance(type, getVersion());
        } catch (IOException e) {
            ErrorAlarm.show(e);
            ball = null;
        }
        if (ball == null) {
            if (!AssetLoader.failedResources.contains("Ball: " + getAttribute("type").stringValue() + " (version " + getVersion() + ")")) {
                AssetLoader.failedResources.add("Ball: " + getAttribute("type").stringValue() + " (version " + getVersion() + ")");
            }
        }
        if (ball != null && ball == previousBall) return;

        String id = getAttribute("id").stringValue();
        for (EditorObject object : AssetManager.getAsset().getObjects()) if (object instanceof Strand strand) {
            String gb1 = strand.getAttribute("gb1").stringValue();
            String gb2 = strand.getAttribute("gb2").stringValue();
            if (gb1.equals(id) || gb2.equals(id)) {
                strand.setStrand(null);
                strand.update();
            }
        }

        refreshObjectPositions();

    }


    @Override
    public void update() {

        setBallType(getAttribute("type").stringValue());

        updateStrands();

    }


    private static ArrayList<part> orderPartsByLayer(Collection<EditorObject> objects) {

        ArrayList<part> orderedParts = new ArrayList<>();

        for (EditorObject EditorObject : objects) {

            if (EditorObject instanceof part part) {

                double layer = part.getAttribute("layer").doubleValue();
                int i = 0;

                while (i < orderedParts.size() && orderedParts.get(i).getAttribute("layer").doubleValue() <= layer) i++;

                orderedParts.add(i, part);

            }

        }

        return orderedParts;

    }


    public void refreshObjectPositions() {


        clearObjectComponents();

        if (ball != null) {
            int i = 0;
            for (com.worldOfGoo.ball.part part : orderPartsByLayer(ball.getObjects())) {
                addPartAsObjectPosition(part, randomSeed * i);
                i++;
            }
        }

        boolean isCircle = ball == null || ball.getBall().getAttribute("shape").listValue()[0].equals("circle");

        if (isCircle) addObjectComponent(new CircleComponent(this) {
            public double getX() {
                return getAttribute("x").doubleValue();
            }
            public void setX(double x) {
                setAttribute("x", x);
            }
            public double getY() {
                return -getAttribute("y").doubleValue();
            }
            public void setY(double y) {
                setAttribute("y", -y);
            }
            public double getRotation() {
                return -Math.toRadians(getAttribute("angle").doubleValue());
            }
            public void setRotation(double rotation) {
                setAttribute("angle", -Math.toDegrees(rotation));
            }
            public double getRadius() {
                if (ball == null) return 15;
                return Double.parseDouble(ball.getBall().getAttribute("shape").listValue()[1]) / 2;
            }
            public double getEdgeSize() {
                return 3;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public Paint getBorderColor() {
                if (ball == null) {
                    return new Color(0.5, 0.25, 0.25, 1.0);
                } else {
                    return new Color(0.5 ,0.5, 0.5, 1);
                }
            }
            public Paint getColor() {
                return new Color(0, 0, 0, 0);
            }
            public double getDepth() {
                return 0.000001;
            }
            public boolean isVisible() {
                return ball == null || AssetManager.getVisibility("goos") == 1;
            }
            public boolean isResizable() {
                return false;
            }
        });

        else addObjectComponent(new RectangleComponent(this) {
            public double getX() {
                return getAttribute("x").doubleValue();
            }

            public void setX(double x) {
                setAttribute("x", x);
            }

            public double getY() {
                return -getAttribute("y").doubleValue();
            }

            public void setY(double y) {
                setAttribute("y", -y);
            }

            public double getRotation() {
                return -Math.toRadians(getAttribute("angle").doubleValue());
            }

            public void setRotation(double rotation) {
                setAttribute("angle", -Math.toDegrees(rotation));
            }

            public double getWidth() {
                if (ball == null) return 15;
                return Double.parseDouble(ball.getBall().getAttribute("shape").listValue()[1]);
            }

            public double getHeight() {
                if (ball == null) return 15;
                return Double.parseDouble(ball.getBall().getAttribute("shape").listValue()[2]);
            }

            public double getEdgeSize() {
                return 3;
            }

            public boolean isEdgeOnly() {
                return true;
            }

            public Paint getBorderColor() {
                if (ball == null) {
                    return new Color(0.5, 0.25, 0.25, 1.0);
                } else {
                    return new Color(0.5, 0.5, 0.5, 1);
                }
            }

            public Paint getColor() {
                return new Color(0, 0, 0, 0);
            }

            public double getDepth() {
                return 0.000001;
            }

            public boolean isVisible() {
                return ball == null || AssetManager.getVisibility("goos") == 1;
            }

            public boolean isResizable() {
                return false;
            }
        });

    }


    private boolean partCanBeUsed(part part) {

        String state = "standing";

        if (!getAttribute("discovered").booleanValue()) {
            state = "sleeping";
        } else {
            for (EditorObject obj : AssetManager.getAsset().getObjects()) {
                if (obj instanceof Strand strand) {

                    String id = getAttribute("id").stringValue();
                    String gb1 = strand.getAttribute("gb1").stringValue();
                    String gb2 = strand.getAttribute("gb2").stringValue();

                    if (id.equals(gb1) || id.equals(gb2)) {
                        state = "attached";
                        break;
                    }

                }
            }
        }

        boolean ok = false;

        if (part.getAttribute("state").stringValue().isEmpty()) {
            ok = true;
        } else {
            String word = part.getAttribute("state").stringValue();
            while (word.contains(",")) {
                if (word.substring(0, word.indexOf(",")).equals(state)) {
                    ok = true;
                    break;
                }
                word = word.substring(word.indexOf(",") + 1);
            }
            if (word.equals(state)) {
                ok = true;
            }
        }

        return ok;

    }


    private void addPartAsObjectPosition(part part, long randomSeed) {

        if (!partCanBeUsed(part)) return;

        Random machine = new Random(randomSeed);
        machine.nextDouble();

        double partX = InputField.getRange(part.getAttribute("x").stringValue(), machine.nextDouble());
        double partY = -InputField.getRange(part.getAttribute("y").stringValue(), machine.nextDouble());

        double scale = part.getAttribute("scale").doubleValue();

        // TODO: build hitbox based on entire bounds of parts

        String[] imageStrings = part.getAttribute("image").listValue();
        if (imageStrings.length == 0) return;

        String imageString = imageStrings[(int)(imageStrings.length * machine.nextDouble())];

        Image img = ResourceManager.getImage(ball.getResources(), imageString, AssetManager.getAsset().getVersion());

        if (img != null) {
            addObjectComponent(new ImageComponent(this) {
                public double getX() {

                    double x = getAttribute("x").doubleValue();
                    double y = -getAttribute("y").doubleValue();
                    double angle = -Math.toRadians(getAttribute("angle").doubleValue());

                    Point2D position = new Point2D(partX, partY);
                    position = ObjectUtil.rotate(position, angle, new Point2D(0, 0));
                    position = position.add(x, y);

                    return position.getX();

                }
                public void setX(double x) {
                    setAttribute("x", x - partX);
                }
                public double getY() {

                    double x = getAttribute("x").doubleValue();
                    double y = -getAttribute("y").doubleValue();
                    double angle = -Math.toRadians(getAttribute("angle").doubleValue());

                    Point2D position = new Point2D(partX, partY);
                    position = ObjectUtil.rotate(position, angle, new Point2D(0, 0));
                    position = position.add(x, y);

                    return position.getY();

                }
                public void setY(double y) {
                    setAttribute("y", -y + partY);
                }
                public double getRotation() {
                    return -Math.toRadians(getAttribute("angle").doubleValue());
                }
                public void setRotation(double rotation) {
                    setAttribute("angle", -Math.toDegrees(rotation));
                }
                public double getScaleX() {
                    return scale;
                }
                public double getScaleY() {
                    return scale;
                }
                public double getDepth() {
                    return 0.000001;
                }
                public Image getImage() {
                    return img;
                }
                public boolean isVisible() {
                    return AssetManager.getVisibility("goos") == 2;
                }
                public boolean isSelectable() {
                    String partName = part.getAttribute("name").stringValue();
                    return partName.equals("body");
                }
                public boolean isResizable() {
                    return false;
                }
                public boolean isRotatable() {
                    String partName = part.getAttribute("name").stringValue();
                    return partName.equals("body");
                }
            });
        }


        String[] pupilImageStrings = part.getAttribute("pupil").listValue();

        if (pupilImageStrings.length == 0) return;

        String pupilImageString = pupilImageStrings[(int)(pupilImageStrings.length * machine.nextDouble())];

        Image pupilImg;
        pupilImg = ResourceManager.getImage(ball.getResources(), pupilImageString, AssetManager.getAsset().getVersion());
        if (pupilImg != null) {
            addObjectComponent(new ImageComponent(this) {
                public double getX() {

                    double x = getAttribute("x").doubleValue();
                    double y = -getAttribute("y").doubleValue();
                    double angle = -Math.toRadians(getAttribute("angle").doubleValue());

                    Point2D position = new Point2D(partX, partY);
                    position = ObjectUtil.rotate(position, angle, new Point2D(0, 0));
                    position = position.add(x, y);

                    return position.getX();

                }
                public void setX(double x) {
                    setAttribute("x", x - partX);
                }
                public double getY() {

                    double x = getAttribute("x").doubleValue();
                    double y = -getAttribute("y").doubleValue();
                    double angle = -Math.toRadians(getAttribute("angle").doubleValue());

                    Point2D position = new Point2D(partX, partY);
                    position = ObjectUtil.rotate(position, angle, new Point2D(0, 0));
                    position = position.add(x, y);

                    return position.getY();

                }
                public void setY(double y) {
                    setAttribute("y", -y + partY);
                }
                public double getRotation() {
                    return 0; // ???
                }
                public double getScaleX() {
                    return scale;
                }
                public double getScaleY() {
                    return scale;
                }
                public double getDepth() {
                    return 0.000001;
                }
                public Image getImage() {
                    return pupilImg;
                }
                public boolean isVisible() {
                    return AssetManager.getVisibility("goos") == 2;
                }
                public boolean isSelectable() {
                    return false;
                }
                public boolean isResizable() {
                    return false;
                }
                public boolean isRotatable() {
                    return false;
                }
            });
        }

    }


    @Override
    public List<ObjectDestructionAction> onDelete() {
        List<ObjectDestructionAction> outActions = super.onDelete();

        for (EditorObject object : AssetManager.getAsset().getObjects()) if (object instanceof Strand strand) {
            if (this != strand.getGoo1() && this != strand.getGoo2()) continue;

            outActions.add(new ObjectDestructionAction(strand));

            if (this == strand.getGoo1()) strand.setGoo1(null);
            else strand.setGoo2(null);

        }

        return outActions;

    }

}
