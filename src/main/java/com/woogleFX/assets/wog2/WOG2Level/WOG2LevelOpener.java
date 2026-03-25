package com.woogleFX.assets.wog2.WOG2Level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.AssetManager;
import com.woogleFX.file.FileManager;
import com.woogleFX.file.fileImport.ObjectGOOParser;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.level._2_Level;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Stack;

public class WOG2LevelOpener {

    public static WOG2Level openLevel(String levelName, GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        AssetManager.setAsset(null);

        System.out.println("Hi!");

        File file = new File(FileManager.getGameDir(version) + "/res/levels/" + levelName + ".wog2");

        String contents = Files.readString(file.toPath());
        _2_Level levelObject;

        System.out.println("Around the middle...");

        try {
            levelObject = ObjectGOOParser.read(_2_Level.class, contents, "level");
        } catch (Exception e) {
            throw new IOException("Failed to deserialize level " + levelName);
        }

        System.out.println("Around the middle...");

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


        System.out.println("Okay done.");
        // TODO2: resources and strings
        return new WOG2Level(levelObject, null, null);

    }

}
