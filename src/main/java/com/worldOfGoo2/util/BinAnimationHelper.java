package com.worldOfGoo2.util;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.gameData.animation.AnimBinReader;
import com.woogleFX.gameData.animation.SimpleBinAnimation;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.level._2_Level_BallInstance;
import javafx.scene.image.Image;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BinAnimationHelper {

    public static abstract class BinAnimationInterface {

        public abstract double getX();
        public abstract void setX(double x);

        public abstract double getY();
        public abstract void setY(double y);

        public abstract double getScaleX();
        public abstract void setScaleX(double scaleX);

        public abstract double getScaleY();
        public abstract void setScaleY(double scaleY);

        public abstract double getRotation();
        public abstract void setRotation(double rotation);

        public abstract double getDepth();
        public Depth.Layer getGlobalLayer() {
            return Depth.Layer.DEFAULT;
        }

        public boolean isSelectable() {
            return true;
        }

    }

    private static final Map<Integer, String> hardcodedImageIdMap = new HashMap<>();
    static {
        hardcodedImageIdMap.put(811319554, "IMAGE_GLOBAL_PUPIL");
        hardcodedImageIdMap.put(543867139, "IMAGE_TENTACLE_LIGHT");
    }

    public static void addBinAnimationAsObjectPositions(EditorObject editorObject, SimpleBinAnimation binAnimation, String state, BinAnimationInterface binAnimationInterface) {

        ArrayList<ImageComponent> objectComponents = new ArrayList<>();

        int i1 = 0;
        for (SimpleBinAnimation.SimpleBinAnimationState simpleBinAnimationState : binAnimation.states) {
            StringBuilder stringBuilder = new StringBuilder();
            int byteIndex = binAnimation.stringDefinitions[binAnimation.stateAliasStringTableIndices[i1]].stringTableIndex;
            while (binAnimation.stringTable[byteIndex] != 0x00) {
                stringBuilder.append((char)binAnimation.stringTable[byteIndex]);
                byteIndex++;
            }
            if (stringBuilder.toString().equals(state) || state.equals("")) {
                SimpleBinAnimation.SimpleBinAnimationGroup group = binAnimation.groups[simpleBinAnimationState.groupOffset];
                addBinAnimationGroupAsObjectPositions(objectComponents, editorObject, binAnimation, group, binAnimationInterface);
                break;
            }
            i1++;
        }

        System.out.println(binAnimation.name + ", " + objectComponents.size());

        if (!objectComponents.isEmpty()) {

            ImageComponent objectComponent = objectComponents.get(objectComponents.size() - 1);

            objectComponents.set(objectComponents.size() - 1, new ImageComponent(editorObject) {
                public Image getImage() {
                    return objectComponent.getImage();
                }
                public double getX() {
                    return objectComponent.getX();
                }
                public double getY() {
                    return objectComponent.getY();
                }
                public double getScaleX() {
                    return objectComponent.getScaleX();
                }
                public void setScaleX(double scaleX) {
                    objectComponent.setScaleX(scaleX);
                }
                public double getScaleY() {
                    return objectComponent.getScaleY();
                }
                public void setScaleY(double scaleY) {
                    objectComponent.setScaleY(scaleY);
                }
                public void setX(double x) {
                    objectComponent.setX(x);
                }
                public void setY(double y) {
                    objectComponent.setY(y);
                }
                public double getRotation() {
                    return objectComponent.getRotation();
                }
                public void setRotation(double rotation) {
                    objectComponent.setRotation(rotation);
                }
                public double getDepth() {
                    return objectComponent.getDepth();
                }
                public Depth.Layer getGlobalLayer() {
                    return objectComponent.getGlobalLayer();
                }
                public boolean isResizable() {
                    return objectComponent.isResizable();
                }
                public boolean isVisible() {
                    return objectComponent.isVisible();
                }
            });
        }

        for (int i = objectComponents.size() - 1; i >= 0; i--) {
            editorObject.addObjectComponent(objectComponents.get(i));
        }

    }


    public static void addBinAnimationGroupAsObjectPositions(ArrayList<ImageComponent> objectComponents, EditorObject editorObject, SimpleBinAnimation binAnimation, SimpleBinAnimation.SimpleBinAnimationGroup animationGroup, BinAnimationInterface binAnimationInterface) {

        for (int i = 0; i < animationGroup.sectionLength; i++) {
            SimpleBinAnimation.SimpleBinAnimationSection section = binAnimation.sections[i + animationGroup.sectionOffset];
            for (int j = 0; j < section.elementLength; j++) {
                SimpleBinAnimation.SimpleBinAnimationElement element = binAnimation.elements[j + section.elementOffset];
                if (element.frame != 0) continue;
                switch (element.type) {
                    case 1 -> {
                        SimpleBinAnimation.SimpleBinAnimationKeyframe keyframe = binAnimation.keyframes[element.offset];
                        SimpleBinAnimation.SimpleBinAnimationGroup group = binAnimation.groups[keyframe.groupOffset];
                        addBinAnimationGroupAsObjectPositions(objectComponents, editorObject, binAnimation, group, new BinAnimationInterface() {
                            public double getX() {
                                double dx = -keyframe.centerX;
                                double dy = -keyframe.centerY;
                                double angle = keyframe.angleBottomRight;
                                double addX = keyframe.offsetX + dx * Math.cos(angle) + dy * -Math.sin(angle);
                                double addY = keyframe.offsetY + dx * Math.sin(angle) + dy * Math.cos(angle);
                                return binAnimationInterface.getX()
                                        + addX * binAnimationInterface.getScaleX()
                                        * Math.cos(binAnimationInterface.getRotation())
                                        + addY * binAnimationInterface.getScaleY()
                                        * -Math.sin(binAnimationInterface.getRotation());
                            }
                            public void setX(double x) {
                                double dx = -keyframe.centerX;
                                double dy = -keyframe.centerY;
                                double angle = keyframe.angleBottomRight;
                                double addX = keyframe.offsetX + dx * Math.cos(angle) + dy * -Math.sin(angle);
                                double addY = keyframe.offsetY + dx * Math.sin(angle) + dy * Math.cos(angle);
                                binAnimationInterface.setX(x
                                        - addX * binAnimationInterface.getScaleX()
                                        * Math.cos(binAnimationInterface.getRotation())
                                        - addY * binAnimationInterface.getScaleY()
                                        * -Math.sin(binAnimationInterface.getRotation()));
                            }
                            public double getY() {
                                double dx = -keyframe.centerX;
                                double dy = -keyframe.centerY;
                                double angle = keyframe.angleBottomRight;
                                double addX = keyframe.offsetX + dx * Math.cos(angle) + dy * -Math.sin(angle);
                                double addY = keyframe.offsetY + dx * Math.sin(angle) + dy * Math.cos(angle);
                                return binAnimationInterface.getY()
                                        + addX * binAnimationInterface.getScaleX()
                                        * Math.sin(binAnimationInterface.getRotation())
                                        + addY * binAnimationInterface.getScaleY()
                                        * Math.cos(binAnimationInterface.getRotation());
                            }
                            public void setY(double y) {
                                double dx = -keyframe.centerX;
                                double dy = -keyframe.centerY;
                                double angle = keyframe.angleBottomRight;
                                double addX = keyframe.offsetX + dx * Math.cos(angle) + dy * -Math.sin(angle);
                                double addY = keyframe.offsetY + dx * Math.sin(angle) + dy * Math.cos(angle);
                                binAnimationInterface.setY(y
                                        - addX * binAnimationInterface.getScaleX()
                                        * Math.sin(binAnimationInterface.getRotation())
                                        - addY * binAnimationInterface.getScaleY()
                                        * Math.cos(binAnimationInterface.getRotation()));
                            }
                            public double getScaleX() {
                                return binAnimationInterface.getScaleX() * keyframe.scaleX;
                            }
                            public void setScaleX(double scaleX) {
                                binAnimationInterface.setScaleX(scaleX / keyframe.scaleX);
                            }
                            public double getScaleY() {
                                return binAnimationInterface.getScaleY() * keyframe.scaleY;
                            }
                            public void setScaleY(double scaleY) {
                                binAnimationInterface.setScaleY(scaleY / keyframe.scaleY);
                            }
                            public double getRotation() {
                                return binAnimationInterface.getRotation() + keyframe.angleBottomRight;
                            }
                            public void setRotation(double rotation) {
                                binAnimationInterface.setRotation(rotation - keyframe.angleBottomRight);
                            }
                            public double getDepth() {
                                return binAnimationInterface.getDepth();
                            }
                            public boolean isSelectable() {
                                return binAnimationInterface.isSelectable();
                            }
                        });
                    }
                    case 2 -> {
                        SimpleBinAnimation.SimpleBinAnimationPart part = binAnimation.parts[element.offset];
                        StringBuilder stringBuilder = new StringBuilder();
                        int byteIndex = binAnimation.stringDefinitions[binAnimation.imageStringTableIndices[part.imageIndex]].stringTableIndex;
                        while (binAnimation.stringTable[byteIndex] != 0x00) {
                            stringBuilder.append((char)binAnimation.stringTable[byteIndex]);
                            byteIndex++;
                        }
                        Image image = ResourceManager.getImage((editorObject instanceof _2_Level_BallInstance ballInstance ? ballInstance.getBall().getResources() : null), stringBuilder.toString(), GameVersion.VERSION_WOG2);
                        // System.out.println(stringBuilder.toString() + ", " + image);
                        if (image != null) objectComponents.add(new ImageComponent(editorObject) {
                            public Image getImage() {
                                return image;
                            }
                            public double getX() {
                                double dx = -part.centerX * part.scaleX;
                                double dy = -part.centerY * part.scaleY;
                                double angle = part.angleBottomRight;
                                double addX = part.offsetX - dx + dx * Math.cos(angle) + dy * -Math.sin(angle);
                                double addY = part.offsetY - dy + dx * Math.sin(angle) + dy * Math.cos(angle);
                                return binAnimationInterface.getX()
                                        + addX * binAnimationInterface.getScaleX()
                                        * Math.cos(binAnimationInterface.getRotation())
                                        + addY * binAnimationInterface.getScaleY()
                                        * -Math.sin(binAnimationInterface.getRotation());
                            }
                            public void setX(double x) {
                                double dx = -part.centerX * part.scaleX;
                                double dy = -part.centerY * part.scaleY;
                                double angle = part.angleBottomRight;
                                double addX = part.offsetX - dx + dx * Math.cos(angle) + dy * -Math.sin(angle);
                                double addY = part.offsetY - dy + dx * Math.sin(angle) + dy * Math.cos(angle);
                                binAnimationInterface.setX(x
                                        - addX * binAnimationInterface.getScaleX()
                                        * Math.cos(binAnimationInterface.getRotation())
                                        - addY * binAnimationInterface.getScaleY()
                                        * -Math.sin(binAnimationInterface.getRotation())
                                );
                            }
                            public double getY() {
                                double dx = -part.centerX * part.scaleX;
                                double dy = -part.centerY * part.scaleY;
                                double angle = part.angleBottomRight;
                                double addX = part.offsetX - dx + dx * Math.cos(angle) + dy * -Math.sin(angle);
                                double addY = part.offsetY - dy + dx * Math.sin(angle) + dy * Math.cos(angle);
                                return binAnimationInterface.getY()
                                        + addX * binAnimationInterface.getScaleX()
                                        * Math.sin(binAnimationInterface.getRotation())
                                        + addY * binAnimationInterface.getScaleY()
                                        * Math.cos(binAnimationInterface.getRotation());
                            }
                            public void setY(double y) {
                                double dx = -part.centerX * part.scaleX;
                                double dy = -part.centerY * part.scaleY;
                                double angle = part.angleBottomRight;
                                double addX = part.offsetX - dx + dx * Math.cos(angle) + dy * -Math.sin(angle);
                                double addY = part.offsetY - dy + dx * Math.sin(angle) + dy * Math.cos(angle);
                                binAnimationInterface.setY(y
                                        - addX * binAnimationInterface.getScaleX()
                                        * Math.sin(binAnimationInterface.getRotation())
                                        - addY * binAnimationInterface.getScaleY()
                                        * Math.cos(binAnimationInterface.getRotation())
                                );
                            }
                            public double getScaleX() {
                                return binAnimationInterface.getScaleX() * part.scaleX;
                            }
                            public void setScaleX(double scaleX) {
                                binAnimationInterface.setScaleX(scaleX / part.scaleX);
                            }
                            public double getScaleY() {
                                return binAnimationInterface.getScaleY() * part.scaleY;
                            }
                            public void setScaleY(double scaleY) {
                                binAnimationInterface.setScaleY(scaleY / part.scaleY);
                            }
                            public double getRotation() {
                                return binAnimationInterface.getRotation() + part.angleBottomRight;
                            }
                            public void setRotation(double rotation) {
                                binAnimationInterface.setRotation(rotation - part.angleBottomRight);
                            }
                            public double getDepth() {
                                return binAnimationInterface.getDepth();
                            }
                            public Depth.Layer getGlobalLayer() {
                                return binAnimationInterface.getGlobalLayer();
                            }
                            public boolean isSelectable() {
                                return binAnimationInterface.isSelectable();
                            }

                        });

                    }
                    case 3 -> {
                        SimpleBinAnimation.SimpleBinAnimationExternal external = binAnimation.externals[element.offset];

                        String imageString = hardcodedImageIdMap.get(external.globalIdHash);
                        if (imageString != null) {

                            Image image = ResourceManager.getImage(null, imageString, GameVersion.VERSION_WOG2);
                            if (image != null) objectComponents.add(new ImageComponent(editorObject) {
                                public Image getImage() {
                                    return image;
                                }
                                public double getX() {
                                    return binAnimationInterface.getX();
                                }
                                public double getY() {
                                    return binAnimationInterface.getY();
                                }
                                public double getScaleX() {
                                    return binAnimationInterface.getScaleX() / 2.0;
                                }
                                public double getScaleY() {
                                    return binAnimationInterface.getScaleY() / 2.0;
                                }
                                public double getDepth() {
                                    return binAnimationInterface.getDepth();
                                }
                                public Depth.Layer getGlobalLayer() {
                                    return binAnimationInterface.getGlobalLayer();
                                }
                            });

                        }

                        if (external.property12Offset != -1 && binAnimation.property12s.length != 0) {

                            SimpleBinAnimation.SimpleBinAnimationProperty12 property12 = binAnimation.property12s[external.property12Offset];
                            if (property12.type == 1) {

                                StringBuilder stringBuilder = new StringBuilder();
                                int byteIndex = binAnimation.stringDefinitions[property12.stringTableIndex].stringTableIndex;
                                while (binAnimation.stringTable[byteIndex] != 0x00) {
                                    stringBuilder.append((char) binAnimation.stringTable[byteIndex]);
                                    byteIndex++;
                                }

                                String animString = stringBuilder.toString().replace(".xml", ".bin");
                                if (!animString.isEmpty()) {
                                    SimpleBinAnimation binAnimation1 = AnimBinReader.readSimpleBinAnimation(Path.of(FileManager.getGameDir(GameVersion.VERSION_WOG2) + "/" + animString), "idk");
                                    addBinAnimationAsObjectPositions(editorObject, binAnimation1, "", binAnimationInterface);
                                }

                            }

                        }

                        for (int i1 = 0; i1 < external.property9Length; i1++) {
                            SimpleBinAnimation.SimpleBinAnimationProperty9 property9 = binAnimation.property9s[i1 + external.property9Offset];
                        }

                    }
                }
            }
        }

    }

}
