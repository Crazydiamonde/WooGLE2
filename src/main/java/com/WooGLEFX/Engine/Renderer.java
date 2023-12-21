package com.WooGLEFX.Engine;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.BallInstance;
import com.WorldOfGoo.Level.Pipe;
import com.WorldOfGoo.Level.Strand;
import com.WorldOfGoo.Level.Vertex;
import com.WorldOfGoo.Scene.Compositegeom;
import com.WorldOfGoo.Scene.Hinge;
import com.WorldOfGoo.Scene.Linearforcefield;
import com.WorldOfGoo.Scene.Scene;
import com.WorldOfGoo.Scene.Slider;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

public class Renderer {

    public static final Paint solidRed = Paint.valueOf("FF4000FF");
    public static final Paint solidBlue = Paint.valueOf("0040FFFF");
    public static final Paint transparentRed = Paint.valueOf("FF800040");
    public static final Paint transparentBlue = Paint.valueOf("0080FF40");
    public static final Paint selectionOutline = Paint.valueOf("000000FF");
    public static final Paint selectionOutline2 = Paint.valueOf("FFFFFFFF");
    public static final Paint mechanics = Paint.valueOf("FFFF00FF");
    public static final Paint pipeVertex = Paint.valueOf("FF5599FF");
    public static final Paint PIPE = Paint.valueOf("404040FF");
    public static final Paint PIPE_BEAUTY = Paint.valueOf("FFA6B7FF");
    public static final Paint PIPE_ISH = Paint.valueOf("5FFF5FFF");
    public static final Paint compositeGeom = Paint.valueOf("00FF00FF");
    public static final Paint poiNormal = Paint.valueOf("41C0C080");
    public static final Paint poiWidescreen = Paint.valueOf("21FE8080");
    public static final Paint endNormal = Paint.valueOf("004040FF");
    public static final Paint endWidescreen = Paint.valueOf("006030FF");
    public static final Paint noLevel = Paint.valueOf("A0A0A0FF");
    public static final Paint middleColor = Paint.valueOf("808080FF");
    public static final Paint particleLabels = Paint.valueOf("A81CFF");

    public static final Stop[] stops = new Stop[] { new Stop(0, javafx.scene.paint.Color.valueOf("802000FF")), new Stop(1, Color.valueOf("FFC040FF")) };

    public static double angleTo(Point2D p1, Point2D p2){
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        if (dy > 0) {
            return -Math.acos(dx / magnitude);
        } else {
            return Math.acos(dx / magnitude);
        }
    }

    public static void addAppropriately(EditorObject obj, ArrayList<EditorObject> list) {
        if (list.size() == 0){
            list.add(obj);
        } else {
            int where = 0;
            while (where < list.size() && list.get(where).getDouble("depth") <= obj.getDouble("depth")){
                where++;
            }
            list.add(where, obj);
        }
    }

