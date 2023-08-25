package com.WorldOfGoo.Level;

import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WorldOfGoo.Ball.Part;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Affine;
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
        addAttribute("type", "common", InputField.BALL, true); //valid goo ball
        addAttribute("x", "0", InputField.NUMBER, true);
        addAttribute("y", "0", InputField.NUMBER, true);
        addAttribute("id", "", InputField.ANY_REQUIRED, true);
        addAttribute("discovered", "", InputField.FLAG, false);
        addAttribute("angle", "0", InputField.NUMBER, true);
        randomSeed = (int)(Math.random() * 10000000);
        setNameAttribute(getAttribute2("id"));
        setNameAttribute2(getAttribute2("type"));
        setMetaAttributes(MetaEditorAttribute.parse("id,type,x,y,angle,discovered,"));
    }

    @Override
    public void update(){
        setNameAttribute(getAttribute2("id"));
        for (_Ball ball2 : Main.getImportedBalls()) {
            if (ball2.getVersion() == getLevel().getVersion() && ball2.getObjects().get(0).getAttribute("name").equals(getAttribute("type"))) {
                ball = ball2;
            }
        }
        setChangeListener("type", ((observable, oldValue, newValue) -> {
            boolean found = false;
            for (_Ball ball2 : Main.getImportedBalls()) {
                if (ball2.getVersion() == getLevel().getVersion() && ball2.getObjects().get(0).getAttribute("name").equals(getAttribute("type"))) {
                    found = true;
                    ball = ball2;
                    break;
                }
            }
            if (!found) {
                _Ball ball2 = null;
                try {
                    ball2 = FileManager.openBall(getAttribute("type"), Main.getLevel().getVersion());
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    throw new RuntimeException(e);
                }

                for (EditorObject resrc : FileManager.commonBallResrcData){
                    GlobalResourceManager.addResource(resrc, Main.getLevel().getVersion());
                }

                ball2.makeImages(Main.getLevel().getVersion());
                ball2.setVersion(Main.getLevel().getVersion());

                Main.getImportedBalls().add(ball2);
                ball = ball2;
            }
            for (EditorObject strand : Main.getLevel().getLevel()) {
                if (strand instanceof Strand) {
                    if (strand.getAttribute("gb2").equals(getAttribute("id"))) {
                        ((Strand) strand).setStrand(null);
                        strand.update();
                    }
                }
            }
        }));

        BallInstance thisBall = this;

        setChangeListener("id", new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                strands.clear();
                for (EditorObject obj : Main.getLevel().getLevel()) {
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
            }
        });
    }

    @Override
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext){
        if (ball != null) {

            if (Main.getLevel().getShowGoos() == 2) {

                double angle = Double.parseDouble(getAttribute("angle"));

                double width;
                double height;

                if (ball.getShapeType().equals("rectangle")) {
                    width = ball.getShapeSize2();
                    height = ball.getShapeSize();
                } else {
                    width = ball.getShapeSize();
                    height = ball.getShapeSize();
                }

                int i = 0;
                for (EditorObject part : ball.getObjects()) {

                    String state = "standing";

                    if (getAttribute("discovered").equals("false")) {
                        state = "sleeping";
                    } else {
                        if (strands.size() > 0) {
                            state = "attached";
                        }
                    }

                    i++;
                    if (part instanceof Part) {

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

                        if (ok && ((Part) part).getImages().size() > 0) {
                            Random machine = new Random(randomSeed * i);
                            machine.nextDouble();
                            Image image = ((Part) part).getImages().get((int) (((Part) part).getImages().size() * machine.nextDouble()));
                            if (image != null) {
                                double scale = Double.parseDouble(part.getAttribute("scale"));

                                double lowX = 0;
                                double highX = 0;
                                double lowY = 0;
                                double highY = 0;

                                String x = part.getAttribute("x");
                                String y = part.getAttribute("y");

                                if (x.contains(",")) {
                                    lowX = Double.parseDouble(x.substring(0, x.indexOf(",")));
                                    highX = Double.parseDouble(x.substring(x.indexOf(",") + 1));
                                } else {
                                    lowX = Double.parseDouble(x);
                                    highX = lowX;
                                }
                                if (y.contains(",")) {
                                    lowY = Double.parseDouble(y.substring(0, y.indexOf(",")));
                                    highY = Double.parseDouble(y.substring(y.indexOf(",") + 1));
                                } else {
                                    lowY = Double.parseDouble(y);
                                    highY = lowY;
                                }

                                double myX = machine.nextDouble() * (highX - lowX) + lowX;
                                double myY = machine.nextDouble() * (highY - lowY) + lowY;

                                double screenX = Double.parseDouble(getAttribute("x")) + myX - image.getWidth() / 2.0 * scale;
                                double screenY = -Double.parseDouble(getAttribute("y")) - myY - image.getHeight() / 2.0 * scale;

                                imageGraphicsContext.save();
                                Affine t = imageGraphicsContext.getTransform();
                                t.appendRotation(-angle, Double.parseDouble(getAttribute("x")), -Double.parseDouble(getAttribute("y")));
                                imageGraphicsContext.setTransform(t);

                                imageGraphicsContext.drawImage(image, screenX, screenY, image.getWidth() * scale, image.getHeight() * scale);

                                if (((Part) part).getPupilImage() != null) {
                                    Image pupilImg = ((Part) part).getPupilImage();

                                    double screenX2 = Double.parseDouble(getAttribute("x")) + myX - pupilImg.getWidth() / 2.0 * scale;
                                    double screenY2 = -Double.parseDouble(getAttribute("y")) - myY - pupilImg.getHeight() / 2.0 * scale;

                                    imageGraphicsContext.drawImage(pupilImg, screenX2, screenY2, pupilImg.getWidth() * scale, pupilImg.getHeight() * scale);
                                }
                                imageGraphicsContext.restore();
                            }
                        }
                    }
                }
                if (this == Main.getSelected()) {
                    double x3 = Double.parseDouble(getAttribute("x"));
                    double y3 = Double.parseDouble(getAttribute("y"));

                    double screenX2 = (x3) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                    double screenY2 = (-y3) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                    graphicsContext.save();
                    Affine t2 = new Affine();
                    t2.appendRotation(-angle, screenX2, screenY2);
                    graphicsContext.setTransform(t2);

                    width *= 1.25;
                    height *= 1.25;

                    graphicsContext.setStroke(Renderer.selectionOutline2);
                    graphicsContext.setLineWidth(1);
                    graphicsContext.setLineDashes(3);
                    graphicsContext.setLineDashOffset(0);
                    graphicsContext.strokeRect(screenX2 - width * Main.getLevel().getZoom() / 2, screenY2 - height * Main.getLevel().getZoom() / 2, width * Main.getLevel().getZoom(), height * Main.getLevel().getZoom());
                    graphicsContext.setStroke(Renderer.selectionOutline);
                    graphicsContext.setLineWidth(1);
                    graphicsContext.setLineDashOffset(3);
                    graphicsContext.strokeRect(screenX2 - width * Main.getLevel().getZoom() / 2, screenY2 - height * Main.getLevel().getZoom() / 2, width * Main.getLevel().getZoom(), height * Main.getLevel().getZoom());
                    graphicsContext.setLineDashes(0);
                    graphicsContext.restore();
                }
            } else if (Main.getLevel().getShowGoos() == 1) {

                if (ball.getShapeType().equals("circle")) {

                    double angle = Math.toRadians(Double.parseDouble(getAttribute("angle")));

                    double width = ball.getShapeSize() - 3;
                    double height = ball.getShapeSize() - 3;

                    double screenX = Double.parseDouble(getAttribute("x")) - width / 2.0;
                    double screenY = -Double.parseDouble(getAttribute("y")) - height / 2.0;

                    graphicsContext.setStroke(Renderer.middleColor);
                    graphicsContext.setLineWidth(3 * Main.getLevel().getZoom());

                    graphicsContext.strokeOval(screenX * Main.getLevel().getZoom() + Main.getLevel().getOffsetX(), screenY * Main.getLevel().getZoom() + Main.getLevel().getOffsetY(), width * Main.getLevel().getZoom(), height * Main.getLevel().getZoom());

                    if (this == Main.getSelected()) {
                        double x3 = Double.parseDouble(getAttribute("x"));
                        double y3 = Double.parseDouble(getAttribute("y"));

                        double screenX2 = (x3) * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                        double screenY2 = (-y3) * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                        graphicsContext.save();
                        Affine t2 = new Affine();
                        t2.appendRotation(-angle, screenX2, screenY2);
                        graphicsContext.setTransform(t2);

                        width += 3;
                        height += 3;

                        graphicsContext.setStroke(Renderer.selectionOutline2);
                        graphicsContext.setLineWidth(1);
                        graphicsContext.setLineDashes(3);
                        graphicsContext.setLineDashOffset(0);
                        graphicsContext.strokeRect(screenX2 - width * Main.getLevel().getZoom() / 2, screenY2 - height * Main.getLevel().getZoom() / 2, width * Main.getLevel().getZoom(), height * Main.getLevel().getZoom());
                        graphicsContext.setStroke(Renderer.selectionOutline);
                        graphicsContext.setLineWidth(1);
                        graphicsContext.setLineDashOffset(3);
                        graphicsContext.strokeRect(screenX2 - width * Main.getLevel().getZoom() / 2, screenY2 - height * Main.getLevel().getZoom() / 2, width * Main.getLevel().getZoom(), height * Main.getLevel().getZoom());
                        graphicsContext.setLineDashes(0);
                        graphicsContext.restore();
                    }

                } else {
                    double x2 = Double.parseDouble(getAttribute("x"));
                    double y2 = Double.parseDouble(getAttribute("y"));

                    double rotation = Math.toRadians(Double.parseDouble(getAttribute("angle")));

                    double width = ball.getShapeSize2();
                    double height = ball.getShapeSize();

                    Point2D center = new Point2D(x2, y2);

                    Point2D topRight2 = new Point2D(x2 + width / 2, y2 - height / 2).multiply(Main.getLevel().getZoom());
                    Point2D topLeft2 = new Point2D(x2 - width / 2, y2 - height / 2).multiply(Main.getLevel().getZoom());
                    Point2D bottomLeft2 = new Point2D(x2 - width / 2, y2 + height / 2).multiply(Main.getLevel().getZoom());
                    Point2D bottomRight2 = new Point2D(x2 + width / 2, y2 + height / 2).multiply(Main.getLevel().getZoom());

                    double woag = Math.min(Math.min(1.5, Math.abs(width) / 4), Math.abs(height) / 4);

                    Point2D topRight = new Point2D(x2 + width / 2 - woag, y2 - height / 2 + woag).multiply(Main.getLevel().getZoom());
                    Point2D topLeft = new Point2D(x2 - width / 2 + woag, y2 - height / 2 + woag).multiply(Main.getLevel().getZoom());
                    Point2D bottomLeft = new Point2D(x2 - width / 2 + woag, y2 + height / 2 - woag).multiply(Main.getLevel().getZoom());
                    Point2D bottomRight = new Point2D(x2 + width / 2 - woag, y2 + height / 2 - woag).multiply(Main.getLevel().getZoom());

                    topRight = EditorObject.rotate(topRight, rotation, center.multiply(Main.getLevel().getZoom()));
                    topLeft = EditorObject.rotate(topLeft, rotation, center.multiply(Main.getLevel().getZoom()));
                    bottomLeft = EditorObject.rotate(bottomLeft, rotation, center.multiply(Main.getLevel().getZoom()));
                    bottomRight = EditorObject.rotate(bottomRight, rotation, center.multiply(Main.getLevel().getZoom()));

                    topRight2 = EditorObject.rotate(topRight2, rotation, center.multiply(Main.getLevel().getZoom()));
                    topLeft2 = EditorObject.rotate(topLeft2, rotation, center.multiply(Main.getLevel().getZoom()));
                    bottomLeft2 = EditorObject.rotate(bottomLeft2, rotation, center.multiply(Main.getLevel().getZoom()));
                    bottomRight2 = EditorObject.rotate(bottomRight2, rotation, center.multiply(Main.getLevel().getZoom()));

                    topRight = new Point2D((topRight.getX()) + Main.getLevel().getOffsetX(), (-topRight.getY()) + Main.getLevel().getOffsetY());
                    topLeft = new Point2D((topLeft.getX()) + Main.getLevel().getOffsetX(), (-topLeft.getY()) + Main.getLevel().getOffsetY());
                    bottomLeft = new Point2D((bottomLeft.getX()) + Main.getLevel().getOffsetX(), (-bottomLeft.getY()) + Main.getLevel().getOffsetY());
                    bottomRight = new Point2D((bottomRight.getX()) + Main.getLevel().getOffsetX(), (-bottomRight.getY()) + Main.getLevel().getOffsetY());

                    topRight2 = new Point2D((topRight2.getX()) + Main.getLevel().getOffsetX(), (-topRight2.getY()) + Main.getLevel().getOffsetY());
                    topLeft2 = new Point2D((topLeft2.getX()) + Main.getLevel().getOffsetX(), (-topLeft2.getY()) + Main.getLevel().getOffsetY());
                    bottomLeft2 = new Point2D((bottomLeft2.getX()) + Main.getLevel().getOffsetX(), (-bottomLeft2.getY()) + Main.getLevel().getOffsetY());
                    bottomRight2 = new Point2D((bottomRight2.getX()) + Main.getLevel().getOffsetX(), (-bottomRight2.getY()) + Main.getLevel().getOffsetY());

                    graphicsContext.setStroke(Renderer.middleColor);
                    graphicsContext.setLineWidth(2 * woag * Main.getLevel().getZoom());
                    graphicsContext.strokeLine(topRight.getX(), topRight.getY(), topLeft.getX(), topLeft.getY());
                    graphicsContext.strokeLine(bottomLeft.getX(), bottomLeft.getY(), bottomRight.getX(), bottomRight.getY());
                    graphicsContext.strokeLine(topLeft.getX(), topLeft.getY(), bottomLeft.getX(), bottomLeft.getY());
                    graphicsContext.strokeLine(bottomRight.getX(), bottomRight.getY(), topRight.getX(), topRight.getY());

                    if (this == Main.getSelected()) {
                        graphicsContext.setStroke(Renderer.selectionOutline2);
                        graphicsContext.setLineWidth(1);
                        graphicsContext.setLineDashes(3);
                        graphicsContext.setLineDashOffset(0);
                        graphicsContext.strokePolygon(new double[]{topRight2.getX(), topLeft2.getX(), bottomLeft2.getX(), bottomRight2.getX()}, new double[]{topRight2.getY(), topLeft2.getY(), bottomLeft2.getY(), bottomRight2.getY()}, 4);
                        graphicsContext.setStroke(Renderer.selectionOutline);
                        graphicsContext.setLineWidth(1);
                        graphicsContext.setLineDashOffset(3);
                        graphicsContext.strokePolygon(new double[]{topRight2.getX(), topLeft2.getX(), bottomLeft2.getX(), bottomRight2.getX()}, new double[]{topRight2.getY(), topLeft2.getY(), bottomLeft2.getY(), bottomRight2.getY()}, 4);
                        graphicsContext.setLineDashes(0);
                    }
                }
            }
        }
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {
        if (Main.getLevel().getShowGoos() == 2 && ball != null) {
            double angle = Double.parseDouble(getAttribute("angle"));

            double width;
            double height;

            if (ball.getShapeType().equals("rectangle")) {
                width = ball.getShapeSize2();
                height = ball.getShapeSize();
            } else {
                width = ball.getShapeSize();
                height = ball.getShapeSize();
            }

            double x = Double.parseDouble(getAttribute("x"));
            double y = -Double.parseDouble(getAttribute("y"));

            Point2D rotated = rotate(new Point2D(mX2, mY2), Math.toRadians(angle), new Point2D(x, y));

            double mX = rotated.getX();
            double mY = rotated.getY();



            if (mX > x - width / 2 && mX < x + width / 2 && mY > y - height / 2 && mY < y + height / 2) {

                int i = 0;
                for (EditorObject part : ball.getObjects()) {

                    String state = "standing";

                    if (getAttribute("discovered").equals("false")) {
                        state = "sleeping";
                    } else {
                        for (EditorObject obj : Main.getLevel().getLevel()) {
                            if (obj instanceof Strand) {
                                if (obj.getAttribute("gb1").equals(getAttribute("id")) || obj.getAttribute("gb2").equals(getAttribute("id"))) {
                                    state = "attached";
                                    break;
                                }
                            }
                        }
                    }

                    i++;
                    if (part instanceof Part) {

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

                        if (ok) {
                            Random machine = new Random(randomSeed * i);
                            machine.nextDouble();
                            Image img = ((Part) part).getImages().get((int) (((Part) part).getImages().size() * machine.nextDouble()));
                            if (img != null) {
                                double scale = Double.parseDouble(part.getAttribute("scale"));

                                double myX = InputField.getRange(part.getAttribute("x"), machine.nextDouble());
                                double myY = InputField.getRange(part.getAttribute("y"), machine.nextDouble());

                                double screenX = Double.parseDouble(getAttribute("x")) + myX - img.getWidth() / 2.0 * scale;
                                double screenY = -Double.parseDouble(getAttribute("y")) - myY - img.getHeight() / 2.0 * scale;

                                if (mX >= screenX && mY >= screenY && (mX - screenX) / scale < img.getWidth() && (mY - screenY) / scale < img.getHeight()) {
                                    if (img.getPixelReader().getArgb((int) ((mX - screenX) / scale), (int) ((mY - screenY) / scale)) != 0) {
                                        DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                                        dragSettings.setInitialSourceX(mX - x);
                                        dragSettings.setInitialSourceY(mY - y);
                                        return dragSettings;
                                    }
                                }

                                if (((Part) part).getPupilImage() != null) {
                                    Image pupilImg = ((Part) part).getPupilImage();

                                    double screenX2 = Double.parseDouble(getAttribute("x")) + myX - pupilImg.getWidth() / 2.0 * scale;
                                    double screenY2 = -Double.parseDouble(getAttribute("y")) - myY - pupilImg.getHeight() / 2.0 * scale;

                                    if (mX >= screenX2 && mY >= screenY2 && (mX - screenX2) / scale < pupilImg.getWidth() && (mY - screenY2) / scale < pupilImg.getHeight()) {
                                        if (pupilImg.getPixelReader().getArgb((int) ((mX - screenX2) / scale), (int) ((mY - screenY2) / scale)) != 0) {
                                            DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                                            dragSettings.setInitialSourceX(mX - x);
                                            dragSettings.setInitialSourceY(mY - y);
                                            return dragSettings;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (Main.getLevel().getShowGoos() == 1) {

            if (ball.getShapeType().equals("rectangle")) {

                double x2 = Double.parseDouble(getAttribute("x"));
                double y2 = Double.parseDouble(getAttribute("y"));

                double rotation = Math.toRadians(Double.parseDouble(getAttribute("angle")));

                double width = ball.getShapeSize2();
                double height = ball.getShapeSize();
                double width2 = width - 6;
                double height2 = height - 6;

                Point2D rotated = rotate(new Point2D(mX2, mY2), rotation, new Point2D(x2, -y2));

                double mX = rotated.getX();
                double mY = rotated.getY();

                if (mX > x2 - width / 2 && mX < x2 + width / 2 && mY > -y2 - height / 2 && mY < -y2 + height / 2 &&
                        !(mX > x2 - width2 / 2 && mX < x2 + width2 / 2 && mY > -y2 - height2 / 2 && mY < -y2 + height2 / 2)) {
                    DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                    dragSettings.setInitialSourceX(mX2 - x2);
                    dragSettings.setInitialSourceY(mY2 + y2);
                    return dragSettings;
                } else {
                    return new DragSettings(DragSettings.NONE);
                }

            } else {
                double radius1 = ball.getShapeSize() / 2;
                double radius2 = radius1 - 3;

                double x = Double.parseDouble(getAttribute("x"));
                double y = Double.parseDouble(getAttribute("y"));

                if ((mX2 - x) * (mX2 - x) + (mY2 + y) * (mY2 + y) < radius1 * radius1 &&
                        (mX2 - x) * (mX2 - x) + (mY2 + y) * (mY2 + y) > radius2 * radius2) {
                    DragSettings dragSettings = new DragSettings(DragSettings.MOVE);
                    dragSettings.setInitialSourceX(mX2 - x);
                    dragSettings.setInitialSourceY(mY2 + y);
                    return dragSettings;
                } else {
                    return new DragSettings(DragSettings.NONE);
                }
            }

        }
        return new DragSettings(DragSettings.NONE);
    }
}
