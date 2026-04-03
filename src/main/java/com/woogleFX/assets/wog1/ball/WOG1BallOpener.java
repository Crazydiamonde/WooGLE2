package com.woogleFX.assets.wog1.ball;

import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.file.FileManager;
import com.woogleFX.assets.GameVersion;
import com.woogleFX.file.fileImport.EditorObjectXMLReader;
import com.worldOfGoo.ball.ball;
import com.worldOfGoo.resrc.ResourceManifest;
import com.worldOfGoo.resrc.Resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class WOG1BallOpener {

    public static WOG1Ball openBall(File file, GameVersion version) throws IOException {

        // Make sure a ball from an invalid version isn't being opened (possible because of properties.xml)
        if (FileManager.getGameDir(version).isEmpty()) return null;

        String suffix = (version == GameVersion.VERSION_WOG1_OLD) ? ".xml.bin" : ".xml";
        File ballFile = new File(file.toPath() + "/balls" + suffix);
        File ballFileR = new File(file.toPath() + "/resources" + suffix);

        // Make sure the ball file is actually there
        if (!Files.exists(ballFile.toPath())) return null;

        ball ball = EditorObjectXMLReader.readEditorObject(
                version, ballFile, ball.class);
        ResourceManifest resourceManifest = EditorObjectXMLReader.readEditorObject(
                version, ballFileR, ResourceManifest.class);
        Resources resources = (Resources) resourceManifest.getChildren().get(0);

        return new WOG1Ball(version, ball, resources);

    }


    public static WOG1Ball newBall(String ballName, GameVersion version) {

        ball ball = ObjectCreator.create(ball.class, null, version);
        ball.setAttribute("name", ballName);

        Resources resources = ObjectCreator.create(Resources.class, null, version);
        resources.setAttribute("id", "ball_" + ballName);

        return new WOG1Ball(version, ball, resources);

    }

}
