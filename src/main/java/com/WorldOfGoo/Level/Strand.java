package com.WorldOfGoo.Level;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectUtil;
import com.WooGLEFX.EditorObjects._Ball;
import com.WooGLEFX.Engine.Renderer;
import com.WooGLEFX.Functions.BallManager;
import com.WooGLEFX.Functions.LevelLoader;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.InputField;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WorldOfGoo.Ball.BallStrand;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
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


    public Strand(EditorObject _parent) {
        super(_parent, "Strand");

        addAttribute("gb1", InputField.GOOBALL_ID).assertRequired();
        addAttribute("gb2", InputField.GOOBALL_ID).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("gb1,gb2,"));

    }


    @Override
    public String getName() {
        String gb1 = getAttribute("gb1").stringValue();
        String gb2 = getAttribute("gb2").stringValue();
        return gb1 + ", " + gb2;
    }


    private void setStrand(String type) {

        for (_Ball ball : BallManager.getImportedBalls()) {
            String ballType = ball.getObjects().get(0).getAttribute("name").stringValue();
            if (ballType.equals(type)) {
                for (EditorObject object : ball.getObjects()) if (object instanceof BallStrand strand2) {
                    this.strand = strand2;
                }
            }
        }

    }


    @Override
    public void update() {

        for (EditorObject obj : LevelManager.getLevel().getLevel()) if (obj instanceof BallInstance ballInstance) {

            String id = ballInstance.getAttribute("id").stringValue();
            String gb1 = getAttribute("gb1").stringValue();
            String gb2 = getAttribute("gb2").stringValue();

            if (id.equals(gb1)) {

                goo1 = ballInstance;

                if (strand == null) {

                    String type = ballInstance.getAttribute("type").stringValue();
                    setStrand(type);

                }

            }

            else if (id.equals(gb2)) {

                goo2 = ballInstance;

                String type = ballInstance.getAttribute("type").stringValue();
                setStrand(type);

            }

        }

        if (strand != null) {
            try {
                strandImage = strand.getAttribute("image").imageValue(LevelManager.getVersion());
            } catch (Exception e) {
                // TODO make this cleaner
                if (!LevelLoader.failedResources.contains("From strand: \"" + strand.getAttribute("image") + "\" (version " + LevelManager.getVersion() + ")")) {
                    LevelLoader.failedResources.add("From strand: \"" + strand.getAttribute("image") + "\" (version " + LevelManager.getVersion() + ")");
                }
            }
        }

        if (strand != null) addPartAsObjectPosition();

    }

    private Point2D lineIntersection(double x1, double y1, double m1, double x2, double y2, double m2) {
        double x = (m1 * x1 - m2 * x2 + y2 - y1) / (m1 - m2);
        return new Point2D(x, m1 * (x - x1) + y1);
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

        Point2D topLeft = ObjectUtil.rotate(new Point2D(x2 - sizeX / 2, y2 - sizeY / 2), -rotation,
                new Point2D(x2, y2));
        Point2D topRight = ObjectUtil.rotate(new Point2D(x2 + sizeX / 2, y2 - sizeY / 2), -rotation,
                new Point2D(x2, y2));
        Point2D bottomLeft = ObjectUtil.rotate(new Point2D(x2 - sizeX / 2, y2 + sizeY / 2), -rotation,
                new Point2D(x2, y2));
        Point2D bottomRight = ObjectUtil.rotate(new Point2D(x2 + sizeX / 2, y2 + sizeY / 2), -rotation,
                new Point2D(x2, y2));

        Point2D top = lineLineSegmentIntersection(x1, y1, -theta, topLeft.getX(), topLeft.getY(), topRight.getX(),
                topRight.getY());
        Point2D bottom = lineLineSegmentIntersection(x1, y1, -theta, bottomLeft.getX(), bottomLeft.getY(),
                bottomRight.getX(), bottomRight.getY());
        Point2D left = lineLineSegmentIntersection(x1, y1, -theta, topLeft.getX(), topLeft.getY(), bottomLeft.getX(),
                bottomLeft.getY());
        Point2D right = lineLineSegmentIntersection(x1, y1, -theta, topRight.getX(), topRight.getY(),
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

        if (strandImage != null) addObjectPosition(new ObjectPosition(ObjectPosition.IMAGE) {
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
            public double getWidth() {
                return strand.getAttribute("thickness").doubleValue();
            }
            public double getHeight() {

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();

                double x2 = goo2.getAttribute("x").doubleValue();
                double y2 = -goo2.getAttribute("y").doubleValue();

                return Math.hypot(x2 - x1, y2 - y1);

            }
            public Image getImage(){
                return strandImage;
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getShowGoos() == 2;
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

        addObjectPosition(new ObjectPosition(ObjectPosition.RECTANGLE) {

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

            public double getWidth() {
                return 3;
            }

            public double getHeight() {

                double x1 = goo1.getAttribute("x").doubleValue();
                double y1 = -goo1.getAttribute("y").doubleValue();

                double x2 = goo2.getAttribute("x").doubleValue();
                double y2 = -goo2.getAttribute("y").doubleValue();

                return Math.hypot(x2 - x1, y2 - y1);

            }

            public double getEdgeSize() {
                return 3;
            }

            public double getDepth(){
                return 0.00000001;
            }

            public Paint getBorderColor() {

                double length = getHeight();

                double minSize = strand.getAttribute("minlen").doubleValue();
                double maxSize = strand.getAttribute("maxlen2").doubleValue();

                if (length > maxSize) return new Color(1.0, 0.0, 0.0, 1.0);
                if (length < minSize) return new Color(0.0, 0.0, 1.0, 1.0);
                else return new Color(0.5, 0.5, 0.5, 1.0);

            }

            public boolean isVisible() {
                return LevelManager.getLevel().getShowGoos() == 1;
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


    public void draw(GraphicsContext graphicsContext) {

        /*
        if (LevelManager.getLevel().getShowGoos() == 1) {

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
                graphicsContext.setLineWidth(LevelManager.getLevel().getZoom() * 3);

                double theta = Renderer.angleTo(new Point2D(pos1.getX(), pos1.getY()), new Point2D(pos2.getX(), pos2.getY()));

                Point2D hit1;
                Point2D hit2;

                if (circle1) {
                    double r1 = size1.getX() / 2;
                    hit1 = new Point2D(pos1.getX() + r1 * Math.cos(theta), pos1.getY() - r1 * Math.sin(theta));
                } else {
                    hit1 = lineBoxIntersection(pos2.getX(), pos2.getY(), theta - 3.1415, pos1.getX(), pos1.getY(), size1.getX(), size1.getY(), Math.toRadians(rot1));
                }

                if (circle2) {
                    double r2 = size2.getX() / 2;
                    hit2 = new Point2D(pos2.getX() - r2 * Math.cos(theta), pos2.getY() + r2 * Math.sin(theta));
                } else {
                    hit2 = lineBoxIntersection(pos1.getX(), pos1.getY(), theta, pos2.getX(), pos2.getY(), size2.getX(), size2.getY(), Math.toRadians(rot2));
                }

                double colorWeightA = 1.5 / (0.5 + Math.exp((Math.log(0.25)) / maxSize * hit1.distance(hit2))) - 2;
                double colorWeightB = 1.5 / (0.5 + Math.exp((Math.log(0.25)) / minSize * hit1.distance(hit2))) - 2;

                if (colorWeightA > 0) {
                    graphicsContext.setStroke(Paint.valueOf(HexFormat.of().toHexDigits((int) (Math.min(colorWeightA * 256, 128) + 127)).substring(6) + "8080FF"));
                }
                if (colorWeightB < 0) {
                    graphicsContext.setStroke(Paint.valueOf("8080" + HexFormat.of().toHexDigits((int) (Math.min(-colorWeightB * 256, 128) + 127)).substring(6) + "FF"));
                }

                graphicsContext.strokeLine(hit1.getX() * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX(), hit1.getY() * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY(), hit2.getX() * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX(), hit2.getY() * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY());
                graphicsContext.restore();
            }
        }

         */
    }

}