    public static ArrayList<EditorObject> orderObjectsByDepth(WorldLevel level){
        ArrayList<EditorObject> sceneLayersByDepth = new ArrayList<>();

        ArrayList<EditorObject> lowDepth = new ArrayList<>();
        ArrayList<EditorObject> strands = new ArrayList<>();
        ArrayList<EditorObject> midLowDepth = new ArrayList<>();
        ArrayList<EditorObject> pipe = new ArrayList<>();
        ArrayList<EditorObject> midHighDepth = new ArrayList<>();
        ArrayList<EditorObject> balls = new ArrayList<>();
        ArrayList<EditorObject> highDepth = new ArrayList<>();
        ArrayList<EditorObject> compositeGeom = new ArrayList<>();
        ArrayList<EditorObject> geom = new ArrayList<>();
        ArrayList<EditorObject> geomConstraints = new ArrayList<>();
        ArrayList<EditorObject> scene = new ArrayList<>();

        for (EditorObject object : level.getScene()) {
            if (object.getAttribute("depth") != null && !(object instanceof Linearforcefield)) {
                double depth = object.getDouble("depth");
                if (depth < -0.001) {
                    addAppropriately(object, lowDepth);
                } else if (depth < 0) {
                    addAppropriately(object, midLowDepth);
                } else if (depth < 0.002) {
                    addAppropriately(object, midHighDepth);
                } else {
                    addAppropriately(object, highDepth);
                }
            } else if (object instanceof Scene) {
                scene.add(object);
            } else if (object instanceof Hinge || object instanceof Slider) {
                geomConstraints.add(object);
            } else if (object instanceof Compositegeom) {
                compositeGeom.add(object);
            } else {
                geom.add(object);
            }
        }

        for (EditorObject object : level.getLevel()) {
            if (object.getAttribute("depth") != null) {
                double depth = object.getDouble("depth");
                if (depth < -0.001) {
                    addAppropriately(object, lowDepth);
                } else if (depth < 0) {
                    addAppropriately(object, midLowDepth);
                } else if (depth < 0.002) {
                    addAppropriately(object, midHighDepth);
                } else {
                    addAppropriately(object, highDepth);
                }
            } else if (object instanceof BallInstance) {
                balls.add(object);
            } else if (object instanceof Strand) {
                strands.add(object);
            } else {
                geom.add(object);
            }
        }

        sceneLayersByDepth.addAll(lowDepth);
        sceneLayersByDepth.addAll(strands);
        sceneLayersByDepth.addAll(midLowDepth);
        sceneLayersByDepth.addAll(pipe);
        sceneLayersByDepth.addAll(midHighDepth);
        sceneLayersByDepth.addAll(balls);
        sceneLayersByDepth.addAll(highDepth);
        sceneLayersByDepth.addAll(geom);
        sceneLayersByDepth.addAll(compositeGeom);
        sceneLayersByDepth.addAll(geomConstraints);
        sceneLayersByDepth.addAll(scene);

        return sceneLayersByDepth;

    }

    public static ArrayList<EditorObject> orderObjectsBySelectionDepth(WorldLevel level){
        ArrayList<EditorObject> sceneLayersByDepth = new ArrayList<>();

        ArrayList<EditorObject> lowDepth = new ArrayList<>();
        ArrayList<EditorObject> midLowDepth = new ArrayList<>();
        ArrayList<EditorObject> pipe = new ArrayList<>();
        ArrayList<EditorObject> midHighDepth = new ArrayList<>();
        ArrayList<EditorObject> highDepth = new ArrayList<>();
        ArrayList<EditorObject> geomConstraints = new ArrayList<>();
        ArrayList<EditorObject> geom = new ArrayList<>();
        ArrayList<EditorObject> compositeGeom = new ArrayList<>();
        ArrayList<EditorObject> scene = new ArrayList<>();
        ArrayList<EditorObject> strands = new ArrayList<>();
        ArrayList<EditorObject> balls = new ArrayList<>();

        for (EditorObject object : level.getScene()) {
            if (object.getAttribute("depth") != null && !(object instanceof Linearforcefield)) {
                double depth = object.getDouble("depth");
                if (depth < -0.001) {
                    addAppropriately(object, lowDepth);
                } else if (depth < 0) {
                    addAppropriately(object, midLowDepth);
                } else if (depth < 0.002) {
                    addAppropriately(object, midHighDepth);
                } else {
                    addAppropriately(object, highDepth);
                }
            } else if (object instanceof Scene) {
                scene.add(object);
            } else if (object instanceof Hinge || object instanceof Slider) {
                geomConstraints.add(object);
            } else if (object instanceof Compositegeom) {
                compositeGeom.add(object);
            } else {
                geom.add(object);
            }
        }

        for (EditorObject object : level.getLevel()) {
            if (object.getAttribute("depth") != null) {
                double depth = object.getDouble("depth");
                if (depth < -0.001) {
                    addAppropriately(object, lowDepth);
                } else if (depth < 0) {
                    addAppropriately(object, midLowDepth);
                } else if (depth < 0.002) {
                    addAppropriately(object, midHighDepth);
                } else {
                    addAppropriately(object, highDepth);
                }
            } else if (object instanceof BallInstance) {
                balls.add(object);
            } else if (object instanceof Strand) {
                strands.add(object);
            } else {
                geom.add(object);
            }
        }

        sceneLayersByDepth.addAll(lowDepth);
        sceneLayersByDepth.addAll(midLowDepth);
        sceneLayersByDepth.addAll(pipe);
        sceneLayersByDepth.addAll(midHighDepth);
        sceneLayersByDepth.addAll(highDepth);
        sceneLayersByDepth.addAll(geom);
        sceneLayersByDepth.addAll(compositeGeom);
        sceneLayersByDepth.addAll(scene);
        sceneLayersByDepth.addAll(geomConstraints);
        sceneLayersByDepth.addAll(strands);
        sceneLayersByDepth.addAll(balls);

        return sceneLayersByDepth;

    }

