package com.woogleFX.assets.wog1.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.engine.fx.editorButtons.FXEditorButtons;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.fx.menu.FXMenu;
import com.woogleFX.engine.fx.propertiesView.FXPropertiesView;
import com.woogleFX.file.fileImport.EditorObjectXMLReader;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.level.camera;
import com.worldOfGoo.level.level;
import com.worldOfGoo.level.levelexit;
import com.worldOfGoo.level.poi;
import com.worldOfGoo.resrc.ResourceManifest;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.scene.line;
import com.worldOfGoo.scene.linearforcefield;
import com.worldOfGoo.scene.scene;
import com.worldOfGoo.text.strings;

import java.io.File;

public class WOG1LevelOpener {

    public static WOG1Level openLevel(File file, GameVersion version) {

        String suffix = (version == GameVersion.VERSION_WOG1_OLD) ? ".bin" : "";

        File sceneF = new File( file.getPath() + "/" + file.getName() + ".scene" + suffix);
        scene sceneObject = EditorObjectXMLReader.readEditorObject(
                "com.worldOfGoo.scene", version, sceneF, scene.class);

        File levelF = new File( file.getPath() + "/" + file.getName() + ".level" + suffix);
        level levelObject = EditorObjectXMLReader.readEditorObject(
                "com.worldOfGoo.level", version, levelF, level.class);

        File resrcF = new File(file.getPath() + "/" + file.getName()  + ".resrc" + suffix);
        ResourceManifest resourceManifest = EditorObjectXMLReader.readEditorObject(
                "com.worldOfGoo.resrc", version, resrcF, ResourceManifest.class);
        if (resourceManifest == null) {
            // Create a new ResourceManifest and Resources
            resourceManifest = ObjectCreator.create(ResourceManifest.class, null, version);
            Resources resources = ObjectCreator.create(Resources.class, resourceManifest, version);
            resources.setAttribute("id", "scene_" + file.getName());
        }

        Resources resources = (Resources) resourceManifest.getChildren().get(0);

        // Create strings object (filled with stuff in WOG1Level constructor)
        strings strings = ObjectCreator.create(strings.class, null, version);

        return new WOG1Level(version, sceneObject, levelObject, resources, strings);

    }


    public static WOG1Level newLevel(String name, GameVersion version) {

        scene sceneObject = ObjectCreator.create(scene.class, null, version);
        level levelObject = ObjectCreator.create(level.class, null, version);
        Resources resourcesObject = ObjectCreator.create(Resources.class, null, version);
        strings stringsObject = ObjectCreator.create(strings.class, null, version);

        WOG1Level level = new WOG1Level(version, sceneObject, levelObject, resourcesObject, stringsObject);
        FXEditorButtons.updateAllButtons();
        FXMenu.updateAllButtons();

        sceneObject.setAttribute("backgroundcolor", "255,255,255");
        sceneObject.setAttribute("minx", "-500");
        sceneObject.setAttribute("miny", "0");
        sceneObject.setAttribute("maxx", "500");
        sceneObject.setAttribute("maxy", "1000");

        levelObject.setAttribute("ballsrequired", "1");
        levelObject.setAttribute("letterboxed", "false");
        levelObject.setAttribute("visualdebug", "false");
        levelObject.setAttribute("autobounds", "false");
        levelObject.setAttribute("textcolor", "255,255,255");
        levelObject.setAttribute("timebugprobability", "0");
        levelObject.setAttribute("strandgeom", "false");
        levelObject.setAttribute("allowskip", "true");

        resourcesObject.setAttribute("id", "scene_" + name);

        EditorObject linearForceField = ObjectCreator.create(linearforcefield.class, sceneObject, version);
        linearForceField.setAttribute("type", "gravity");
        linearForceField.setAttribute("force", "0,-10");
        linearForceField.setAttribute("dampeningfactor", "0");
        linearForceField.setAttribute("antigrav", "true");

        EditorObject lineRight = ObjectCreator.create(line.class, sceneObject, version);
        lineRight.setAttribute("id", "right");
        lineRight.setAttribute("anchor", "500,300");
        lineRight.setAttribute("normal", "-1,0");
        lineRight.setAttribute("tag", "detaching");

        EditorObject lineLeft = ObjectCreator.create(line.class, sceneObject, version);
        lineLeft.setAttribute("id", "left");
        lineLeft.setAttribute("anchor", "-500,300");
        lineLeft.setAttribute("normal", "1,0");
        lineLeft.setAttribute("tag", "detaching");

        EditorObject lineGround = ObjectCreator.create(line.class, sceneObject, version);
        lineGround.setAttribute("id", "ground");
        lineGround.setAttribute("anchor", "0,20");
        lineGround.setAttribute("normal", "0,1");

        EditorObject cameraNormal = ObjectCreator.create(camera.class, levelObject, version);
        cameraNormal.setAttribute("aspect", "normal");
        cameraNormal.setAttribute("endpos", "0,0");
        cameraNormal.setAttribute("endzoom", "1");

        EditorObject poiNormal = ObjectCreator.create(poi.class, cameraNormal, version);
        poiNormal.setAttribute("pos", "0,0");
        poiNormal.setAttribute("zoom", "1");
        poiNormal.setAttribute("pause", "0");
        poiNormal.setAttribute("traveltime", "0");

        EditorObject cameraWidescreen = ObjectCreator.create(camera.class, levelObject, version);
        cameraWidescreen.setAttribute("aspect", "widescreen");
        cameraWidescreen.setAttribute("endpos", "0,0");
        cameraWidescreen.setAttribute("endzoom", "1");

        EditorObject poiWidescreen = ObjectCreator.create(poi.class, cameraWidescreen, version);
        poiWidescreen.setAttribute("pos", "0,0");
        poiWidescreen.setAttribute("zoom", "1");
        poiWidescreen.setAttribute("pause", "0");
        poiWidescreen.setAttribute("traveltime", "0");

        EditorObject levelExit = ObjectCreator.create(levelexit.class, levelObject, version);
        levelExit.setAttribute("id", "theExit");
        levelExit.setAttribute("pos", "0,0");
        levelExit.setAttribute("radius", "75");

        // Put everything in the hierarchy
        sceneObject.getTreeItem().setExpanded(true);
        FXHierarchy.getHierarchy().setRoot(sceneObject.getTreeItem());

        // Add items from the Scene to it
        FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(new EditorObject[]{sceneObject}));

        return level;

    }

}
