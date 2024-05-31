package com.worldOfGoo.level;

import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.gameData.ball._Ball;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.gameData.ball.BallManager;
import com.woogleFX.gameData.level.levelOpening.LevelLoader;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import com.woogleFX.editorObjects.attributes.dataTypes.Position;
import com.worldOfGoo.ball.BallStrand;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Strand extends EditorObject {

    private BallStrand strand;
    public void setStrand(BallStrand strand) {
        this.strand = strand;
    }


    private Image strandImage;


    private BallInstance goo1 = null;
    public void setGoo1(BallInstance goo1) {
        this.goo1 = goo1;
    }


    private BallInstance goo2 = null;
    public void setGoo2(BallInstance goo2) {
        this.goo2 = goo2;
    }


    private int strandBallID = 2;


    public Strand(EditorObject _parent, GameVersion version) {
        super(_parent, "Strand", version);

        addAttribute("gb1", InputField.GOOBALL_ID).assertRequired();
        addAttribute("gb2", InputField.GOOBALL_ID).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("gb1,gb2,"));

        getAttribute("gb1").addChangeListener(((observable, oldValue, newValue) -> addPartAsObjectPosition()));
        getAttribute("gb2").addChangeListener(((observable, oldValue, newValue) -> addPartAsObjectPosition()));

    }


    @Override
    public String getName() {
        String gb1 = getAttribute("gb1").stringValue();
        String gb2 = getAttribute("gb2").stringValue();
        return gb1 + ", " + gb2;
    }


    private boolean setStrand(String type) {

        for (_Ball ball : BallManager.getImportedBalls()) {
            String ballType = ball.getObjects().get(0).getAttribute("name").stringValue();
            if (ballType.equals(type)) {
                for (EditorObject object : ball.getObjects()) if (object instanceof BallStrand strand2) {
                    this.strand = strand2;
                    return true;
                }
            }
        }

        return false;

    }


    @Override
    public void update() {

        if (LevelManager.getLevel() == null) return;

        for (EditorObject obj : LevelManager.getLevel().getLevel()) if (obj instanceof BallInstance ballInstance) {

            String id = ballInstance.getAttribute("id").stringValue();
            String gb1 = getAttribute("gb1").stringValue();
            String gb2 = getAttribute("gb2").stringValue();

            if (id.equals(gb1)) {

                goo1 = ballInstance;

                if (strand == null) {

                    String type = ballInstance.getAttribute("type").stringValue();
                    if (setStrand(type)) strandBallID = 1;

                }

            }

            else if (id.equals(gb2)) {

                goo2 = ballInstance;

                String type = ballInstance.getAttribute("type").stringValue();
                if (setStrand(type)) strandBallID = 2;

            }

        }

        if (strand != null) {
            try {
                _Ball ball;
                if (strandBallID == 1) ball = goo1.getBall(); else ball = goo2.getBall();
                if (goo1.getBall() == null || goo2.getBall() == null) return;
                strandImage = strand.getAttribute("image").imageValue(ball.getResources(), getVersion());
            } catch (Exception e) {
                // TODO make this cleaner
                if (!LevelLoader.failedResources.contains("From Strand: \"" + strand.getAttribute("image").stringValue() + "\" (version " + getVersion() + ")")) {
                    LevelLoader.failedResources.add("From Strand: \"" + strand.getAttribute("image").stringValue() + "\" (version " + getVersion() + ")");
                }
            }
        }

        if (strandImage != null) addPartAsObjectPosition();

    }


    private static Point2D lineLineSegmentIntersection(double x1, double y1, double theta, double x2, double y2,
                                                      double x3, double y3) {
        // logger.info(x1 + ", " + y1 + ", " + theta + ", " + x2 + ", " + y2 + ",
        // " + x3 + ", " + y3);
        if (y3 == y2) {
            y3 += 0.00001;
        }
        if (x3 == x2) {
            x3 += 0.00001;
        }
        double m = (y3 - y2) / (x3 - x2);
        double x = (y2 - x2 * m + x1 * Math.tan(theta) - y1) / (Math.tan(theta) - m);
        double y = (x - x1) * Math.tan(theta) + y1;

        double bruh = 0.01;
        // logger.info(x + ", " + y);
        // logger.info(y + ", " + ((x - x2) * m + y2));
        // 385.94690307546693, 682.9469030754669
        if (x > Math.min(x2, x3) - bruh && x < Math.max(x2, x3) + bruh && y > Math.min(y2, y3) - bruh
                && y < Math.max(y2, y3) + bruh) {
            // logger.info("e");
            return new Point2D(x, y);
        } else {
            return null;
        }
    }

    private static Point2D lineBoxIntersection(double x1, double y1, double theta, double x2, double y2, double sizeX,
                                              double sizeY, double rotation) {

        Point2D topLeft = ObjectUtil.rotate(new Point2D(x2 - sizeX / 2, y2 - sizeY / 2), rotation,
                new Point2D(x2, y2));
        Point2D topRight = ObjectUtil.rotate(new Point2D(x2 + sizeX / 2, y2 - sizeY / 2), rotation,
                new Point2D(x2, y2));
        Point2D bottomLeft = ObjectUtil.rotate(new Point2D(x2 - sizeX / 2, y2 + sizeY / 2), rotation,
                new Point2D(x2, y2));
        Point2D bottomRight = ObjectUtil.rotate(new Point2D(x2 + sizeX / 2, y2 + sizeY / 2), rotation,
                new Point2D(x2, y2));

        Point2D top = lineLineSegmentIntersection(x1, y1, theta, topLeft.getX(), topLeft.getY(), topRight.getX(),
                topRight.getY());
        Point2D bottom = lineLineSegmentIntersection(x1, y1, theta, bottomLeft.getX(), bottomLeft.getY(),
                bottomRight.getX(), bottomRight.getY());
        Point2D left = lineLineSegmentIntersection(x1, y1, theta, topLeft.getX(), topLeft.getY(), bottomLeft.getX(),
                bottomLeft.getY());
        Point2D right = lineLineSegmentIntersection(x1, y1, theta, topRight.getX(), topRight.getY(),
                bottomRight.getX(), bottomRight.getY());

        Point2D origin = new Point2D(x1, y1);

        double topDistance = top == null ? 100000000 : top.distance(origin);
        double bottomDistance = bottom == null ? 100000000 : bottom.distance(origin);
        double leftDistance = left == null ? 100000000 : left.distance(origin);
        double rightDistance = right == null ? 100000000 : right.distance(origin);

        if (topDistance < bottomDistance && topDistance < leftDistance && topDistance < rightDistance) {
            return top;
        }

        if (bottomDistance < leftDistance && bottomDistance < rightDistance) {
            return bottom;
        }

        if (leftDistance < rightDistance) {
            return left;
        }

        if (right == null) {
            return new Point2D(0, 0);
        }
        return right;
    }


    private void addPartAsObjectPosition() {

        clearObjectPositions();

        if (goo1 == null || goo2 == null) return;

        if (strandImage == null) {
            update();
        }

        if (strandImage != null) addObjectComponent(new ImageComponent() {
            public double getX() {
                double x1 = goo1.getAttribute("x").doubleValue();
                double x2 = goo2.getAttribute("x").doubleValue();
                return (x1 + x2) / 2;
            }
            public double getY() {
                double y1 = -goo1.getAttribute("y").doubleValue();
                double y2 = -goo2.getAttribute("y").doubleValue();
                return (y1 + y2) / 2;
            }
            public double getRotation() {

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();

                double x2 = goo2.getAttribute("x").doubleValue();
                double y2 = -goo2.getAttribute("y").doubleValue();

                return Math.PI / 2 + Renderer.angleTo(new Point2D(x1, y1), new Point2D(x2, y2));

            }
            public double getScaleX() {
                if (strand == null) return 0;
                return strand.getAttribute("thickness").doubleValue() / strandImage.getWidth();
            }
            public double getScaleY() {

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();

                double x2 = goo2.getAttribute("x").doubleValue();
                double y2 = -goo2.getAttribute("y").doubleValue();

                return Math.hypot(x2 - x1, y2 - y1) / strandImage.getHeight();

            }
            public Image getImage() {
                return strandImage;
            }
            public double getDepth() {
                return 0.00000001;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGoos() == 2;
            }
            public boolean isDraggable() {
                return false;
            }
            public boolean isResizable() {
                return false;
            }
            public boolean isRotatable() {
                return false;
            }
        });

        addObjectComponent(new RectangleComponent() {

            public double getX() {

                if (goo1 == null || goo2 == null) return 0;

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();
                double rotation1 = -Math.toRadians(goo1.getAttribute("angle").doubleValue());

                double x2 = goo2.getAttribute("x").doubleValue();
                double y2 = -goo2.getAttribute("y").doubleValue();
                double rotation2 = -Math.toRadians(goo2.getAttribute("angle").doubleValue());

                Position size1 = goo1.getBall() == null ? new Position(30, 30) : new Position(goo1.getBall().getShapeSize(), goo1.getBall().getShapeSize2());
                boolean circle1 = goo1.getBall() == null || goo1.getBall().getShapeType().equals("circle");

                Position size2 = goo2.getBall() == null ? new Position(30, 30) : new Position(goo2.getBall().getShapeSize(), goo2.getBall().getShapeSize2());
                boolean circle2 = goo2.getBall() == null || goo2.getBall().getShapeType().equals("circle");

                double theta = Renderer.angleTo(new Point2D(x1, y1), new Point2D(x2, y2));

                Point2D hit1;
                Point2D hit2;

                if (circle1) {
                    double r1 = size1.getX() / 2;
                    hit1 = new Point2D(x1 + r1 * Math.cos(theta), y1 + r1 * Math.sin(theta));
                } else {
                    hit1 = lineBoxIntersection(x2, y2, theta - Math.PI, x1, y1, size1.getX(), size1.getY(), rotation1);
                }

                if (circle2) {
                    double r2 = size2.getX() / 2;
                    hit2 = new Point2D(x2 - r2 * Math.cos(theta), y2 - r2 * Math.sin(theta));
                } else {
                    hit2 = lineBoxIntersection(x1, y1, theta, x2, y2, size2.getX(), size2.getY(), rotation2);
                }

                return (hit1.getX() + hit2.getX()) / 2;

            }

            public double getY() {

                if (goo1 == null || goo2 == null) return 0;

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();
                double rotation1 = -Math.toRadians(goo1.getAttribute("angle").doubleValue());

                double x2 = goo2.getAttribute("x").doubleValue();
                double y2 = -goo2.getAttribute("y").doubleValue();
                double rotation2 = -Math.toRadians(goo2.getAttribute("angle").doubleValue());

                Position size1 = goo1.getBall() == null ? new Position(30, 30) : new Position(goo1.getBall().getShapeSize(), goo1.getBall().getShapeSize2());
                boolean circle1 = goo1.getBall() == null || goo1.getBall().getShapeType().equals("circle");

                Position size2 = goo2.getBall() == null ? new Position(30, 30) : new Position(goo2.getBall().getShapeSize(), goo2.getBall().getShapeSize2());
                boolean circle2 = goo2.getBall() == null || goo2.getBall().getShapeType().equals("circle");

                double theta = Renderer.angleTo(new Point2D(x1, y1), new Point2D(x2, y2));

                Point2D hit1;
                Point2D hit2;

                if (circle1) {
                    double r1 = size1.getX() / 2;
                    hit1 = new Point2D(x1 + r1 * Math.cos(theta), y1 + r1 * Math.sin(theta));
                } else {
                    hit1 = lineBoxIntersection(x2, y2, theta - Math.PI, x1, y1, size1.getX(), size1.getY(), rotation1);
                }

                if (circle2) {
                    double r2 = size2.getX() / 2;
                    hit2 = new Point2D(x2 - r2 * Math.cos(theta), y2 - r2 * Math.sin(theta));
                } else {
                    hit2 = lineBoxIntersection(x1, y1, theta, x2, y2, size2.getX(), size2.getY(), rotation2);
                }

                return (hit1.getY() + hit2.getY()) / 2;

            }

            public double getRotation() {

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();

                double x2 = goo2.getAttribute("x").doubleValue();
                double y2 = -goo2.getAttribute("y").doubleValue();

                return Math.PI / 2 + Renderer.angleTo(new Point2D(x1, y1), new Point2D(x2, y2));

            }

            public double getWidth() {
                return 3;
            }

            public double getHeight() {

                if (goo1 == null || goo2 == null) return 0;

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();
                double rotation1 = -Math.toRadians(goo1.getAttribute("angle").doubleValue());

                double x2 = goo2.getAttribute("x").doubleValue();
                double y2 = -goo2.getAttribute("y").doubleValue();
                double rotation2 = -Math.toRadians(goo2.getAttribute("angle").doubleValue());

                Position size1 = goo1.getBall() == null ? new Position(30, 30) : new Position(goo1.getBall().getShapeSize(), goo1.getBall().getShapeSize2());
                boolean circle1 = goo1.getBall() == null || goo1.getBall().getShapeType().equals("circle");

                Position size2 = goo2.getBall() == null ? new Position(30, 30) : new Position(goo2.getBall().getShapeSize(), goo2.getBall().getShapeSize2());
                boolean circle2 = goo2.getBall() == null || goo2.getBall().getShapeType().equals("circle");

                double theta = Renderer.angleTo(new Point2D(x1, y1), new Point2D(x2, y2));

                Point2D hit1;
                Point2D hit2;

                if (circle1) {
                    double r1 = size1.getX() / 2;
                    hit1 = new Point2D(x1 + r1 * Math.cos(theta), y1 + r1 * Math.sin(theta));
                } else {
                    hit1 = lineBoxIntersection(x2, y2, theta - Math.PI, x1, y1, size1.getX(), size1.getY(), rotation1);
                }

                if (circle2) {
                    double r2 = size2.getX() / 2;
                    hit2 = new Point2D(x2 - r2 * Math.cos(theta), y2 - r2 * Math.sin(theta));
                } else {
                    hit2 = lineBoxIntersection(x1, y1, theta, x2, y2, size2.getX(), size2.getY(), rotation2);
                }

                return Math.hypot(hit2.getY() - hit1.getY(), hit2.getX() - hit1.getX()) + 3.0;

            }

            public double getEdgeSize() {
                return 0;
            }
            public boolean isEdgeOnly() {
                return false;
            }

            public double getDepth(){
                return 0.00000001;
            }

            public Paint getBorderColor() {
                return new Color(0.0, 0.0, 0.0, 0.0);
            }

            public Paint getColor() {

                double length = getHeight();

                double minSize = strand.getAttribute("minlen").doubleValue();
                double maxSize = strand.getAttribute("maxlen2").doubleValue();

                if (length > maxSize) return new Color(1.0, 0.0, 0.0, 1.0);
                if (length < minSize) return new Color(0.0, 0.0, 1.0, 1.0);
                else return new Color(0.5, 0.5, 0.5, 1.0);

            }

            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().getShowGoos() == 1;
            }
            public boolean isDraggable() {
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
