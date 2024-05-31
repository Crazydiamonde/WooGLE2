package com.woogleFX.gameData.font;

import com.woogleFX.file.*;
import com.woogleFX.gameData.level.GameVersion;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FontReader {

    private static final Logger logger = LoggerFactory.getLogger(FontReader.class);


    public static _Font read(String fontPath, GameVersion version) {

        String dir = FileManager.getGameDir(version);

        String text;
        try {
            text = Files.readString(Path.of(dir + "\\" + fontPath + ".txt"));
        } catch (IOException e) {
            return null;
        }

        _Font font = new _Font(fontPath, version);


        Map<String, FontData> variables = new HashMap<>();

        // Parse all commands from the text.
        ArrayList<FontCommand> commands = FontDataParser.parse(text);

        for (FontCommand command : commands) switch (command.id()) {

            case "Define" -> {
                // Define a new variable.
                FontData _name = command.args()[0];
                FontData _value = command.args()[1];

                String name = ((FontKeyword)_name).getKeyword();
                variables.put(name, _value);

            }

            case "CreateLayer" -> {
                // Create a new layer.
                FontData _layerID = command.args()[0];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                font.addLayer(new Layer(layerID));

            }

            case "LayerSetImage" -> {
                // Set an image for the layer.
                FontData _layerID = command.args()[0];
                FontData _imageID = command.args()[1];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                String imageID = ((FontString)_imageID).getData();

                Image image;
                try {
                    image = FileManager.openImageFromFilePath(dir + "\\res\\fonts\\" + imageID + ".png");
                } catch (IOException e) {
                    logger.error("", e);
                    image = null;
                }

                font.getLayer(layerID).setImage(image);

            }

            case "LayerSetAscent" -> {
                // Set an ascent for the layer.
                FontData _layerID = command.args()[0];
                FontData _ascent = command.args()[1];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                int ascent = ((FontInteger)_ascent).getData();
                font.getLayer(layerID).setAscent(ascent);

            }

            case "LayerSetCharWidths" -> {
                FontData _layerID = command.args()[0];
                FontData _charList = command.args()[1];
                FontData _widthList = command.args()[2];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                Layer layer = font.getLayer(layerID);

                FontList charsFontList;
                if (_charList instanceof FontKeyword fontKeyword)
                    charsFontList = (FontList)variables.get(fontKeyword.getKeyword());
                else if (_charList instanceof FontList fontList)
                    charsFontList = fontList;
                else {
                    logger.error("Invalid data type (" + _charList + "): expected Keyword or List");
                    break;
                }

                String[] chars;

                FontData[] charsFontData = charsFontList.getData();
                chars = new String[charsFontData.length];
                for (int i = 0; i < charsFontData.length; i++) chars[i] = ((FontString)charsFontData[i]).getData();

                FontList widthFontList;
                if (_widthList instanceof FontKeyword fontKeyword)
                    widthFontList = (FontList)variables.get(fontKeyword.getKeyword());
                else if (_widthList instanceof FontList fontList)
                    widthFontList = fontList;
                else {
                    logger.error("Invalid data type (" + _widthList + "): expected Keyword or List");
                    break;
                }

                int[] widths;

                FontData[] widthFontData = widthFontList.getData();
                widths = new int[widthFontData.length];
                for (int i = 0; i < widthFontData.length; i++) widths[i] = ((FontInteger)widthFontData[i]).getData();

                for (int i = 0; i < chars.length; i++) {
                    char c = chars[i].charAt(0); // string has to be 1 char long anyway so this is cool
                    int width = widths[i];
                    layer.mapWidth(c, width);
                }

            }

            case "LayerSetImageMap" -> {
                FontData _layerID = command.args()[0];
                FontData _charList = command.args()[1];
                FontData _rectList = command.args()[2];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                Layer layer = font.getLayer(layerID);

                FontList charsFontList;
                if (_charList instanceof FontKeyword fontKeyword)
                    charsFontList = (FontList)variables.get(fontKeyword.getKeyword());
                else if (_charList instanceof FontList fontList)
                    charsFontList = fontList;
                else {
                    logger.error("Invalid data type (" + _charList + "): expected Keyword or List");
                    break;
                }

                String[] chars;

                FontData[] charsFontData = charsFontList.getData();
                chars = new String[charsFontData.length];
                for (int i = 0; i < charsFontData.length; i++) chars[i] = ((FontString)charsFontData[i]).getData();

                FontList rectFontList;
                if (_rectList instanceof FontKeyword fontKeyword)
                    rectFontList = (FontList)variables.get(fontKeyword.getKeyword());
                else if (_rectList instanceof FontList fontList)
                    rectFontList = fontList;
                else {
                    logger.error("Invalid data type (" + _rectList + "): expected Keyword or List");
                    break;
                }

                int[][] rects;

                FontData[] rectFontData = rectFontList.getData();
                rects = new int[rectFontData.length][4];
                for (int i = 0; i < rectFontData.length; i++) {
                    FontList rectList = (FontList)rectFontData[i];
                    int[] rect = new int[4];
                    for (int j = 0; j < 4; j++) rect[j] = ((FontInteger)rectList.getData()[j]).getData();
                    rects[i] = rect;
                }

                Image image = layer.getImage();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

                for (int i = 0; i < chars.length; i++) {
                    char c = chars[i].charAt(0);

                    int[] rect = rects[i];

                    BufferedImage subImage = bufferedImage.getSubimage(rect[0], rect[1], rect[2], rect[3]);
                    BufferedImage newImage = new BufferedImage(subImage.getWidth(), subImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    newImage.getGraphics().drawImage(subImage, 0, 0, null);
                    Image fxImage = SwingFXUtils.toFXImage(newImage, null);
                    layer.mapImage(c, fxImage);
                }

            }

            case "LayerSetCharOffsets" -> {
                FontData _layerID = command.args()[0];
                FontData _charList = command.args()[1];
                FontData _offsetsList = command.args()[2];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                Layer layer = font.getLayer(layerID);

                FontList charsFontList;
                if (_charList instanceof FontKeyword fontKeyword)
                    charsFontList = (FontList)variables.get(fontKeyword.getKeyword());
                else if (_charList instanceof FontList fontList)
                    charsFontList = fontList;
                else {
                    logger.error("Invalid data type (" + _charList + "): expected Keyword or List");
                    break;
                }

                String[] chars;

                FontData[] charsFontData = charsFontList.getData();
                chars = new String[charsFontData.length];
                for (int i = 0; i < charsFontData.length; i++) chars[i] = ((FontString)charsFontData[i]).getData();

                FontList offsetsFontList;
                if (_offsetsList instanceof FontKeyword fontKeyword)
                    offsetsFontList = (FontList)variables.get(fontKeyword.getKeyword());
                else if (_offsetsList instanceof FontList fontList)
                    offsetsFontList = fontList;
                else {
                    logger.error("Invalid data type (" + _offsetsList + "): expected Keyword or List");
                    break;
                }

                int[][] offsets;

                FontData[] offsetsFontData = offsetsFontList.getData();
                offsets = new int[offsetsFontData.length][2];
                for (int i = 0; i < offsetsFontData.length; i++) {
                    FontList offsetList = (FontList)offsetsFontData[i];
                    int[] offset = new int[2];
                    for (int j = 0; j < 2; j++) offset[j] = ((FontInteger)offsetList.getData()[j]).getData();
                    offsets[i] = offset;
                }

                for (int i = 0; i < chars.length; i++) {
                    char c = chars[i].charAt(0); // string has to be 1 char long anyway so this is cool
                    int[] offset = offsets[i];
                    layer.mapOffset(c, offset);
                }

            }

            case "LayerSetKerningPairs" -> {
                FontData _layerID = command.args()[0];
                FontData _kerningStringsList = command.args()[1];
                FontData _kerningValuesList = command.args()[2];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                Layer layer = font.getLayer(layerID);

                FontList kerningStringsFontList;
                if (_kerningStringsList instanceof FontKeyword fontKeyword)
                    kerningStringsFontList = (FontList)variables.get(fontKeyword.getKeyword());
                else if (_kerningStringsList instanceof FontList fontList)
                    kerningStringsFontList = fontList;
                else {
                    logger.error("Invalid data type (" + _kerningStringsList + "): expected Keyword or List");
                    break;
                }

                String[] kerningStrings;

                FontData[] kerningStringsFontData = kerningStringsFontList.getData();
                kerningStrings = new String[kerningStringsFontData.length];
                for (int i = 0; i < kerningStringsFontData.length; i++) kerningStrings[i] = ((FontString)kerningStringsFontData[i]).getData();

                FontList kerningValuesFontList;
                if (_kerningValuesList instanceof FontKeyword fontKeyword)
                    kerningValuesFontList = (FontList)variables.get(fontKeyword.getKeyword());
                else if (_kerningValuesList instanceof FontList fontList)
                    kerningValuesFontList = fontList;
                else {
                    logger.error("Invalid data type (" + _kerningValuesList + "): expected Keyword or List");
                    break;
                }

                int[] kerningValues;

                FontData[] kerningValuesFontData = kerningValuesFontList.getData();
                kerningValues = new int[kerningValuesFontData.length];
                for (int i = 0; i < kerningValuesFontData.length; i++) {
                    int kerningValue = ((FontInteger)kerningValuesFontData[i]).getData();
                    kerningValues[i] = kerningValue;
                }

                for (int i = 0; i < kerningStrings.length; i++) {
                    String s = kerningStrings[i];
                    int value = kerningValues[i];
                    layer.mapKerning(s, value);
                }

            }

            case "LayerSetAscentPadding" -> {
                FontData _layerID = command.args()[0];
                FontData _ascentPadding = command.args()[1];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                int ascentPadding = ((FontInteger)_ascentPadding).getData();
                font.getLayer(layerID).setAscentPadding(ascentPadding);

            }

            case "LayerSetLineSpacingOffset" -> {
                FontData _layerID = command.args()[0];
                FontData _lineSpacingOffset = command.args()[1];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                int lineSpacingOffset = ((FontInteger)_lineSpacingOffset).getData();
                font.getLayer(layerID).setLineSpacingOffset(lineSpacingOffset);

            }

            case "LayerSetPointSize" -> {
                FontData _layerID = command.args()[0];
                FontData _pointSize = command.args()[1];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                int pointSize = ((FontInteger)_pointSize).getData();
                font.getLayer(layerID).setPointSize(pointSize);

            }

            case "LayerSetScale" -> {
                FontData _layerID = command.args()[0];
                FontData _scale = command.args()[1];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                double scale = ((FontDouble)_scale).getData();
                font.getLayer(layerID).setScale(scale);

            }

            case "SetDefaultPointSize" -> {
                FontData _defaultPointSize = command.args()[0];

                double defaultPointSize = ((FontInteger)_defaultPointSize).getData();
                font.setDefaultPointSize(defaultPointSize);

            }

            case "LayerSetSpacing" -> {
                FontData _layerID = command.args()[0];
                FontData _spacing = command.args()[1];

                String layerID = ((FontKeyword)_layerID).getKeyword();
                double spacing = ((FontDouble)_spacing).getData();
                font.getLayer(layerID).setSpacing(spacing);

            }

            default -> {
                logger.error("Unknown command: " + command.id());
                System.out.println(font.getName());
                System.out.println((int)command.id().charAt(0));
                System.out.println(command.id().length());
                System.out.println(Arrays.toString(command.id().toCharArray()));
            }

        }

        return font;

    }

}
