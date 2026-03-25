package com.worldOfGoo2.util;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.assets.wog2.WOG2Ball.WOG2Ball;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.ball.Image;
import com.worldOfGoo2.ball.Part;
import com.worldOfGoo2.level._2_Level_BallInstance;
import com.worldOfGoo2.misc.ImageID;
import javafx.embed.swing.SwingFXUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BallInstanceHelper {

    public static final Map<Integer, String> vanillaTypeEnumToTypeMap = new HashMap<>();
    
    private static Map<Integer, String> typeEnumToTypeMap;
    private static Map<String, Integer> typeToTypeEnumMap;

    static {

        vanillaTypeEnumToTypeMap.put(1, "Common");
        vanillaTypeEnumToTypeMap.put(2, "CommonAlbino");
        vanillaTypeEnumToTypeMap.put(3, "Ivy");
        vanillaTypeEnumToTypeMap.put(4, "Balloon");
        vanillaTypeEnumToTypeMap.put(5, "GoolfSingle");
        vanillaTypeEnumToTypeMap.put(6, "Anchor");
        vanillaTypeEnumToTypeMap.put(7, "LauncherL2B");
        vanillaTypeEnumToTypeMap.put(8, "GooProduct");
        vanillaTypeEnumToTypeMap.put(9, "Thruster");
        vanillaTypeEnumToTypeMap.put(10, "Terrain");
        vanillaTypeEnumToTypeMap.put(11, "BalloonEye");
        vanillaTypeEnumToTypeMap.put(12, "Conduit");
        vanillaTypeEnumToTypeMap.put(13, "LauncherL2L");
        vanillaTypeEnumToTypeMap.put(14, "GooProductWhite");
        vanillaTypeEnumToTypeMap.put(15, "Grow");
        vanillaTypeEnumToTypeMap.put(16, "BombSticky");
        vanillaTypeEnumToTypeMap.put(17, "Rope");
        vanillaTypeEnumToTypeMap.put(18, "Bouncy");
        vanillaTypeEnumToTypeMap.put(19, "Fish");
        vanillaTypeEnumToTypeMap.put(20, "TimeBug");
        vanillaTypeEnumToTypeMap.put(23, "MatchStick");
        vanillaTypeEnumToTypeMap.put(25, "Fireworks");
        vanillaTypeEnumToTypeMap.put(26, "Lightball");
        vanillaTypeEnumToTypeMap.put(27, "TwuBit");
        vanillaTypeEnumToTypeMap.put(28, "TwuBitBit");
        vanillaTypeEnumToTypeMap.put(29, "Adapter");
        vanillaTypeEnumToTypeMap.put(30, "Winch");
        vanillaTypeEnumToTypeMap.put(32, "Shrink");
        vanillaTypeEnumToTypeMap.put(33, "Jelly");
        vanillaTypeEnumToTypeMap.put(34, "Goolf");
        vanillaTypeEnumToTypeMap.put(35, "ThisWayUp");
        vanillaTypeEnumToTypeMap.put(36, "LiquidLevelExit");
        vanillaTypeEnumToTypeMap.put(37, "Eye");
        vanillaTypeEnumToTypeMap.put(38, "UtilAttachWalkable");

        // CommonBlack LightBall CommonWorld

        // by default uses vanilla ball table
        // if GlobalResourceManager finds the FistyLoader
        // ballTable.ini, it will change it from there
        setTypeEnumMaps(vanillaTypeEnumToTypeMap);
    }

    
    public static AttributeAdapter ballTypeAttributeAdapter(EditorObject object, String displayName, String realName, EditorAttribute attribute) {
        return new AttributeAdapter(displayName) {
            private final EditorAttribute typeAttribute = attribute != null
                ? attribute
                : new EditorAttribute(displayName, InputField._2_BALL_TYPE, object);

            @Override
            public EditorAttribute getValue() {
                if (object.getAttribute2(realName).stringValue().isEmpty()) return typeAttribute;
                
                typeAttribute.setValue(BallInstanceHelper.typeEnumToTypeMap.getOrDefault(
                        object.getAttribute2(realName).intValue(), ""));
                
                return typeAttribute;
            }

            @Override
            public void setValue(String value) {
                Integer typeEnum = BallInstanceHelper.typeToTypeEnumMap.get(value);
                
                if (typeEnum != null) {
                    typeAttribute.setValue(value);
                } else {
                    try {
                        typeEnum = Integer.parseInt(value);
                        typeAttribute.setValue(value);
                    } catch (NumberFormatException ignored) {
                        typeEnum = 0;
                    }
                }

                object.setAttribute2(realName, typeEnum);
            }

        };
    }
    

    private static boolean part2CanBeUsed(_2_Level_BallInstance ballInstance, Part part) {

        String state = "0";

        if (!ballInstance.getAttribute("discovered").booleanValue()) {
            if (!part.getAttribute("isActiveWhenUndiscovered").booleanValue()) return false;
        } else if (ballInstance.hasStrands()) {
            state = "4";
        }

        List<EditorObject> states = part.getChildren("states");
        if (states.isEmpty()) return true;
        else for (EditorObject stateObject : states) {
            if (stateObject.getAttribute("ballState").stringValue().equals(state)) return true;
        }
        return false;

    }


    private static BufferedImage getPartImageWoG2(WOG2Ball ball, Part part, Random machine) {

        ArrayList<Image> images = new ArrayList<>();
        for (EditorObject editorObject : part.getChildren()) if (editorObject instanceof Image ball_image) images.add(ball_image);
        if (images.isEmpty()) return null;

        String imageString = images.get((int)(images.size() * machine.nextDouble())).getChildren().get(0).getAttribute("imageId").stringValue();

        return SwingFXUtils.fromFXImage(ResourceManager.getImage(ball.getResources(), imageString, GameVersion.VERSION_WOG2), null);

    }


    private static javafx.scene.image.Image getPartPupilImageWoG2(Part part, Random machine) {

        ArrayList<ImageID> pupilImages = new ArrayList<>();
        for (EditorObject editorObject : part.getChildren()) if (editorObject instanceof ImageID ball_image) pupilImages.add(ball_image);
        if (pupilImages.isEmpty()) return null;

        String pupilImageString = pupilImages.get((int)(pupilImages.size() * machine.nextDouble())).getAttribute("imageId").stringValue();

        return ResourceManager.getImage(null, pupilImageString, GameVersion.VERSION_WOG2);

    }


    public static javafx.scene.image.Image createBallImageWoG2(_2_Level_BallInstance ballInstance, WOG2Ball ball, double _scaleX, double _scaleY, Random machine) {

        ArrayList<Part> parts = new ArrayList<>();
        for (EditorObject child : ball.getObjects()) if (child instanceof Part part) parts.add(part);
        parts.sort((o1, o2) -> (int)Math.signum(
                o1.getAttribute("layer").doubleValue() - o2.getAttribute("layer").doubleValue()));
        if (parts.isEmpty()) return null;

        // Create image bounds
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        double bodyMinX = Double.POSITIVE_INFINITY;
        double bodyMinY = Double.POSITIVE_INFINITY;
        double bodyMaxX = Double.NEGATIVE_INFINITY;
        double bodyMaxY = Double.NEGATIVE_INFINITY;

        String bodyPartID = ball.getBall().getChildren("bodyPart")
                .get(0).getAttribute("partName").stringValue();

        record PartPosition(double x, double y, double scaleX, double scaleY, BufferedImage image) {

        }

        ArrayList<PartPosition> partPositions = new ArrayList<>();

        boolean thereWasABody = false;
        for (Part part : parts) {

            double sizeVariance = ball.getBall().getAttribute("sizeVariance").doubleValue();

            Random machine2 = new Random(1);
            machine2.nextDouble();
            double random = machine2.nextDouble();

            boolean relative = part.getAttribute("scaleIsRelative").booleanValue();

            double scaleX = part.getAttribute("scale").doubleValue() * (relative ? 1 : 1 / _scaleX) * (1 + (random * 2 - 1) * sizeVariance);
            double scaleY = part.getAttribute("scale").doubleValue() * (relative ? 1 : 1 / _scaleY) * (1 + (random * 2 - 1) * sizeVariance);

            double partMinX = part.getAttribute("minX").doubleValue() / _scaleX;
            double partMinY = -part.getAttribute("minY").doubleValue() / _scaleY;
            double partMaxX = part.getAttribute("maxX").doubleValue() / _scaleX;
            double partMaxY = -part.getAttribute("maxY").doubleValue() / _scaleY;

            double partX = partMinX + (partMaxX - partMinX) * machine.nextDouble();
            double partY = partMinY + (partMaxY - partMinY) * machine.nextDouble();

            BufferedImage partImage = getPartImageWoG2(ball, part, machine);
            if (partImage == null) continue;
            if ((ballInstance == null || part2CanBeUsed(ballInstance, part)))
                partPositions.add(new PartPosition(partX, partY, scaleX, scaleY, partImage));

            javafx.scene.image.Image pupilImage = getPartPupilImageWoG2(part, machine);
            if (pupilImage != null && (ballInstance == null || part2CanBeUsed(ballInstance, part)))
                partPositions.add(new PartPosition(partX, partY, scaleX, scaleY, SwingFXUtils.fromFXImage(pupilImage, null)));

            double partImageMinX = partX - partImage.getWidth() * scaleX / 2.0;
            double partImageMinY = partY - partImage.getHeight() * scaleY / 2.0;
            double partImageMaxX = partX + partImage.getWidth() * scaleX / 2.0;
            double partImageMaxY = partY + partImage.getHeight() * scaleY / 2.0;

            if (partImageMinX < minX) minX = partImageMinX;
            if (partImageMinY < minY) minY = partImageMinY;
            if (partImageMaxX > maxX) maxX = partImageMaxX;
            if (partImageMaxY > maxY) maxY = partImageMaxY;

            if (part.getAttribute("name").stringValue().equals(bodyPartID)) {
                thereWasABody = true;
                bodyMinX = partImageMinX;
                bodyMinY = partImageMinY;
                bodyMaxX = partImageMaxX;
                bodyMaxY = partImageMaxY;
            }

        }

        if (!thereWasABody) {
            bodyMinX = 0;
            bodyMinY = 0;
            bodyMaxX = 1;
            bodyMaxY = 1;
        }

        double paddingLeft = bodyMinX - minX;
        double paddingRight = maxX - bodyMaxX;
        paddingLeft = Math.max(paddingLeft, paddingRight);
        paddingRight = paddingLeft;
        minX = bodyMinX - paddingLeft;
        maxX = bodyMaxX + paddingRight;

        double paddingTop = bodyMinY - minY;
        double paddingBottom = maxY - bodyMaxY;
        paddingTop = Math.max(paddingTop, paddingBottom);
        paddingBottom = paddingTop;
        minY = bodyMinY - paddingTop;
        maxY = bodyMaxY + paddingBottom;

        if (maxX - minX <= -100000000) return null;

        BufferedImage image = new BufferedImage((int)(maxX - minX), (int)(maxY - minY), BufferedImage.TYPE_INT_ARGB);

        Graphics drawGraphics = image.createGraphics();

        for (PartPosition partPosition : partPositions) {

            BufferedImage partImage = partPosition.image;

            int imageX = (int)(partPosition.x - partImage.getWidth() * partPosition.scaleX / 2 - minX);
            int imageY = (int)(partPosition.y - partImage.getHeight() * partPosition.scaleY / 2 - minY);
            int imageWidth = (int)(partImage.getWidth() * partPosition.scaleX);
            int imageHeight = (int)(partImage.getHeight() * partPosition.scaleY);
            drawGraphics.drawImage(partImage, imageX, imageY, imageWidth, imageHeight, null);

        }

        drawGraphics.dispose();

        return SwingFXUtils.toFXImage(image, null);

    }


    public static ArrayList<ObjectComponent> generateBallObjectComponents(_2_Level_BallInstance ballInstance) {

        WOG2Ball ball = ballInstance.getBall();

        ArrayList<ObjectComponent> objectComponents = new ArrayList<>();

        if (ball != null) {

            ArrayList<Image> images = new ArrayList<>();
            for (EditorObject editorObject : ball.getObjects()) if (editorObject instanceof Part && editorObject.getAttribute("name").stringValue().equals(ball.getBall().getChildren("bodyPart").get(0).getAttribute("partName").stringValue())) for (EditorObject child : editorObject.getChildren())
                if (child instanceof Image ball_image) images.add(ball_image);

            double _scaleX = 1;
            double _scaleY = 1;
            if (!images.isEmpty()) {

                String imageString = images.get(0).getChildren().get(0).getAttribute("imageId").stringValue();

                javafx.scene.image.Image image = ResourceManager.getImage(ball.getResources(), imageString, GameVersion.VERSION_WOG2);
                if (image == null) return objectComponents;

                int _width = (int)image.getWidth();
                int _height = (int)image.getHeight();

                double width = ball.getBall().getAttribute("width").doubleValue();
                double height = ball.getBall().getAttribute("height").doubleValue();

                _scaleX = width / _width;
                _scaleY = height / _height;

            }

            javafx.scene.image.Image image = createBallImageWoG2(ballInstance, ball, _scaleX, _scaleY, new Random(ballInstance.getRandomSeed()));

            double final_scaleX = _scaleX;
            double final_scaleY = _scaleY;
            objectComponents.add(new ImageComponent(ballInstance) {
                @Override
                public double getX() {
                    return ballInstance.getPosition().getX();
                }
                @Override
                public void setX(double x) {
                    ballInstance.setPosition(x, ballInstance.getPosition().getY());
                }
                @Override
                public double getY() {
                    return -ballInstance.getPosition().getY();
                }
                @Override
                public void setY(double y) {
                    ballInstance.setPosition(ballInstance.getPosition().getX(), -y);
                }
                @Override
                public double getRotation() {
                    return -ballInstance.getAttribute("angle").doubleValue();
                }
                @Override
                public void setRotation(double rotation) {
                    ballInstance.setAttribute("angle", -rotation);
                }
                @Override
                public double getScaleX() {
                    return final_scaleX;
                }
                @Override
                public double getScaleY() {
                    return final_scaleY;
                }
                @Override
                public double getDepth() {
                    return 0.000001;
                }
                @Override
                public javafx.scene.image.Image getImage() {
                    return image;
                }
                @Override
                public boolean isVisible() {
                    return !ballInstance.getAttribute("type").stringValue().equals("Terrain") && AssetManager.getVisibility("goos") == 2;
                }
                @Override
                public boolean isResizable() {
                    return false;
                }
            });

        }
        boolean isCircle = ball == null || !ball.getBall().getChildren("shape").get(0).getAttribute("ballShape").stringValue().equals("1");

        if (isCircle) {

            if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) {

                objectComponents.add(new CircleComponent(ballInstance) {
                    @Override
                    public double getX() {
                        return ballInstance.getPosition().getX();
                    }
                    @Override
                    public void setX(double x) {
                        ballInstance.setPosition(x, ballInstance.getPosition().getY());
                    }
                    @Override
                    public double getY() {
                        return -ballInstance.getPosition().getY();
                    }
                    @Override
                    public void setY(double y) {
                        ballInstance.setPosition(ballInstance.getPosition().getX(), -y);
                    }
                    @Override
                    public double getRotation() {
                        return -ballInstance.getAttribute("angle").doubleValue();
                    }
                    @Override
                    public double getRadius() {
                        return 0.1;
                    }
                    @Override
                    public double getEdgeSize() {
                        return 100;
                    }
                    @Override
                    public boolean isEdgeOnly() {
                        return true;
                    }
                    @Override
                    public javafx.scene.paint.Paint getBorderColor() {
                        return new javafx.scene.paint.Color(0.0, 0.0, 0.0, 1.0);
                    }
                    @Override
                    public javafx.scene.paint.Paint getColor() {
                        return new javafx.scene.paint.Color(0.0, 0.0, 0.0, 0.0);
                    }
                    @Override
                    public double getDepth() {
                        return 1000000;
                    }
                    @Override
                    public boolean isVisible() {
                        if (AssetManager.getVisibility("goos") == 0) return false;
                        return true;//(ball == null || ballInstance.getAttribute("type").stringValue().equals("Terrain") && ballInstance.visibilityFunction()) || AssetManager.getAsset().getVisibilitySettings().getShowGoos() == 1 || (ballInstance.getAttribute("type").stringValue().equals("Terrain") && FXEditorButtons_ShowHide.comboBoxSelected == ballInstance.getAttribute("terrainGroup").intValue());
                    }
                    @Override
                    public boolean isRotatable() {
                        return false;
                    }
                    @Override
                    public boolean isResizable() {
                        return false;
                    }
                });

            }
            objectComponents.add(new CircleComponent(ballInstance) {
                @Override
                public double getX() {
                    return ballInstance.getPosition().getX();
                }
                @Override
                public void setX(double x) {
                    ballInstance.setPosition(x, ballInstance.getPosition().getY());
                }
                @Override
                public double getY() {
                    return -ballInstance.getPosition().getY();
                }
                @Override
                public void setY(double y) {
                    ballInstance.setPosition(ballInstance.getPosition().getX(), -y);
                }
                @Override
                public double getRotation() {
                    return -ballInstance.getAttribute("angle").doubleValue();
                }
                @Override
                public void setRotation(double rotation) {
                    ballInstance.setAttribute("angle", -rotation);
                }
                @Override
                public double getRadius() {
                    if (ball == null) return 0.2;
                    else if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) return 0.1;
                    return ball.getWidth() / 2;
                }
                @Override
                public double getEdgeSize() {
                    if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) return 0.025;
                    else return 0.05;
                }
                @Override
                public boolean isEdgeOnly() {
                    return false;
                }
                @Override
                public javafx.scene.paint.Paint getBorderColor() {
                    if (ball == null) {
                        return new javafx.scene.paint.Color(0.5, 0.25, 0.25, 1.0);
                    } else {
                        return new javafx.scene.paint.Color(0.5, 0.25, 0.25, 1.0);
                        /*
                        if (ballInstance.getAttribute("type").stringValue().equals("Terrain") && FXEditorButtons_ShowHide.comboBoxSelected == ballInstance.getAttribute("terrainGroup").intValue() && FXEditorButtons_ShowHide.comboBoxSelected != -1) {
                            if (FXEditorButtons_ShowHide.comboBoxList.get(FXEditorButtons_ShowHide.comboBoxSelected)) {
                                return new javafx.scene.paint.Color(1.0 ,0.0, 1.0, 1);
                            } else {
                                return new javafx.scene.paint.Color(0.0 ,1.0, 1.0, 1);
                            }
                        } else {
                            if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) {
                                return new javafx.scene.paint.Color(1.0, 1.0, 1.0, 1);
                            } else {
                                return new javafx.scene.paint.Color(0.5, 0.5, 0.5, 1);
                            }
                        }

                         */
                    }
                }
                @Override
                public javafx.scene.paint.Paint getColor() {
                    if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) {
                        return new javafx.scene.paint.Color(1.0, 1.0, 1.0, 1);
                    } else {
                        return new javafx.scene.paint.Color(0, 0, 0, 0);
                    }
                }
                @Override
                public double getDepth() {
                    if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) return 1000000;
                    return 0.000001;
                }
                @Override
                public boolean isVisible() {
                    if (AssetManager.getVisibility("goos") == 0) return false;
                    return (ball == null || ballInstance.getAttribute("type").stringValue().equals("Terrain") && ballInstance.visibilityFunction()) || AssetManager.getVisibility("goos") == 1 || (ballInstance.getAttribute("type").stringValue().equals("Terrain"));// && FXEditorButtons_ShowHide.comboBoxSelected == ballInstance.getAttribute("terrainGroup").intValue());
                }
                @Override
                public boolean isResizable() {
                    return false;
                }

                @Override
                public boolean isRotatable() {
                    return !ballInstance.getAttribute("type").stringValue().equals("Terrain");
                }
            });

        }

        else objectComponents.add(new RectangleComponent(ballInstance) {
            @Override
            public double getX() {
                return ballInstance.getPosition().getX();
            }
            
            @Override
            public void setX(double x) {
                ballInstance.setPosition(x, ballInstance.getPosition().getY());
            }
            
            @Override
            public double getY() {
                return -ballInstance.getPosition().getY();
            }
            
            @Override
            public void setY(double y) {
                ballInstance.setPosition(ballInstance.getPosition().getX(), -y);
            }

            @Override
            public double getRotation() {
                return -ballInstance.getAttribute("angle").doubleValue();
            }

            @Override
            public void setRotation(double rotation) {
                ballInstance.setAttribute("angle", -rotation);
            }

            @Override
            public double getWidth() {
                return ball.getWidth();
            }

            @Override
            public double getHeight() {
                return ball.getHeight();
            }

            @Override
            public double getEdgeSize() {
                return 0.05;
            }

            @Override
            public boolean isEdgeOnly() {
                return !ballInstance.getAttribute("type").stringValue().equals("Terrain");
            }

            @Override
            public javafx.scene.paint.Paint getBorderColor() {
                if (ball == null) {
                    return new javafx.scene.paint.Color(0.5, 0.25, 0.25, 1.0);
                } else {
                    return new javafx.scene.paint.Color(0.5, 0.5, 0.5, 1);
                }
            }

            @Override
            public javafx.scene.paint.Paint getColor() {
                if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) {
                    return new javafx.scene.paint.Color(0.5, 0.5, 0.5, 1.0);
                } else {
                    return new javafx.scene.paint.Color(0, 0, 0, 0);
                }
            }

            @Override
            public double getDepth() {
                return 0.000001;
            }

            @Override
            public boolean isVisible() {
                return (ball == null || ballInstance.getAttribute("type").stringValue().equals("Terrain") && ballInstance.visibilityFunction()) || AssetManager.getVisibility("goos") == 1;
            }

            @Override
            public boolean isResizable() {
                return false;
            }
        });

        return objectComponents;

    }

    public static void setTypeEnumMaps(Map<Integer, String> typeEnumToTypeMap) {
        BallInstanceHelper.typeEnumToTypeMap = typeEnumToTypeMap;
        
        HashMap<String, Integer> typeToTypeEnumMap = new HashMap<>();
        for (int key : typeEnumToTypeMap.keySet()) {
            typeToTypeEnumMap.put(typeEnumToTypeMap.get(key), key);
        }
        
        BallInstanceHelper.typeToTypeEnumMap = typeToTypeEnumMap;
    }
    
    public static Map<Integer, String> getTypeEnumToTypeMap() {
        return typeEnumToTypeMap;
    }
    
    public static Map<String, Integer> getTypeToTypeEnumMap() {
        return typeToTypeEnumMap;
    }
}
