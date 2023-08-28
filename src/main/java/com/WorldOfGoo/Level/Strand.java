package com.WorldOfGoo.Level;

import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.GUI.Alarms;
import com.WooGLEFX.Structures.*;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import com.WorldOfGoo.Ball.BallStrand;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;

import java.util.HexFormat;

public class Strand extends EditorObject {

    private BallStrand strand;
    private Image strandImage;


    private BallInstance goo1 = null;
    private BallInstance goo2 = null;

    public BallInstance getGoo1() {
        return goo1;
    }

    public void setGoo1(BallInstance goo1) {
        this.goo1 = goo1;
    }

    public BallInstance getGoo2() {
        return goo2;
    }

    public void setGoo2(BallInstance goo2) {
        this.goo2 = goo2;
    }

    public void setStrand(BallStrand strand) {
        this.strand = strand;
    }

    private int strandomSeed;
    //TODO make strands selectable
    public Strand(EditorObject _parent) {
        super(_parent);
        setRealName("Strand");
        addAttribute("gb1", "", InputField.GOOBALL_ID, true);
        addAttribute("gb2", "", InputField.GOOBALL_ID, true);
        setNameAttribute(getAttribute2("gb1"));
        setNameAttribute2(getAttribute2("gb2"));
        setChangeListener("gb2", (observableValue, s, t1) -> {
            String bruh = getAttribute("gb1");
            setAttribute("gb1", "AAAAA");
            setAttribute("gb1", bruh);
        });
        setMetaAttributes(MetaEditorAttribute.parse("gb1,gb2,"));
    }

