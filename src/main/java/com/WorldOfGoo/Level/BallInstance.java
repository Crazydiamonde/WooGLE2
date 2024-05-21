package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.objectcomponents.CircleComponent;
import com.WooGLEFX.EditorObjects.objectcomponents.ImageComponent;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.ResourceManagers.GlobalResourceManager;
import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.File.ResourceManagers.BallManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.Structures.GameVersion;
import com.WooGLEFX.EditorObjects.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WorldOfGoo.Ball.Part;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Random;

public class BallInstance extends EditorObject {

    private _Ball ball = null;
    public _Ball getBall() {
        return ball;
    }


    private final long randomSeed;


    public BallInstance(EditorObject _parent) {
        super(_parent, "BallInstance", "level\\BallInstance");

        addAttribute("type",       InputField.BALL)                                         .assertRequired();
        addAttribute("x",          InputField.NUMBER)                                       .assertRequired();
        addAttribute("y",          InputField.NUMBER)                                       .assertRequired();
        addAttribute("id",         InputField.UNIQUE_GOOBALL_ID)                            .assertRequired();
        addAttribute("discovered", InputField.FLAG)               .setDefaultValue("true");
        addAttribute("angle",      InputField.NUMBER)             .setDefaultValue("0")     .assertRequired();

        randomSeed = (long)(Math.random() * 10000000);

        setMetaAttributes(MetaEditorAttribute.parse("id,type,x,y,angle,discovered,"));

        getAttribute("type").addChangeListener((observable, oldValue, newValue) -> setType(newValue));
        getAttribute("id").addChangeListener((observable, oldValue, newValue) -> updateStrands());
        getAttribute("discovered").addChangeListener((observable, oldValue, newValue) -> refreshObjectPositions());

    }


    @Override
    public String getName() {
        String id = getAttribute("id").stringValue();
        String type = getAttribute("type").stringValue();
        return id + ", " + type;
    }


    private void updateStrands() {

        if (LevelManager.getLevel() == null) return;

        for (EditorObject object : LevelManager.getLevel().getLevel()) if (object instanceof Strand strand) {

            String id = getAttribute("id").stringValue();
            String gb1 = strand.getAttribute("gb1").stringValue();
            String gb2 = strand.getAttribute("gb2").stringValue();

            if (id.equals(gb1)) strand.setGoo1(this);

            if (id.equals(gb2)) strand.setGoo2(this);

        }

        refreshObjectPositions();

    }


    private static _Ball getBall(String type) {

        GameVersion version = LevelManager.getVersion();

        for (_Ball ball : BallManager.getImportedBalls()) {
            String name = ball.getObjects().get(0).getAttribute("name").stringValue();
            if (ball.getVersion() == version && name.equals(type)) {
                return ball;
            }
        }

        _Ball ball;
        try {
            ball = FileManager.openBall(type, version);
            if (ball == null) return null;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return null;
        }

        for (EditorObject resrc : FileManager.commonBallResrcData) {
            GlobalResourceManager.addResource(resrc, version);
        }

        ball.makeImages(version);
        ball.setVersion(version);

        BallManager.getImportedBalls().add(ball);

        return ball;

    }


    private void setType(String type) {

        if (LevelManager.getLevel() == null) return;

        this.ball = getBall(type);

        String id = getAttribute("id").stringValue();
        for (EditorObject object : LevelManager.getLevel().getLevel()) if (object instanceof Strand strand) {
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

        setType(getAttribute("type").stringValue());

        updateStrands();

    }


    public void refreshObjectPositions() {

        if (ball == null) return;

        clearObjectPositions();

        int i = 0;

        for (EditorObject editorObject : ball.getObjects()) if (editorObject instanceof Part part) {
            addPartAsObjectPosition(part, randomSeed * i);
            i++;
        }

        addObjectComponent(new CircleComponent() {
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
                //return ball.getObjects().get(0).getAttribute("shape").stringValue();
                return 10;
            }
            public double getEdgeSize() {
                return 3;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public Paint getBorderColor() {
                return new Color(0.5 ,0.5, 0.5, 1);
            }
            public Paint getColor() {
                return new Color(0, 0, 0, 0);
            }
            public double getDepth() {
                return 0.000001;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGoos() == 1;
            }
            public boolean isResizable() {
                return false;
            }
        });

    }


    private boolean partCanBeUsed(Part part) {

        String state = "standing";

        if (!getAttribute("discovered").booleanValue()) {
            state = "sleeping";
        } else {
            for (EditorObject obj : LevelManager.getLevel().getLevel()) {
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


    private void addPartAsObjectPosition(Part part, long randomSeed) {

        if (!partCanBeUsed(part)) return;

        Random machine = new Random(randomSeed);
        machine.nextDouble();

        double partX = InputField.getRange(part.getAttribute("x").stringValue(), machine.nextDouble());
        double partY = -InputField.getRange(part.getAttribute("y").stringValue(), machine.nextDouble());

        double scale = part.getAttribute("scale").doubleValue();

        // TODO build hitbox based on entire bounds of parts

        if (part.getImages().isEmpty()) return;

        Image img = part.getImages().get((int)(part.getImages().size() * machine.nextDouble()));
        if (img != null) addObjectComponent(new ImageComponent() {
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
                return part.getAttribute("layer").doubleValue() + 0.000001;
            }
            public Image getImage() {
                return img;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGoos() == 2;
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

        Image pupilImg = part.getPupilImage();
        if (pupilImg != null) addObjectComponent(new ImageComponent() {
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
                return part.getAttribute("layer").doubleValue() + 0.000002;
            }
            public Image getImage() {
                return pupilImg;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGoos() == 2;
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
