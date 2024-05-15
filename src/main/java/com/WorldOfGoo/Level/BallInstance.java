package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.Functions.BallManager;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WorldOfGoo.Ball.Part;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class BallInstance extends EditorObject {

    private _Ball ball = null;
    public _Ball getBall() {
        return ball;
    }


    private final ArrayList<Strand> strands = new ArrayList<>();
    public ArrayList<Strand> getStrands() {
        return strands;
    }


    private final int randomSeed;


    public BallInstance(EditorObject _parent) {
        super(_parent);
        setRealName("BallInstance");

        addAttribute("type",       InputField.BALL)                                  .assertRequired();
        addAttribute("x",          InputField.NUMBER)                                .assertRequired();
        addAttribute("y",          InputField.NUMBER)                                .assertRequired();
        addAttribute("id",         InputField.UNIQUE_GOOBALL_ID)                     .assertRequired();
        addAttribute("discovered", InputField.FLAG);
        addAttribute("angle",      InputField.NUMBER)           .setDefaultValue("0").assertRequired();

        randomSeed = (int)(Math.random() * 10000000);
        setNameAttribute(getAttribute2("id"));
        setNameAttribute2(getAttribute2("type"));
        setMetaAttributes(MetaEditorAttribute.parse("id,type,x,y,angle,discovered,"));

    }


    @Override
    public void update() {
        setNameAttribute(getAttribute2("id"));
        for (_Ball ball2 : BallManager.getImportedBalls()) {
            if (ball2.getVersion() == getLevel().getVersion() && ball2.getObjects().get(0).getAttribute("name").equals(getAttribute("type"))) {
                ball = ball2;
            }
        }
        setChangeListener("type", ((observable, oldValue, newValue) -> {
            boolean found = false;
            for (_Ball ball2 : BallManager.getImportedBalls()) {
                if (ball2.getVersion() == getLevel().getVersion() && ball2.getObjects().get(0).getAttribute("name").equals(getAttribute("type"))) {
                    found = true;
                    ball = ball2;
                    break;
                }
            }
            if (!found) {
                _Ball ball2;
                try {
                    ball2 = FileManager.openBall(getAttribute("type"), LevelManager.getLevel().getVersion());
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    throw new RuntimeException(e);
                }
                if (ball2 == null) return;

                for (EditorObject resrc : FileManager.commonBallResrcData){
                    GlobalResourceManager.addResource(resrc, LevelManager.getLevel().getVersion());
                }

                ball2.makeImages(LevelManager.getLevel().getVersion());
                ball2.setVersion(LevelManager.getLevel().getVersion());

                BallManager.getImportedBalls().add(ball2);
                ball = ball2;
            }
            for (EditorObject strand : LevelManager.getLevel().getLevel()) {
                if (strand instanceof Strand) {
                    if (strand.getAttribute("gb2").equals(getAttribute("id"))) {
                        ((Strand) strand).setStrand(null);
                        strand.update();
                    }
                }
            }
        }));

        BallInstance thisBall = this;

        //TODO possibly make it so that when you make two gooballs have the same ID strands don't permanently break
        strands.clear();
        for (EditorObject obj : LevelManager.getLevel().getLevel()) {
            if (obj instanceof Strand) {
                if (obj.getAttribute("gb1").equals(getAttribute("id"))) {
                    strands.add((Strand)obj);
                    ((Strand) obj).setGoo1(thisBall);
                }
                if (obj.getAttribute("gb2").equals(getAttribute("id"))) {
                    strands.add((Strand)obj);
                    ((Strand) obj).setGoo2(thisBall);
                }
            }
        }
        setChangeListener("id", (observable, oldValue, newValue) -> {
            strands.clear();
            for (EditorObject obj : LevelManager.getLevel().getLevel()) {
                if (obj instanceof Strand) {
                    if (obj.getAttribute("gb1").equals(getAttribute("id"))) {
                        strands.add((Strand)obj);
                        ((Strand) obj).setGoo1(thisBall);
                    }
                    if (obj.getAttribute("gb2").equals(getAttribute("id"))) {
                        strands.add((Strand)obj);
                        ((Strand) obj).setGoo2(thisBall);
                    }
                }
            }
        });
        refreshObjectPositions();
    }


    public void refreshObjectPositions() {

        clearObjectPositions();

        int i = 0;

        for (EditorObject editorObject : ball.getObjects()) {
            if (editorObject instanceof Part part) {
                addPartAsObjectPosition(part, (long)randomSeed * i);
                i++;
            }
        }

    }


    private boolean partCanBeUsed(Part part) {

        String state = "standing";

        if (getAttribute("discovered").equals("false")) {
            state = "sleeping";
        } else {
            for (EditorObject obj : LevelManager.getLevel().getLevel()) {
                if (obj instanceof Strand) {
                    if (obj.getAttribute("gb1").equals(getAttribute("id")) || obj.getAttribute("gb2").equals(getAttribute("id"))) {
                        state = "attached";
                        break;
                    }
                }
            }
        }

        boolean ok = false;

        if (part.getAttribute("state").equals("")) {
            ok = true;
        } else {
            String word = part.getAttribute("state");
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

        double partX = InputField.getRange(part.getAttribute("x"), machine.nextDouble());
        double partY = -InputField.getRange(part.getAttribute("y"), machine.nextDouble());

        double scale = part.getDouble("scale");

        // TODO build hitbox based on entire bounds of parts

        Image img = part.getImages().get((int)(part.getImages().size() * machine.nextDouble()));
        if (img != null) addObjectPosition(new ObjectPosition(ObjectPosition.IMAGE) {
            public double getX() {

                Point2D position = new Point2D(partX, partY);
                position = EditorObject.rotate(position, -Math.toRadians(getDouble("angle")), new Point2D(0, 0));
                position = position.add(getDouble("x"), -getDouble("y"));

                return position.getX();

            }
            public void setX(double x) {
                setAttribute("x", x - partX);
            }
            public double getY() {

                Point2D position = new Point2D(partX, partY);
                position = EditorObject.rotate(position, -Math.toRadians(getDouble("angle")), new Point2D(0, 0));
                position = position.add(getDouble("x"), -getDouble("y"));

                return position.getY();

            }
            public void setY(double y) {
                setAttribute("y", -y + partY);

            }
            public double getRotation() {
                return -Math.toRadians(getDouble("angle"));
            }
            public void setRotation(double rotation) {
                setAttribute("angle", -Math.toDegrees(rotation));
            }
            public double getWidth() {
                return img.getWidth() * scale;
            }
            public double getHeight() {
                return img.getHeight() * scale;
            }
            public Image getImage() {
                return img;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getShowGoos() == 2;
            }
            public boolean isSelectable() {
                return part.getString("name").equals("body");
            }
            public boolean isResizable() {
                return false;
            }
            public boolean isRotatable() {
                return part.getString("name").equals("body");
            }
        });

        Image pupilImg = part.getPupilImage();
        if (pupilImg != null) addObjectPosition(new ObjectPosition(ObjectPosition.IMAGE) {
            public double getX() {

                Point2D position = new Point2D(partX, partY);
                position = EditorObject.rotate(position, -Math.toRadians(getDouble("angle")), new Point2D(0, 0));
                position = position.add(getDouble("x"), -getDouble("y"));

                return position.getX();

            }
            public void setX(double x) {
                setAttribute("x", x - partX);
            }
            public double getY() {

                Point2D position = new Point2D(partX, partY);
                position = EditorObject.rotate(position, -Math.toRadians(getDouble("angle")), new Point2D(0, 0));
                position = position.add(getDouble("x"), -getDouble("y"));

                return position.getY();

            }
            public void setY(double y) {
                setAttribute("y", -y + partY);

            }
            public double getRotation() {
                return 0; // ???
            }
            public double getWidth() {
                return pupilImg.getWidth() * scale;
            }
            public double getHeight() {
                return pupilImg.getHeight() * scale;
            }
            public Image getImage() {
                return pupilImg;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getShowGoos() == 2;
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
