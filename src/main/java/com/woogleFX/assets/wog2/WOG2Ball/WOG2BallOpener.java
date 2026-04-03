package com.woogleFX.assets.wog2.WOG2Ball;

import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileImport.EditorObjectXMLReader;
import com.woogleFX.file.fileImport.ObjectGOOParser;
import com.worldOfGoo.resrc.ResourceManifest;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo2.ball.Ball;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Stack;

public class WOG2BallOpener {

    public static WOG2Ball open2Ball(String ballName, GameVersion version) {

        String contents;
        try {
            contents = Files.readString(Path.of(FileManager.getGameDir(version) + "/res/balls/" + ballName + "/ball.wog2"));
        } catch (IOException e) {
            ErrorAlarm.show(e);
            return null;
        }

        EditorObject levelObject = ObjectGOOParser.read(Ball.class, contents, "ball");
        ArrayList<EditorObject> objects = new ArrayList<>();
        Stack<EditorObject> toAdd = new Stack<>();
        toAdd.push(levelObject);
        while (!toAdd.isEmpty()) {
            EditorObject thisObject = toAdd.pop();
            objects.add(thisObject);
            for (EditorObject child : thisObject.getChildren()) {
                toAdd.add(0, child);
            }

        }

        // load resources
        Resources resources;
        File ballFileR = new File(FileManager.getGameDir(GameVersion.VERSION_WOG2)
                + "/res/balls/" + ballName + "/resources.xml");
        try {
            ResourceManifest resourceManifest = EditorObjectXMLReader.readEditorObject(
                    GameVersion.VERSION_WOG2, ballFileR, ResourceManifest.class);
            assert resourceManifest != null;
            resources = (Resources) resourceManifest.getChildren().get(0);
        } catch (IOException e) {
            // display error to user and abort
            ErrorAlarm.show(e);
            return null;
        }

        // load addin
        // TODO: implement this
        /*ArrayList<EditorObject> addin = new ArrayList<>();

        File addinF = new File(FileManager.getGameDir(GameVersion.VERSION_WOG2)
                + "/res/balls/" + ballName + ".addin.xml");
        if (addinF.exists()) {
            try {
                saxParser.parse(addinF, defaultHandler);
            } catch (SAXException | IOException e) {
                return null;
            }
        }
        else supremeAddToList(addin, BlankObjectGenerator.generateBlankAddinObject(ballName, version));
        */
        // TODO2:
        return new WOG2Ball(objects, resources, null);

    }

}
