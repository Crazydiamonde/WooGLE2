package com.woogleFX.assets.wog2.WOG2Ball;

import com.woogleFX.assets.GameVersion;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileImport.ObjectGOOParser;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo2.ball.Ball;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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

        ArrayList<EditorObject> resources = new ArrayList<>();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
        try {
            saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            return null;
        }

        /*

        DefaultXmlOpener defaultHandler = new DefaultXmlOpener(objects, resources, GameVersion.VERSION_WOG2);
        defaultHandler.setPrefix("com.worldOfGoo.resrc");

        File ballFileR = new File(wog2dir + "/res/balls/" + ballName + "/resources.xml");
        defaultHandler.setMode(Mode.RESOURCE);
        try {
            saxParser.parse(ballFileR, defaultHandler);
        } catch (SAXException | IOException e) {
            return null;
        }

        ArrayList<EditorObject> addin = new ArrayList<>();

        File addinF = new File(wog2dir + "/res/balls/" + ballName + ".addin.xml");
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
        return new WOG2Ball(objects, (Resources) resources.get(0), null);

    }

}
