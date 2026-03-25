package com.woogleFX.engine.fx.editorButtons;

import com.woogleFX.assets.GameVersion;
import com.woogleFX.assets.wog1.ball.WOG1Ball;
import com.woogleFX.assets.wog1.level.WOG1Level;
import com.woogleFX.assets.wog2.WOG2Ball.WOG2Ball;
import com.woogleFX.assets.wog2.WOG2Level.WOG2Level;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectAdder;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.engine.fx.PaletteManager;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.worldOfGoo.ball.part;
import com.worldOfGoo.level.BallInstance;
import com.worldOfGoo2.ball.Part;
import com.worldOfGoo2.level._2_Level_BallInstance;
import com.worldOfGoo2.util.BallInstanceHelper;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class FXBallPaletteManager {

    private static final Map<GameVersion, ArrayList<Button>> palettes = new HashMap<>();

    public static List<Button> getPaletteForVersion(GameVersion version) {
        if (!palettes.containsKey(version)) palettes.put(version, new ArrayList<>());
        return palettes.get(version);
    }

    public static void regeneratePalettes() {

        // Initialize palettes as empty
        for (GameVersion paletteVersion : PaletteManager.getPaletteVersions()) {
            palettes.put(paletteVersion, new ArrayList<>());
        }

        int size = 18;

        int i = 0;
        for (String paletteBall : PaletteManager.getPaletteBalls()) {

            GameVersion version = PaletteManager.getPaletteVersions().get(i);

            Button button;
            if (version == GameVersion.VERSION_WOG1_OLD || version == GameVersion.VERSION_WOG1_NEW) {

                WOG1Ball ball = WOG1Ball.assetSelector.openInstance(paletteBall, version);
                if (ball == null) continue;

                button = createTemplateForBall(size, ball);
                button.setTooltip(new FXEditorButtons.DelayedTooltip("Add " + ball.getBall().getAttribute("name").stringValue()));

            } else {

                WOG2Ball ball2 = WOG2Ball.assetSelector.openInstance(paletteBall, version);
                if (ball2 == null) continue;

                button = createTemplateFor2Ball(size, ball2);
                button.setTooltip(new FXEditorButtons.DelayedTooltip("Add " + ball2.getBall().getAttribute("name").stringValue()));

            }
            palettes.get(version).add(button);
            i++;
        }


    }

    public static Button createTemplateForBall(int size, WOG1Ball ball) {

        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;

        for (com.woogleFX.editorObjects.EditorObject EditorObject : ball.getObjects()) {
            String state = "standing";

            if (EditorObject instanceof part part) {

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
                if (!ok) continue;

                double lowX;
                double highX;
                double lowY;
                double highY;

                String x = part.getAttribute("x").stringValue();
                String y = part.getAttribute("y").stringValue();

                double scale = Double.parseDouble(part.getAttribute("scale").stringValue());

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

                double myX = 0.5 * (highX - lowX) + lowX;
                double myY = 0.5 * (highY - lowY) + lowY;

                String[] imageStrings = part.getAttribute("image").listValue();

                if (imageStrings.length == 0) continue;

                String imageString = imageStrings[0];
                javafx.scene.image.Image img;
                img = ResourceManager.getImage(ball.getResources(), imageString, ball.getVersion());
                if (img == null) continue;

                BufferedImage image = SwingFXUtils.fromFXImage(img, null);

                double iWidth = image.getWidth() * scale;
                double iHeight = image.getHeight() * scale;

                if (myX - iWidth / 2 < minX) minX = myX - iWidth / 2;
                if (-myY - iHeight / 2 < minY) minY = -myY - iHeight / 2;
                if (myX + iWidth / 2 > maxX) maxX = myX + iWidth / 2;
                if (-myY + iHeight / 2 > maxY) maxY = -myY + iHeight / 2;

            }
        }

        double width = maxX - minX;
        double height = maxY - minY;

        Button idk = new Button();
        if (width < 0 || height < 0) return idk;

        BufferedImage toWriteOn = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
        Graphics writeGraphics = toWriteOn.getGraphics();

        for (EditorObject EditorObject : ball.getObjects()) {

            String state = "standing";

            if (EditorObject instanceof part part) {

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
                if (!ok) continue;

                String[] imageStrings = part.getAttribute("image").listValue();

                if (imageStrings.length == 0) continue;
                String imageString = imageStrings[0];
                javafx.scene.image.Image img;
                img = ResourceManager.getImage(ball.getResources(), imageString, ball.getVersion());
                if (img == null) continue;

                double scale = part.getAttribute("scale").doubleValue();

                double lowX;
                double highX;
                double lowY;
                double highY;

                String x = part.getAttribute("x").stringValue();
                String y = part.getAttribute("y").stringValue();

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

                double myX = 0.5 * (highX - lowX) + lowX;
                double myY = 0.5 * (highY - lowY) + lowY;

                if (myY == 0) {
                    myY = -0;
                }

                double screenX = myX + toWriteOn.getWidth() / 2.0 - img.getWidth() * scale / 2;
                double screenY = -myY + toWriteOn.getHeight() / 2.0 - img.getHeight() * scale / 2;

                writeGraphics.drawImage(SwingFXUtils.fromFXImage(img, null), (int) screenX, (int) screenY,
                        (int) (img.getWidth() * scale), (int) (img.getHeight() * scale), null);

                String[] pupilImageStrings = part.getAttribute("pupil").listValue();
                if (pupilImageStrings.length == 0) continue;

                String pupilImageString = pupilImageStrings[0];
                javafx.scene.image.Image pupilImage;
                pupilImage = ResourceManager.getImage(ball.getResources(), pupilImageString, ball.getVersion());
                if (pupilImage == null) continue;

                double screenX2 = myX + toWriteOn.getWidth() / 2.0 - pupilImage.getWidth() * scale / 2;
                double screenY2 = -myY + toWriteOn.getHeight() / 2.0
                        - pupilImage.getHeight() * scale / 2;

                writeGraphics.drawImage(SwingFXUtils.fromFXImage(pupilImage, null), (int) screenX2,
                        (int) screenY2, (int) (pupilImage.getWidth() * scale),
                        (int) (pupilImage.getHeight() * scale), null);

            }

            double scaleFactor = (double) size / Math.max(toWriteOn.getWidth(), toWriteOn.getHeight());

            java.awt.Image tmp = toWriteOn.getScaledInstance((int) (toWriteOn.getWidth() * scaleFactor),
                    (int) (toWriteOn.getHeight() * scaleFactor), java.awt.Image.SCALE_SMOOTH);
            BufferedImage dimg = new BufferedImage((int) (toWriteOn.getWidth() * scaleFactor),
                    (int) (toWriteOn.getHeight() * scaleFactor), BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();

            idk.setGraphic(new ImageView(SwingFXUtils.toFXImage(dimg, null)));
        }

        idk.setPrefSize(size, size);
        idk.setOnAction(e -> {

            String name = ball.getBall().getAttribute("name").stringValue();

            // TODO: ???
            EditorObject ballInstance = ObjectCreator.create(BallInstance.class, ((WOG1Level) AssetManager.getAsset()).getLevel(), ball.getVersion());
            ballInstance.setAttribute("type", name);

            ObjectAdder.addAnything(ballInstance);

            ObjectAdder.objectToCameraCenter(ballInstance);

            AssetManager.getAsset().setSelectedDiscreetly(new EditorObject[]{ ballInstance });
            AssetManager.getAsset().setSelectedObjects(new EditorObject[]{ ballInstance });

        });
        return idk;
    }


    public static Button createTemplateFor2Ball(int size, WOG2Ball ball) {

        ArrayList<com.worldOfGoo2.ball.Image> images = new ArrayList<>();
        for (EditorObject editorObject : ball.getObjects()) if (editorObject instanceof Part && editorObject.getAttribute("name").stringValue().equals(ball.getBall().getChildren("bodyPart").get(0).getAttribute("partName").stringValue())) for (EditorObject child : editorObject.getChildren())
            if (child instanceof com.worldOfGoo2.ball.Image ball_image) images.add(ball_image);

        double _scaleX = 1;
        double _scaleY = 1;
        Button idk = new Button();
        if (!images.isEmpty()) {

            String imageString = images.get(0).getChildren().get(0).getAttribute("imageId").stringValue();

            javafx.scene.image.Image image = ResourceManager.getImage(ball.getResources(), imageString, GameVersion.VERSION_WOG2);
            if (image == null) return idk;

            int _width = (int)image.getWidth();
            int _height = (int)image.getHeight();

            double width = ball.getBall().getAttribute("width").doubleValue();
            double height = ball.getBall().getAttribute("height").doubleValue();

            _scaleX = width / _width;
            _scaleY = height / _height;

        }

        // TODO2: this
        javafx.scene.image.Image image = BallInstanceHelper.createBallImageWoG2(null, ball, _scaleX, _scaleY, new Random(0));

        if (image == null) return idk;
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(20);
        idk.setGraphic(imageView);

        idk.setPrefSize(size, size);
        idk.setOnAction(e -> {

            String name = ball.getBall().getAttribute("name").stringValue();

            _2_Level_BallInstance ballInstance = ObjectCreator.create(
                    _2_Level_BallInstance.class, ((WOG2Level) AssetManager.getAsset()).getLevel(), "balls", ball.getVersion());

            ballInstance.createPosition();
            ballInstance.setAttribute("type", name);
            ballInstance.onLoaded(AssetManager.getAsset());

            AssetManager.getAsset().getObjects().add(ballInstance);

            ObjectAdder.addAnything(ballInstance);

        });
        return idk;

    }

}