    public static void drawEverything(WorldLevel level, Canvas canvas, Canvas imageCanvas) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        GraphicsContext imageGraphicsContext = imageCanvas.getGraphicsContext2D();

        canvas.resize(canvas.getWidth(), canvas.getHeight());
        canvas.autosize();
        imageCanvas.resize(canvas.getWidth(), canvas.getHeight());
        imageCanvas.autosize();

        //Define bounds of where content is displayed on the canvas
        int left = 0; //(int)(-Main.offsetX / Main.zoom);
        int top = 0; //(int)(-Main.offsetY / Main.zoom);
        int right = 1920 - 520; //left + (int)((1920 - 520) / Main.zoom);
        int bottom = 1080; //top + (int)((1080) / Main.zoom);

        BufferedImage toWriteOn = new BufferedImage(right - left, bottom - top, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = toWriteOn.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

        //Loop through and display every object in an arbitrary order
        graphicsContext.save();
        EditorObject previousVertex = null;
        Paint previousPipeColor = null;
        for (EditorObject object : level.getLevel()) {
            if (object instanceof Pipe && previousPipeColor == null) {
                String pipeColor = object.getAttribute("type");
                switch (pipeColor) {
                    case "BEAUTY":
                        previousPipeColor = PIPE_BEAUTY;
                        break;
                    case "ISH":
                        previousPipeColor = PIPE_ISH;
                        break;
                    default:
                        previousPipeColor = PIPE;
                        break;
                }
            }
            if (object instanceof Vertex && Main.getLevel().isShowGeometry()) {
                if (previousVertex != null) {
                    ((Vertex) object).drawTo(graphicsContext, (Vertex) previousVertex, previousPipeColor);
                }
                previousVertex = object;
            }
        }
        graphicsContext.restore();

        for (EditorObject object : orderObjectsByDepth(level)) {
            graphicsContext.save();
            imageGraphicsContext.save();
            if (object.getAttribute("image") != null && !object.getAttribute("image").equals("") && Main.getLevel().isShowGraphics()) {
                object.drawImage(graphicsContext, imageGraphicsContext);
            }
            object.draw(graphicsContext, imageGraphicsContext);
            graphicsContext.restore();
            imageGraphicsContext.restore();
        }
        //System.out.println(count + " <--");

        if (Main.getMode() == Main.STRAND) {
            graphicsContext.save();

            if (Main.getStrand1Gooball() != null) {

                double x = Main.getStrand1Gooball().getDouble("x") * Main.getLevel().getZoom() + Main.getLevel().getOffsetX();
                double y = -Main.getStrand1Gooball().getDouble("y") * Main.getLevel().getZoom() + Main.getLevel().getOffsetY();

                graphicsContext.setLineWidth(3 * Main.getLevel().getZoom());

                graphicsContext.strokeLine(x, y, Main.getMouseX(), Main.getMouseY());

            }

            graphicsContext.restore();
        }

    }

    public static void clear(WorldLevel level, Canvas canvas, Canvas imageCanvas) {
        canvas.getGraphicsContext2D().setFill(noLevel);
        imageCanvas.getGraphicsContext2D().setFill(noLevel);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        imageCanvas.getGraphicsContext2D().fillRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
    }


}