    @Override
    public void update() {
        for (EditorObject object : Main.getLevel().getLevel()) {
            if (object instanceof BallInstance && object.getAttribute("id").equals(getAttribute("gb2"))) {
                goo2 = (BallInstance)object;
                ((BallInstance) object).getStrands().add(this);
                for (_Ball ball2 : Main.getImportedBalls()) {
                    if (ball2.getObjects().get(0).getAttribute("name").equals(object.getAttribute("type"))) {
                        for (EditorObject possiblyStrand : ball2.getObjects()) {
                            if (possiblyStrand.getRealName().equals("strand")) {
                                strand = (BallStrand) possiblyStrand;
                            }
                        }
                    }
                }
            }
            if (object instanceof BallInstance && object.getAttribute("id").equals(getAttribute("gb1"))) {
                ((BallInstance) object).getStrands().add(this);
            }
        }
        for (EditorObject object : Main.getLevel().getLevel()) {
            if (object instanceof BallInstance && object.getAttribute("id").equals(getAttribute("gb1"))) {
                goo1 = (BallInstance)object;
                if (strand == null) {
                    for (_Ball ball2 : Main.getImportedBalls()) {
                        if (ball2.getObjects().get(0).getAttribute("name").equals(object.getAttribute("type"))) {
                            for (EditorObject possiblyStrand : ball2.getObjects()) {
                                if (possiblyStrand.getRealName().equals("strand")) {
                                    strand = (BallStrand) possiblyStrand;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (strand != null) {
            try {
                strandImage = GlobalResourceManager.getImage(strand.getAttribute("image"), Main.getLevel().getVersion());
            } catch (Exception e) {
                if (!Main.failedResources.contains("From strand: \"" + strand.getAttribute("image") + "\" (version " + Main.getLevel().getVersion() + ")")) {
                    Main.failedResources.add("From strand: \"" + strand.getAttribute("image") + "\" (version " + Main.getLevel().getVersion() + ")");
                }
            }
        }
    }

    private Point2D lineIntersection(double x1, double y1, double m1, double x2, double y2, double m2) {
        double x = (m1 * x1 - m2 * x2 + y2 - y1) / (m1 - m2);
        return new Point2D(x, m1 * (x - x1) + y1);
    }

    @Override
    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {
        if (Main.getLevel().getShowGoos() == 2) {
            if (goo1 != null && goo2 != null && strand != null) {
                Position pos1 = new Position(goo1.getDouble("x"), -goo1.getDouble("y"));;
                Position pos2 = new Position(goo2.getDouble("x"), -goo2.getDouble("y"));
                if (strandImage == null) {
                    update();
                }
                double width = strand.getDouble("thickness");
                double height = new Point2D(pos1.getX(), pos1.getY()).distance(new Point2D(pos2.getX(), pos2.getY()));

                imageGraphicsContext.save();
                Affine t = imageGraphicsContext.getTransform();
                t.appendRotation(180 - Math.toDegrees(Renderer.angleTo(new Point2D(pos2.getX(), pos2.getY()), new Point2D(pos1.getX(), pos1.getY()))) + 90, pos2.getX(), pos2.getY());
                imageGraphicsContext.setTransform(t);

                imageGraphicsContext.drawImage(strandImage, pos2.getX() - width / 2, pos2.getY(), width, height);

                imageGraphicsContext.restore();
            }

        } else if (Main.getLevel().getShowGoos() == 1) {

            if (goo1 != null && goo2 != null) {
                Position pos1 = new Position(goo1.getDouble("x"), -goo1.getDouble("y"));
                Position size1 = new Position(goo1.getBall().getShapeSize(), goo1.getBall().getShapeSize2());
                boolean circle1 = goo1.getBall().getShapeType().equals("circle");
                double rot1 = goo1.getDouble("angle");

                Position pos2 = new Position(goo2.getDouble("x"), -goo2.getDouble("y"));
                Position size2 = new Position(goo2.getBall().getShapeSize(), goo2.getBall().getShapeSize2());
                boolean circle2 = goo2.getBall().getShapeType().equals("circle");
                double rot2 = goo2.getDouble("angle");

                double maxSize = -1;
                double minSize = -1;
                for (EditorObject object2 : goo2.getBall().getObjects()) {
                    if (object2 instanceof BallStrand) {
                        minSize = object2.getDouble("minlen") / 2;
                        maxSize = object2.getDouble("maxlen2") / 2;
                    }
                }

                graphicsContext.save();
                graphicsContext.setTransform(new Affine());

                graphicsContext.setStroke(Renderer.middleColor);
                graphicsContext.setLineWidth(Main.getLevel().getZoom() * 3);

                double theta = Renderer.angleTo(new Point2D(pos1.getX(), pos1.getY()), new Point2D(pos2.getX(), pos2.getY()));

                Point2D hit1;
                Point2D hit2;

                if (circle1) {
                    double r1 = size1.getX() / 2;
                    hit1 = new Point2D(pos1.getX() + r1 * Math.cos(theta), pos1.getY() - r1 * Math.sin(theta));
                } else {
                    hit1 = Main.lineBoxIntersection(pos2.getX(), pos2.getY(), theta - 3.1415, pos1.getX(), pos1.getY(), size1.getX(), size1.getY(), Math.toRadians(rot1));
                }

                if (circle2) {
                    double r2 = size2.getX() / 2;
                    hit2 = new Point2D(pos2.getX() - r2 * Math.cos(theta), pos2.getY() + r2 * Math.sin(theta));
                } else {
                    hit2 = Main.lineBoxIntersection(pos1.getX(), pos1.getY(), theta, pos2.getX(), pos2.getY(), size2.getX(), size2.getY(), Math.toRadians(rot2));
                }

                double colorWeightA = 1.5 / (0.5 + Math.exp((Math.log(0.25)) / maxSize * hit1.distance(hit2))) - 2;
                double colorWeightB = 1.5 / (0.5 + Math.exp((Math.log(0.25)) / minSize * hit1.distance(hit2))) - 2;

                if (colorWeightA > 0) {
                    graphicsContext.setStroke(Paint.valueOf(HexFormat.of().toHexDigits((int) (Math.min(colorWeightA * 256, 128) + 127)).substring(6) + "8080FF"));
                }
                if (colorWeightB < 0) {
                    graphicsContext.setStroke(Paint.valueOf("8080" + HexFormat.of().toHexDigits((int) (Math.min(-colorWeightB * 256, 128) + 127)).substring(6) + "FF"));
                }

                graphicsContext.strokeLine(hit1.getX() * Main.getLevel().getZoom() + Main.getLevel().getOffsetX(), hit1.getY() * Main.getLevel().getZoom() + Main.getLevel().getOffsetY(), hit2.getX() * Main.getLevel().getZoom() + Main.getLevel().getOffsetX(), hit2.getY() * Main.getLevel().getZoom() + Main.getLevel().getOffsetY());
                graphicsContext.restore();
            }
        }
    }

    @Override
    public DragSettings mouseIntersection(double mX2, double mY2) {

        return new DragSettings(DragSettings.NONE);

    }
}
